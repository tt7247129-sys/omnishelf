package com.foobnix.ui2.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Renders a quote card: the selection, the book, and a line pointing at the project.
 *
 * <p>Six layouts, all drawn from the app's own palette. The card never leaves the device by
 * itself - the caller owns the bitmap and decides where it goes.
 *
 * <p>Everything here is android.* plus ZXing, so the class can be compiled and reasoned
 * about on its own; the strings are passed in, never read from resources, which keeps the
 * renderer free of the app's R class.
 */
public class ShareQuoteCard {

    // The six templates, in the order the user flips through them.
    public static final int T_DATE_PAPER = 0;
    public static final int T_COVER_TICKET_NIGHT = 1;
    public static final int T_VERTICAL_SPINE_NIGHT = 2;
    public static final int T_COVER_FULL_PAPER = 3;
    public static final int T_CINNABAR_FRAME_PAPER = 4;
    public static final int T_GALLERY_NIGHT = 5;
    public static final int TEMPLATE_COUNT = 6;

    private static final int W = 1080;
    private static final int H = 1440;
    private static final int M = 84;

    private static final int PAPER = 0xFFFBF8F2;
    private static final int INK = 0xFF241815;
    private static final int MUTED = 0xFF7C6B63;
    private static final int NIGHT = 0xFF151412;
    private static final int NIGHT_TEXT = 0xFFEFE4DC;
    private static final int NIGHT_MUTED = 0xFFA3938B;
    private static final int CINNABAR = 0xFF8E2B21;
    private static final int ACCENT = 0xFFD2503C;
    private static final int HAIR_DARK = 0xFF392D28;
    private static final int HAIR_LIGHT = 0xFFD8CBBE;

    /** The text a card needs. Built by the caller from resources, so it is localized. */
    public static class Texts {
        public String quote = "";
        public String title = "";
        public String author = "";
        public String chapter = "";
        /** e.g. "OmniShelf：一个不给你看广告的阅读器，34 种格式、离线朗读、55 种语言。" */
        public String intro = "";
        /** e.g. "扫码 · 读同款" */
        public String scan = "";
        /** e.g. "https://omnishelf.cc" - the QR target */
        public String url = "https://omnishelf.cc";
        public String urlLabel = "omnishelf.cc";
        /** Pre-formatted date, localized by the caller: "2026 年 11 月 21 日" or "Nov 21, 2026". */
        public String dateLine = "";
    }

    private final Context context;
    private final Texts texts;
    private final int template;
    private final Bitmap cover;

    private Typeface serif;
    private Typeface sans;
    private Bitmap noise;
    private Bitmap qr;
    private boolean qrDark;

    private ShareQuoteCard(Context context, Texts texts, int template, Bitmap cover) {
        this.context = context;
        this.texts = texts;
        this.template = template;
        this.cover = cover;
    }

    public static ShareQuoteCard create(Context context, Texts texts, int template, Bitmap cover) {
        return new ShareQuoteCard(context, texts, template, cover);
    }

    /** Renders the card. Never returns null: on failure it returns the plain background. */
    public Bitmap render() {
        Bitmap bitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        try {
            serif = load("fonts/NotoSerifSC-OmniShelf.ttf");
            sans = Typeface.SANS_SERIF;
            switch (template) {
                case T_COVER_TICKET_NIGHT: dateCoverTicket(canvas, bitmap); break;
                case T_VERTICAL_SPINE_NIGHT: verticalSpine(canvas, bitmap); break;
                case T_COVER_FULL_PAPER: coverFullPaper(canvas, bitmap); break;
                case T_CINNABAR_FRAME_PAPER: cinnabarFrame(canvas, bitmap); break;
                case T_GALLERY_NIGHT: gallery(canvas, bitmap); break;
                case T_DATE_PAPER:
                default: datePaper(canvas, bitmap); break;
            }
        } catch (Throwable e) {
            // A card that cannot be laid out is still a card, but the reason must be visible
            // in logcat: a silently blank card is worse than a crash.
            android.util.Log.e("ShareQuoteCard", "render failed for template " + template, e);
        }
        return bitmap;
    }


    /**
     * Deletes cards older than a week. They live in filesDir/share, one PNG per share, and
     * without this the directory only grows.
     */
    public static void pruneOldCards(java.io.File dir) {
        if (dir == null || !dir.isDirectory()) {
            return;
        }
        java.io.File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        long cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        for (java.io.File f : files) {
            if (f != null && f.isFile() && f.lastModified() < cutoff) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
    }

    // ------------------------------------------------------------------ templates

    private void datePaper(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        Paint p = paint(INK);
        text(c, monthLabel(), M, 128, 22, sans, MUTED);
        rect(c, M, 96, M + 46, 100, CINNABAR);
        text(c, day(), M, 300, 200, Typeface.create(sans, Typeface.BOLD), INK);
        text(c, year(), M + 2, 400, 58, sans, INK);
        text(c, texts.dateLine, M + 2, 470, 26, serif, MUTED);
        line(c, M + 2, 512, W - M, 512, HAIR_LIGHT, 1);

        TextPaint quote = quotePaint(INK, 48);
        float y = 552;
        for (String line : wrap(quote, texts.quote, W - 2 * M - 40)) {
            c.drawText(line, M + 2, y, quote);
            y += 80;
        }
        y += 32;
        text(c, quoted(texts.title), M + 2, y, 30, serif, INK);
        text(c, join(texts.author, " · ", texts.chapter), M + 2, y + 44, 22, serif, MUTED);
        seal(c, W - M - 84, y - 26, 84, "書");
        stub(c, b, false, M, new String[]{"paper", "paper"});
        grain(b, 0.05f);
    }

    private void dateCoverTicket(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        int left = 132, top = 118, right = W - 132, bottom = 704;
        if (cover != null) {
            c.drawBitmap(cover, centerCrop(left, top, right, bottom), coverRect(left, top, right, bottom),
                    new Paint(Paint.FILTER_BITMAP_FLAG));
        } else {
            gradientPlate(c, left, top, right, bottom);
        }
        // a thin keyline inside the plate, like a printed cover plate
        rectStroke(c, left + 14, top + 14, right - 14, bottom - 14, 0xFFF6E2D8, 1);
        seal(c, left - 40, top - 40, 80, "書");
        text(c, texts.title, left + 24, top + 56, 28, serif, 0xFFF6E2D8);
        text(c, texts.dateLine, M, 742, 22, serif, NIGHT_MUTED);

        TextPaint quote = quotePaint(NIGHT_TEXT, 46);
        String[] lines = wrap(quote, texts.quote, W - 2 * M);
        float blockTop = 800, blockBottom = H - 380;
        float y = blockTop + Math.max(0, (blockBottom - blockTop - lines.length * 78 - 60) / 2f);
        for (String line : lines) {
            c.drawText(line, M, y, quote);
            y += 78;
        }
        text(c, "/ " + join(texts.title, " · ", texts.chapter), M, y + 20, 24, serif, NIGHT_MUTED);
        stub(c, b, true, M, new String[]{"night", "night"});
        vignette(b);
        grain(b, 0.05f);
    }

    private void verticalSpine(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        int spineW = 176;
        Paint spine = new Paint(Paint.ANTI_ALIAS_FLAG);
        spine.setShader(new LinearGradient(0, 0, spineW, 0, 0xFF7A241B, 0xFF9C3025, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, spineW, H, spine);

        float y = 150;
        for (char ch : texts.title.toCharArray()) {
            text(c, String.valueOf(ch), 54, y + 60, 74, serif, 0xFFF7E3D9);
            y += 88;
        }
        float ay = H - 360;
        for (char ch : texts.author.toCharArray()) {
            text(c, String.valueOf(ch), 54, ay, 22, serif, 0xFFE4C2B4);
            ay += 32;
        }
        int rx = W - 120;
        float dy = 120;
        for (char ch : verticalDate().toCharArray()) {
            text(c, String.valueOf(ch), rx, dy + 24, 24, serif, NIGHT_MUTED);
            dy += 34;
        }
        line(c, rx - 40, 120, rx - 40, 700, HAIR_DARK, 1);

        TextPaint quote = quotePaint(NIGHT_TEXT, 44);
        float ty = 300;
        for (String line : wrap(quote, texts.quote, (rx - 60) - (spineW + 70))) {
            c.drawText(line, spineW + 70, ty, quote);
            ty += 78;
        }
        text(c, "/ " + texts.chapter, spineW + 70, ty + 24, 24, serif, NIGHT_MUTED);
        seal(c, spineW + 70, ty + 68, 74, "書");
        stub(c, b, true, spineW + 40, new String[]{"spine", "night"});
        vignette(b);
        grain(b, 0.05f);
    }

    private void coverFullPaper(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        int bandH = 620;
        if (cover != null) {
            c.drawBitmap(cover, centerCrop(0, 0, W, bandH), coverRect(0, 0, W, bandH),
                    new Paint(Paint.FILTER_BITMAP_FLAG));
            Paint scrim = new Paint();
            scrim.setShader(new LinearGradient(0, 0, 0, bandH, 0x33000000, 0x00000000, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, bandH, scrim);
        } else {
            gradientPlate(c, 0, 0, W, bandH);
        }
        float y = 96;
        for (char ch : texts.title.toCharArray()) {
            text(c, String.valueOf(ch), 74, y + 64, 64, serif, 0xFFFBF3EC);
            y += 76;
        }
        text(c, texts.author, 74, y + 40, 24, serif, 0xFFECD2C4);
        seal(c, W - 74 - 74, bandH - 74 - 74, 74, "讀");

        TextPaint quote = quotePaint(INK, 46);
        String[] lines = wrap(quote, texts.quote, W - 2 * M);
        float top = bandH + 90, bottom = H - 360;
        float qy = top + Math.max(0, (bottom - top - lines.length * 80 - 70) / 2f);
        for (String line : lines) {
            c.drawText(line, M, qy, quote);
            qy += 80;
        }
        text(c, texts.chapter, M, qy + 22, 24, serif, MUTED);
        text(c, join(texts.author, " · ", dateSlash()), M, qy + 60, 20, serif, MUTED);
        stub(c, b, false, M, new String[]{"paper", "paper"});
        grain(b, 0.05f);
    }

    private void cinnabarFrame(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        int m = 56;
        rectStroke(c, m, m, W - m, H - m, CINNABAR, 3);
        rectStroke(c, m + 10, m + 10, W - m - 10, H - m - 10, 0xFFE2D1C2, 1);
        text(c, texts.dateLine, m + 40, m + 66, 21, serif, MUTED);
        seal(c, W - m - 40 - 70, m + 36, 70, "書");

        TextPaint quote = quotePaint(INK, 52);
        String[] lines = wrap(quote, texts.quote, W - 2 * m - 120);
        float y = (H - lines.length * 88) / 2f - 40;
        for (String line : lines) {
            float w = quote.measureText(line);
            c.drawText(line, (W - w) / 2f, y + 84, quote);
            y += 88;
        }
        y += 30;
        rect(c, W / 2f - 40, y, W / 2f + 40, y + 2, CINNABAR);
        float ty = y + 48;
        String t = quoted(texts.title);
        text(c, t, (W - serifSize(32, t)) / 2f, ty, 32, serif, INK);
        text(c, texts.author, (W - serifSize(24, texts.author)) / 2f, ty + 46, 24, serif, MUTED);
        stub(c, b, false, M, new String[]{"paper", "paper"});
        grain(b, 0.05f);
    }

    private void gallery(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        text(c, "“", 104, 520, 330, serif, 0xFF3B2C27);
        TextPaint quote = quotePaint(NIGHT_TEXT, 54);
        String[] lines = wrap(quote, texts.quote, 800);
        float top = 260, bottom = H - 360;
        float y = top + Math.max(0, (bottom - top - lines.length * 92 - 120) / 2f);
        for (String line : lines) {
            c.drawText(line, 150, y, quote);
            y += 92;
        }
        rect(c, 150, y + 6, 150 + 64, y + 9, ACCENT);
        text(c, quoted(texts.title), 150, y + 56, 30, serif, NIGHT_TEXT);
        text(c, join(texts.author, " · ", texts.chapter), 150, y + 100, 22, serif, NIGHT_MUTED);
        seal(c, W - 82 - 78, y + 24, 78, "讀");
        stub(c, b, true, M, new String[]{"night", "night"});
        vignette(b);
        grain(b, 0.05f);
    }

    // ------------------------------------------------------------------ the stub

    private void stub(Canvas c, Bitmap ground, boolean dark, int left, String[] notches) {
        int qrSize = 112;
        int qrX = W - 84 - qrSize;
        int qrY = H - 84 - qrSize;
        int perfY = H - 296;
        int hair = dark ? HAIR_DARK : HAIR_LIGHT;
        int muted = dark ? NIGHT_MUTED : MUTED;
        int accent = dark ? ACCENT : CINNABAR;

        Paint dash = new Paint(Paint.ANTI_ALIAS_FLAG);
        dash.setColor(hair);
        dash.setStyle(Paint.Style.STROKE);
        dash.setStrokeWidth(2f);
        dash.setPathEffect(new DashPathEffect(new float[]{9f, 9f}, 0f));
        c.drawLine(left, perfY, W - 84, perfY, dash);
        Paint notch = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < 2; i++) {
            String side = notches[i];
            int fill = "spine".equals(side) ? CINNABAR : ("night".equals(side) ? NIGHT : PAPER);
            if ("none".equals(side)) continue;
            notch.setColor(fill);
            c.drawCircle(i == 0 ? 0 : W, perfY, 13, notch);
        }

        // QR + its two labels, one typographic group centred on the code
        Bitmap qrBmp = qr(qrSize, dark);
        if (qrBmp != null) c.drawBitmap(qrBmp, qrX, qrY, null);
        float labelSize = 16f;
        while (labelSize > 11f && trackedWidth(texts.scan, labelSize, 0.10f) > qrSize + 26) {
            labelSize -= 1f;
        }
        drawTracked(c, texts.scan, qrX + qrSize / 2f, qrY - 18 - labelSize, labelSize, muted, 0.10f);
        drawTracked(c, texts.urlLabel, qrX + qrSize / 2f, qrY + qrSize + 22, labelSize, muted, 0.06f);

        // colophon: kicker, name, claim, specs - top line shared with the scan label
        String[] parts = splitIntro(texts.intro);
        TextPaint claim = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        claim.setTypeface(serif);
        claim.setTextSize(23);
        claim.setColor(dark ? NIGHT_TEXT : INK);
        String[] claimLines = wrap(claim, parts[1], qrX - left - 46);
        TextPaint spec = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        spec.setTypeface(sans);
        spec.setTextSize(14);
        spec.setColor(muted);
        String[] specLines = parts[2].length() == 0 ? new String[0]
                : wrap(spec, parts[2], qrX - left - 46);
        float total = 6 + 12 + 26 + claimLines.length * 34 + (specLines.length == 0 ? 0
                : 8 + specLines.length * 22);
        float y = qrY - 34;
        if (total > (qrY + qrSize) - (qrY - 34) + 18) {
            y = qrY + qrSize - total;
        }
        rect(c, left, y, left + 34, y + 4, accent);
        y += 18;
        drawTracked(c, parts[0], left, y + 18, 19f, accent, 0.14f);
        y += 26;
        for (String line : claimLines) {
            c.drawText(line, left, y + 22, claim);
            y += 34;
        }
        if (specLines.length > 0) {
            y += 8;
            for (String line : specLines) {
                drawTracked(c, line, left, y + 14, 14f, muted, 0.05f);
                y += 22;
            }
        }
    }

    // ------------------------------------------------------------------ helpers

    private void grain(Bitmap b, float alpha) {
        if (noise == null) {
            noise = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888);
            java.util.Random rnd = new java.util.Random(7);
            for (int y = 0; y < 160; y++) {
                for (int x = 0; x < 160; x++) {
                    int v = 128 + rnd.nextInt(48) - 24;
                    noise.setPixel(x, y, Color.argb(26, v, v, v));
                }
            }
        }
        Paint p = new Paint();
        p.setShader(new BitmapShader(noise, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        p.setAlpha((int) (255 * alpha * 6));
        new Canvas(b).drawRect(0, 0, W, H, p);
    }

    private void vignette(Bitmap b) {
        Paint p = new Paint();
        p.setShader(new RadialGradient(W / 2f, H / 2f, W * 0.72f,
                new int[]{0x00000000, 0x00000000, 0x66000000}, new float[]{0f, 0.55f, 1f},
                Shader.TileMode.CLAMP));
        new Canvas(b).drawRect(0, 0, W, H, p);
    }

    private void gradientPlate(Canvas c, int l, int t, int r, int bt) {
        Paint p = new Paint();
        int[] colors = new int[]{0xFF7E261E, 0xFF9E3A2B, 0xFFC0503A};
        p.setShader(new LinearGradient(l, t, r, bt, colors, new float[]{0f, 0.6f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(l, t, r, bt, p);
    }

    private Bitmap qr(int size, boolean dark) {
        if (qr != null && qrDark == dark && qr.getWidth() == size) {
            return qr;
        }
        try {
            com.google.zxing.common.BitMatrix m = new QRCodeWriter()
                    .encode(texts.url, BarcodeFormat.QR_CODE, size, size);
            qr = Bitmap.createBitmap(m.getWidth(), m.getHeight(), Bitmap.Config.ARGB_8888);
            int module = dark ? 0xFFF2E7E2 : 0xFF201814;
            for (int y = 0; y < m.getHeight(); y++) {
                for (int x = 0; x < m.getWidth(); x++) {
                    qr.setPixel(x, y, m.get(x, y) ? module : 0x00000000);
                }
            }
            qrDark = dark;
        } catch (Throwable e) {
            android.util.Log.e("ShareQuoteCard", "qr failed", e);
            qr = null;
        }
        return qr;
    }

    private void seal(Canvas c, float x, float y, int size, String glyph) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(CINNABAR);
        c.drawRoundRect(new RectF(x, y, x + size, y + size), 8, 8, p);
        TextPaint t = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        t.setTypeface(serif);
        t.setTextSize(size * 0.56f);
        t.setColor(0xFFFBF8F2);
        float w = t.measureText(glyph);
        c.drawText(glyph, x + (size - w) / 2f, y + size * 0.72f, t);
    }

    private void text(Canvas c, String s, float x, float y, float size, Typeface face, int color) {
        if (s == null || s.length() == 0) return;
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(face);
        p.setTextSize(size);
        p.setColor(color);
        c.drawText(s, x, y, p);
    }

    private float serifSize(float size, String s) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(serif);
        p.setTextSize(size);
        return p.measureText(s);
    }

    private TextPaint quotePaint(int color, float size) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(serif);
        p.setTextSize(size);
        p.setColor(color);
        return p;
    }

    private void drawTracked(Canvas c, String s, float cx, float baseline, float size,
                             int color, float track) {
        if (s == null || s.length() == 0) return;
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(sans);
        p.setTextSize(size);
        p.setColor(color);
        float w = trackedWidth(s, size, track);
        float x = cx - w / 2f;
        for (char ch : s.toCharArray()) {
            c.drawText(String.valueOf(ch), x, baseline, p);
            x += p.measureText(String.valueOf(ch)) + size * track;
        }
    }

    private float trackedWidth(String s, float size, float track) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(sans);
        p.setTextSize(size);
        float w = 0;
        for (char ch : s.toCharArray()) w += p.measureText(String.valueOf(ch)) + size * track;
        return w - size * track;
    }

    private void rect(Canvas c, float l, float t, float r, float b, int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        c.drawRect(l, t, r, b, p);
    }

    private void rectStroke(Canvas c, float l, float t, float r, float b, int color, float w) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(w);
        c.drawRect(l, t, r, b, p);
    }

    private void line(Canvas c, float x1, float y1, float x2, float y2, int color, float w) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setStrokeWidth(w);
        c.drawLine(x1, y1, x2, y2, p);
    }

    private Paint paint(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        return p;
    }

    private RectF coverRect(int l, int t, int r, int b) {
        return new RectF(l, t, r, b);
    }

    /** The part of the cover that fills the target box without distortion. */
    private android.graphics.Rect centerCrop(int l, int t, int r, int b) {
        if (cover == null || cover.getWidth() <= 0 || cover.getHeight() <= 0) {
            return new android.graphics.Rect(0, 0, 1, 1);
        }
        float boxW = r - l, boxH = b - t;
        float srcRatio = cover.getWidth() / (float) cover.getHeight();
        float boxRatio = boxW / boxH;
        float srcW, srcH;
        if (srcRatio > boxRatio) {
            srcH = cover.getHeight();
            srcW = srcH * boxRatio;
        } else {
            srcW = cover.getWidth();
            srcH = srcW / boxRatio;
        }
        int x0 = Math.round((cover.getWidth() - srcW) / 2f);
        int y0 = Math.round((cover.getHeight() - srcH) / 2f);
        return new android.graphics.Rect(x0, y0, x0 + Math.round(srcW), y0 + Math.round(srcH));
    }

    private boolean cjk() {
        String lang = Locale.getDefault().getLanguage();
        return "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
    }

    private String monthLabel() {
        if (cjk()) return "";
        return new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date()).toUpperCase(Locale.getDefault());
    }

    private String year() {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
    }

    private String quoted(String title) {
        String t = title == null ? "" : title.trim();
        return cjk() ? "《" + t + "》" : "\u201C" + t + "\u201D";
    }

    private Typeface load(String asset) {
        try {
            return Typeface.createFromAsset(context.getAssets(), asset);
        } catch (Throwable ignored) {
            return Typeface.SERIF;
        }
    }

    /** Packs the preview text into at most three lines, never starting a line with punctuation. */
    private String[] wrap(TextPaint p, String text, float maxWidth) {
        String body = text == null ? "" : text.trim();
        if (body.length() == 0) return new String[]{""};
        if (body.indexOf('\n') >= 0) {
            java.util.List<String> all = new java.util.ArrayList<>();
            for (String para : body.split("\n")) {
                java.util.Collections.addAll(all, wrapLine(p, para.trim(), maxWidth));
            }
            return all.toArray(new String[0]);
        }
        return wrapLine(p, body, maxWidth);
    }

    private String[] wrapLine(TextPaint p, String body, float maxWidth) {
        String noStart = "，。、；：！？）》」』】…·,.;:!?)]}";
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < body.length(); i++) {
            char ch = body.charAt(i);
            if (p.measureText(cur.toString() + ch) > maxWidth && cur.length() > 0) {
                if (noStart.indexOf(ch) >= 0 && cur.length() > 1) {
                    char last = cur.charAt(cur.length() - 1);
                    lines.add(cur.substring(0, cur.length() - 1));
                    cur.setLength(0);
                    cur.append(last).append(ch);
                } else {
                    lines.add(cur.toString());
                    cur.setLength(0);
                    cur.append(ch);
                }
            } else {
                cur.append(ch);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines.toArray(new String[0]);
    }

    /** Largest size that keeps the quote within six lines, down to 34. */
    private TextPaint fittedQuote(int color, float startSize, float maxWidth) {
        float size = startSize;
        while (size > 34f) {
            TextPaint probe = quotePaint(color, size);
            if (wrap(probe, texts.quote, maxWidth).length <= 6) return probe;
            size -= 2f;
        }
        return quotePaint(color, 34f);
    }

    /** "OmniShelf：claim，specs。" -> {name, claim, specs}. Never drops a word. */
    private String[] splitIntro(String intro) {
        String s = intro == null ? "" : intro.trim();
        String name = "OmniShelf", claim = s, specs = "";
        int sep = s.indexOf('：');
        if (sep < 0) sep = s.indexOf(':');
        if (sep > 0) {
            name = s.substring(0, sep).trim();
            claim = s.substring(sep + 1).trim();
        }
        int cut = claim.lastIndexOf('，');
        if (cut < 0) cut = claim.lastIndexOf(',');
        if (cut > 0) {
            specs = claim.substring(cut + 1).trim();
            claim = claim.substring(0, cut).trim() + (claim.indexOf('，') >= 0 ? "。" : ".");
        }
        return new String[]{name, claim, specs};
    }

    private String join(String a, String sep, String b) {
        String x = a == null ? "" : a.trim();
        String y = b == null ? "" : b.trim();
        if (x.length() == 0) return y;
        if (y.length() == 0) return x;
        return x + sep + y;
    }

    private String truncUrl() {
        return texts.url == null ? "" : texts.url.replace("https://", "").replace("http://", "");
    }

    private String day() {
        return new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
    }

    private String monthYear() {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
    }

    private String weekday() {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
    }

    private String dateSlash() {
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
    }

    private String verticalDate() {
        return new SimpleDateFormat("yyyy年M月d日", Locale.getDefault()).format(new Date());
    }

    private String monthYearLong() {
        return new SimpleDateFormat("yyyy年M月", Locale.getDefault()).format(new Date());
    }
}
