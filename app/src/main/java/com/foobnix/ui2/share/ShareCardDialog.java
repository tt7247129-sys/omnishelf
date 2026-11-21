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
import com.foobnix.pdf.info.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Previews a quote card and lets the reader flip between the six layouts before sharing.
 *
 * <p>The card is rendered on the spot; nothing is uploaded. Sharing writes a PNG into the
 * app's own files/share directory and hands a FileProvider URI to the system chooser.
 */
public class ShareCardDialog {

    private static Dialog dialog;

    public static void show(final Activity activity, String quote, String title, String author) {
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
            render(preview, texts, current[0], label);

            dialog.findViewById(R.id.cardPrev).setOnClickListener(v -> {
                current[0] = (current[0] + ShareQuoteCard.TEMPLATE_COUNT - 1) % ShareQuoteCard.TEMPLATE_COUNT;
                render(preview, texts, current[0], label);
            });
            dialog.findViewById(R.id.cardNext).setOnClickListener(v -> {
                current[0] = (current[0] + 1) % ShareQuoteCard.TEMPLATE_COUNT;
                render(preview, texts, current[0], label);
            });
            share.setOnClickListener(v -> {
                rememberTemplate(current[0]);
                shareBitmap(activity, texts, current[0]);
                dismiss();
            });
            cancel.setOnClickListener(v -> dismiss());
            dialog.show();
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

    private static void render(ImageView preview, ShareQuoteCard.Texts texts, int template,
                               TextView label) {
        Bitmap card = ShareQuoteCard.create(preview.getContext(), texts, template, null).render();
        preview.setImageBitmap(card);
        label.setText((template + 1) + " / " + ShareQuoteCard.TEMPLATE_COUNT);
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

    private static void shareBitmap(Activity activity, ShareQuoteCard.Texts texts, int template) {
        try {
            Bitmap card = ShareQuoteCard.create(activity, texts, template, null).render();
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
        String lang = Locale.getDefault().getLanguage();
        boolean cjk = "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
        String pattern = cjk ? "yyyy 年 M 月 d 日" : "MMM d, yyyy";
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }
}
