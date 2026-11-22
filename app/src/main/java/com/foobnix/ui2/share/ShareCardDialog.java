package com.foobnix.ui2.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.foobnix.android.utils.Apps;
import com.foobnix.android.utils.LOG;
import com.foobnix.model.AppProfile;
import com.foobnix.model.AppState;
import com.foobnix.pdf.info.IMG;
import com.foobnix.pdf.info.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Previews a quote card and lets the reader flip between the six layouts before sharing.
 *
 * <p>The card is rendered on the spot; nothing is uploaded. The book's own cover is loaded
 * for the two layouts that feature one, and every layout degrades to a cinnabar plate when
 * there is no cover, which is the behaviour the approved designs describe.
 */
public class ShareCardDialog {

    private static Dialog dialog;

    public static void show(final Activity activity, String quote, final String title,
                            final String author, final String bookPath) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final String text = quote == null ? "" : quote.trim();
        if (text.length() == 0) {
            return;
        }
        try {
            final ShareQuoteCard.Texts texts = new ShareQuoteCard.Texts();
            // No cut-off: the renderer fits the whole selection, however long it is.
            texts.quote = text;
            texts.title = title == null ? "" : title;
            texts.author = author == null ? "" : author;
            texts.chapter = "";
            texts.intro = activity.getString(R.string.share_card_intro);
            texts.scan = activity.getString(R.string.share_card_scan);
            texts.url = activity.getString(R.string.share_card_url);
            texts.urlLabel = texts.url.replace("https://", "").replace("http://", "").replace("/", "");
            ShareQuoteCard.fillDates(texts);

            dialog = new Dialog(activity, R.style.DialogRoundLight);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_share_card);
            dialog.setCanceledOnTouchOutside(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            final ImageView preview = dialog.findViewById(R.id.cardPreview);
            final TextView label = dialog.findViewById(R.id.cardTemplate);
            final Button share = dialog.findViewById(R.id.cardShare);
            final Button saveToGallery = dialog.findViewById(R.id.cardSave);
            final Button cancel = dialog.findViewById(R.id.cardCancel);

            int start = AppState.get().shareCardTemplate;
            if (start < 0 || start >= ShareQuoteCard.TEMPLATE_COUNT) {
                start = ShareQuoteCard.T_DATE_PAPER;
            }
            final int[] current = {start};
            final Bitmap[] cover = {null};
            render(preview, texts, current[0], label, cover[0]);

            dialog.findViewById(R.id.cardPrev).setOnClickListener(v -> {
                current[0] = (current[0] + ShareQuoteCard.TEMPLATE_COUNT - 1) % ShareQuoteCard.TEMPLATE_COUNT;
                render(preview, texts, current[0], label, cover[0]);
            });
            dialog.findViewById(R.id.cardNext).setOnClickListener(v -> {
                current[0] = (current[0] + 1) % ShareQuoteCard.TEMPLATE_COUNT;
                render(preview, texts, current[0], label, cover[0]);
            });
            share.setOnClickListener(v -> {
                rememberTemplate(current[0]);
                shareBitmap(activity, texts, current[0], cover[0]);
                dismiss();
            });
            saveToGallery.setOnClickListener(v -> {
                rememberTemplate(current[0]);
                Bitmap card = ShareQuoteCard.create(activity, texts, current[0], cover[0]).render();
                if (saveToAlbum(activity, card)) {
                    Toast.makeText(activity, R.string.success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, R.string.fail, Toast.LENGTH_SHORT).show();
                }
            });
            cancel.setOnClickListener(v -> dismiss());
            dialog.show();

            if (bookPath != null && bookPath.length() > 0) {
                loadCover(activity, bookPath, bitmap -> {
                    if (bitmap == null) {
                        return;
                    }
                    cover[0] = bitmap;
                    if (dialog != null && dialog.isShowing()) {
                        render(preview, texts, current[0], label, bitmap);
                    }
                });
            }
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

    private static void loadCover(final Activity activity, final String bookPath,
                                  final IMG.ResourceReady ready) {
        // Two attempts. The image loader knows the app's own pipeline; when it has nothing
        // for this file (a hand-made EPUB is enough to confuse it), decode the archive here.
        try {
            IMG.getCoverPageWithEffect(activity, bookPath, bitmap -> {
                if (bitmap != null) {
                    LOG.d("ShareCard cover for", bookPath, bitmap.getWidth() + "x" + bitmap.getHeight());
                    runOnUi(activity, ready, bitmap);
                    return;
                }
                LOG.d("ShareCard cover for", bookPath, "loader empty, trying the archive");
                loadCoverFromArchive(activity, bookPath, ready);
            }).submit();
        } catch (Throwable e) {
            LOG.e(e);
            loadCoverFromArchive(activity, bookPath, ready);
        }
    }

    /** EPUB and CBZ are ZIP archives: the cover is simply the first sensible image inside. */
    private static void loadCoverFromArchive(final Activity activity, final String bookPath,
                                             final IMG.ResourceReady ready) {
        new Thread(() -> {
            Bitmap bitmap = null;
            try {
                java.util.zip.ZipFile zip = new java.util.zip.ZipFile(bookPath);
                java.util.zip.ZipEntry chosen = null;
                java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    java.util.zip.ZipEntry entry = entries.nextElement();
                    String name = entry.getName().toLowerCase(Locale.ROOT);
                    if (entry.isDirectory()) continue;
                    boolean image = name.endsWith(".jpg") || name.endsWith(".jpeg")
                            || name.endsWith(".png") || name.endsWith(".webp");
                    if (!image) continue;
                    if (chosen == null) chosen = entry;
                    if (name.contains("cover")) { chosen = entry; break; }
                }
                if (chosen != null) {
                    java.io.InputStream in = zip.getInputStream(chosen);
                    byte[] bytes = readAll(in);
                    in.close();
                    if (bytes != null && bytes.length > 0) {
                        android.graphics.BitmapFactory.Options opts = new android.graphics.BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;
                        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                        int sample = 1;
                        while (opts.outWidth / sample > 900) sample *= 2;
                        opts.inJustDecodeBounds = false;
                        opts.inSampleSize = sample;
                        bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                    }
                }
                zip.close();
            } catch (Throwable e) {
                LOG.d("ShareCard cover archive", String.valueOf(e.getMessage()));
            }
            LOG.d("ShareCard cover archive", bookPath, bitmap == null ? "none" : bitmap.getWidth() + "x" + bitmap.getHeight());
            if (bitmap != null) {
                runOnUi(activity, ready, bitmap);
            }
        }).start();
    }

    private static byte[] readAll(java.io.InputStream in) throws java.io.IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    private static void runOnUi(final Activity activity, final IMG.ResourceReady ready,
                                final Bitmap bitmap) {
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing()) {
                ready.onResourceReady(bitmap);
            }
        });
    }

    private static void render(ImageView preview, ShareQuoteCard.Texts texts, int template,
                               TextView label, Bitmap cover) {
        Bitmap card = ShareQuoteCard.create(preview.getContext(), texts, template, cover).render();
        preview.setImageBitmap(card);
        label.setText((template + 1) + " / " + ShareQuoteCard.TEMPLATE_COUNT);
        exportForVerification(preview.getContext(), card, template);
    }

    /**
     * Writes the preview into the app's external files directory, so a verification run can
     * pull the exact bitmap the device produced instead of judging a scaled screenshot.
     */
    private static void exportForVerification(android.content.Context context, Bitmap card, int template) {
        try {
            File dir = context.getExternalFilesDir("cards");
            if (dir == null) {
                return;
            }
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            File out = new File(dir, "card-" + (template + 1) + ".png");
            try (FileOutputStream stream = new FileOutputStream(out)) {
                card.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

    private static void rememberTemplate(int template) {
        try {
            AppState.get().shareCardTemplate = template;
            if (AppProfile.syncState != null) {
                com.foobnix.android.utils.IO.writeObjSync(AppProfile.syncState, AppState.get());
            }
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

    /**
     * Writes the card into the phone's gallery.
     *
     * <p>On Android 10 and later this uses MediaStore with a relative path, which needs no
     * permission at all and puts the file in Pictures/OmniShelf. Older releases insert through
     * MediaStore's data column and then ask the media scanner to index the file; the write
     * permission is already declared for those versions.
     */
    private static boolean saveToAlbum(Activity activity, Bitmap card) {
        String name = "omnishelf-quote-" + System.currentTimeMillis() + ".png";
        ContentResolver resolver = activity.getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/OmniShelf");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            Uri uri = null;
            try {
                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) return false;
                try (OutputStream out = resolver.openOutputStream(uri)) {
                    if (out == null || !card.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        return false;
                    }
                }
                ContentValues done = new ContentValues();
                done.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(uri, done, null, null);
                return true;
            } catch (Throwable e) {
                LOG.e(e);
                if (uri != null) {
                    try {
                        resolver.delete(uri, null, null);
                    } catch (Throwable ignored) {
                    }
                }
                return false;
            }
        }
        // Android 7-9: write into the public Pictures folder, then have it indexed.
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "OmniShelf");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            File file = new File(dir, name);
            try (FileOutputStream out = new FileOutputStream(file)) {
                if (!card.compress(Bitmap.CompressFormat.PNG, 100, out)) return false;
            }
            MediaScannerConnection.scanFile(activity, new String[]{file.getAbsolutePath()},
                    new String[]{"image/png"}, null);
            return true;
        } catch (Throwable e) {
            LOG.e(e);
            return false;
        }
    }

    private static void shareBitmap(Activity activity, ShareQuoteCard.Texts texts, int template,
                                    Bitmap cover) {
        try {
            Bitmap card = ShareQuoteCard.create(activity, texts, template, cover).render();
            File dir = new File(activity.getFilesDir(), "share");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            ShareQuoteCard.pruneOldCards(dir);
            File file = new File(dir, "quote-" + System.currentTimeMillis() + ".png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                card.compress(Bitmap.CompressFormat.PNG, 95, out);
            }
            card.recycle();
            Uri uri = FileProvider.getUriForFile(activity,
                    Apps.getPackageName(activity) + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, texts.quote);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, null));
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

    private static void dismiss() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Throwable ignored) {
        }
        dialog = null;
    }

}
