package com.foobnix.ui2.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Renders a quote card: the selection, the book, and a line pointing at the project.
 *
 * <p>Three styles, chosen to match the reading theme the card was made in. Nothing is sent
 * anywhere here - the caller owns the bitmap and decides what to do with it.
 */
public class ShareQuoteCard {

    public static final int STYLE_CINNABAR = 0;
    public static final int STYLE_PAPER = 1;
    public static final int STYLE_NIGHT = 2;

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 1440;
    private static final int MARGIN = 110;

    /** Renders the card. Never returns null; falls back to a plain card on any failure. */
    public static Bitmap render(Context context, String quote, String title, String author,
                                String page, int style) {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        try {
            int background;
            int foreground;
            int accent;
            if (style == STYLE_PAPER) {
                background = Color.parseColor("#FFFDF8");
                foreground = Color.parseColor("#2A1B18");
                accent = Color.parseColor("#C83A2A");
            } else if (style == STYLE_NIGHT) {
                background = Color.parseColor("#141414");
                foreground = Color.parseColor("#F2E7E2");
                accent = Color.parseColor("#C83A2A");
            } else {
                background = Color.parseColor("#8E2B21");
                foreground = Color.parseColor("#FFF6F2");
                accent = Color.parseColor("#F0B48A");
            }
            canvas.drawColor(background);

            String body = quote == null ? "" : quote.trim();
            if (body.length() > 220) {
                body = body.substring(0, 219) + "…";
            }

            TextPaint quotePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            quotePaint.setColor(foreground);
            quotePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            quotePaint.setTextSize(fitTextSize(body, MARGIN, WIDTH - MARGIN * 2, 60));
            quotePaint.setSubpixelText(true);

            StaticLayout layout = buildLayout(body, quotePaint, WIDTH - MARGIN * 2);
            int textTop = Math.max(220, (HEIGHT - layout.getHeight()) / 2 - 120);
            canvas.save();
            canvas.translate(MARGIN, textTop);
            layout.draw(canvas);
            canvas.restore();

            int ruleTop = textTop + layout.getHeight() + 46;
            Paint rule = new Paint(Paint.ANTI_ALIAS_FLAG);
            rule.setColor(accent);
            canvas.drawRect(MARGIN, ruleTop, MARGIN + 130, ruleTop + 6, rule);

            TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            titlePaint.setColor(foreground);
            titlePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            titlePaint.setTextSize(40);
            String safeTitle = title == null ? "" : title;
            if (safeTitle.length() > 40) {
                safeTitle = safeTitle.substring(0, 39) + "…";
            }
            canvas.drawText(safeTitle, MARGIN, ruleTop + 92, titlePaint);

            TextPaint metaPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            metaPaint.setColor(accent);
            metaPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            metaPaint.setTextSize(32);
            StringBuilder meta = new StringBuilder();
            if (author != null && author.length() > 0) {
                meta.append(author);
            }
            if (page != null && page.length() > 0) {
                if (meta.length() > 0) {
                    meta.append("  ·  ");
                }
                meta.append("p. ").append(page);
            }
            if (meta.length() > 0) {
                canvas.drawText(meta.toString(), MARGIN, ruleTop + 150, metaPaint);
            }

            TextPaint brandPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            brandPaint.setColor(style == STYLE_PAPER ? Color.parseColor("#7A6A63") : foreground);
            brandPaint.setTextSize(30);
            brandPaint.setAlpha(220);
            canvas.drawText("OmniShelf  ·  omnishelf.cc", MARGIN, HEIGHT - 110, brandPaint);
        } catch (Throwable ignored) {
            // A card that cannot be laid out is still a card: the caller gets the background.
        }
        return bitmap;
    }

    /** Single line when it fits, otherwise the largest size down to 34sp that fits three lines. */
    private static float fitTextSize(String text, int left, int width, float startSize) {
        float size = startSize;
        while (size > 34f) {
            TextPaint probe = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            probe.setTextSize(size);
            StaticLayout layout = buildLayout(text, probe, width);
            if (layout.getLineCount() <= 8) {
                return size;
            }
            size -= 4f;
        }
        return 34f;
    }

    private static StaticLayout buildLayout(String text, TextPaint paint, int width) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            return StaticLayout.Builder.obtain(text, 0, text.length(), paint, width)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(14f, 1f)
                    .setIncludePad(false)
                    .build();
        }
        return new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 14f, false);
    }
}
