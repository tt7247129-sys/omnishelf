package com.foobnix.ui2.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.foobnix.android.utils.Apps;
import com.foobnix.android.utils.LOG;
import com.foobnix.model.AppProfile;
import com.foobnix.model.AppState;
import com.foobnix.pdf.info.IMG;
import com.foobnix.pdf.info.R;

import java.io.File;
import java.io.FileOutputStream;
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
            texts.quote = text.length() > 220 ? text.substring(0, 219) + "…" : text;
            texts.title = title == null ? "" : title;
            texts.author = author == null ? "" : author;
            texts.chapter = "";
            texts.intro = activity.getString(R.string.share_card_intro);
            texts.scan = activity.getString(R.string.share_card_scan);
            texts.url = activity.getString(R.string.share_card_url);
            texts.urlLabel = texts.url.replace("https://", "").replace("http://", "").replace("/", "");
            texts.dateLine = dateLine();
            texts.verticalDate = verticalDate();

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
            final Button cancel = dialog.findViewById(R.id.cardCancel);

            int start = AppState.get().shareCardTemplate;
            if (start < 0 || start >= ShareQuoteCard.TEMPLATE_COUNT) {
                start = ShareQuoteCard.T_COVER_TICKET_NIGHT;
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

    private static void loadCover(Activity activity, String bookPath, IMG.ResourceReady ready) {
        try {
            IMG.getCoverPageWithEffect(activity, bookPath, bitmap -> {
                LOG.d("ShareCard cover for", bookPath, bitmap == null ? "none" : bitmap.getWidth() + "x" + bitmap.getHeight());
                if (bitmap == null) {
                    return;
                }
                if (activity.isFinishing()) {
                    return;
                }
                activity.runOnUiThread(() -> ready.onResourceReady(bitmap));
            });
        } catch (Throwable e) {
            LOG.e(e);
        }
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

    /** Date in the reader's own convention: 2026 年 11 月 21 日, or Nov 21, 2026. */
    private static String dateLine() {
        String pattern = cjk() ? "yyyy 年 M 月 d 日" : "MMM d, yyyy";
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    /** The same date as a column of characters: 二〇二六年十一月二十一日. */
    private static String verticalDate() {
        if (!cjk()) {
            return new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        }
        String digits = "〇一二三四五六七八九";
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat month = new SimpleDateFormat("M", Locale.getDefault());
        SimpleDateFormat day = new SimpleDateFormat("d", Locale.getDefault());
        StringBuilder out = new StringBuilder();
        for (char ch : year.format(new Date()).toCharArray()) {
            out.append(digits.charAt(ch - '0'));
        }
        out.append("年");
        out.append(numberWords(Integer.parseInt(month.format(new Date()))));
        out.append("月");
        out.append(numberWords(Integer.parseInt(day.format(new Date()))));
        out.append("日");
        return out.toString();
    }

    /** 1..31 as Chinese numerals, enough for a date column. */
    private static String numberWords(int n) {
        String digits = "〇一二三四五六七八九";
        if (n < 10) {
            return String.valueOf(digits.charAt(n));
        }
        if (n == 10) {
            return "十";
        }
        if (n < 20) {
            return "十" + digits.charAt(n - 10);
        }
        int tens = n / 10;
        int ones = n % 10;
        return digits.charAt(tens) + "十" + (ones == 0 ? "" : String.valueOf(digits.charAt(ones)));
    }

    private static boolean cjk() {
        String lang = Locale.getDefault().getLanguage();
        return "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
    }
}
