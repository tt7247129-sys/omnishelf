package com.foobnix.ui2.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Renders a quote card. Six layouts, all drawn from the app's palette.
 *
 * <p>The geometry mirrors docs/share-card/v3/render-cards.py one to one - every coordinate
 * below is the same number the approved mockups were drawn with - so a card made on a phone
 * and a card made for the design review are the same card. Only the typefaces differ: the
 * device uses the bundled Noto Serif SC and the system sans, the mockups used Songti and
 * Helvetica, which are the same roles in a different file.
 *
 * <p>Everything here is android.* plus ZXing; strings are passed in, so the renderer never
 * touches the app's R class and can be compiled on its own.
 */
public class ShareQuoteCard {

    public static final int T_DATE_PAPER = 0;
    public static final int T_COVER_TICKET_NIGHT = 1;
    public static final int T_VERTICAL_SPINE_NIGHT = 2;
    public static final int T_COVER_FULL_PAPER = 3;
    public static final int T_CINNABAR_FRAME_PAPER = 4;
    public static final int T_GALLERY_NIGHT = 5;
    public static final int TEMPLATE_COUNT = 6;

    private static final int W = 1080, H = 1440, M = 84;
    private static final int PAPER = 0xFFFBF8F2, INK = 0xFF241815, MUTED = 0xFF7C6B63;
    private static final int NIGHT = 0xFF151412, NIGHT_TEXT = 0xFFEFE4DC, NIGHT_MUTED = 0xFFA3938B;
    private static final int CINNABAR = 0xFF8E2B21, ACCENT = 0xFFD2503C;
    private static final int HAIR_DARK = 0xFF392D28, HAIR_LIGHT = 0xFFD8CBBE;

    /** The text a card needs; built by the caller from resources so it is localized. */
    public static class Texts {
        public String quote = "";
        public String title = "";
        public String author = "";
        public String chapter = "";
        public String intro = "";
        public String scan = "";
        public String url = "https://omnishelf.cc";
        public String urlLabel = "omnishelf.cc";
        /** Localized date, e.g. "2026 年 11 月 21 日" or "Nov 21, 2026". */
        public String dateLine = "";
        /** The same date as 二〇二六年十一月二十一日 for the vertical column. */
        public String verticalDate = "";
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

    /** Renders the card. Never null; a failure leaves the ground colour and logs why. */
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
            android.util.Log.e("ShareQuoteCard", "render failed for template " + template, e);
        }
        return bitmap;
    }

    // ---------------------------------------------------------------- 1 日期 · 纸 · 印章

    private void datePaper(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        rect(c, 82, 92, 82 + 46, 96, CINNABAR);
        text(c, "NOVEMBER", 82, 128, 22, sans, MUTED);
        TextPaint big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        big.setTypeface(Typeface.create(sans, Typeface.BOLD));
        big.setTextSize(200);
        big.setColor(INK);
        Rect bb = new Rect();
        big.getTextBounds("21", 0, 2, bb);
        c.drawText("21", 84 - bb.left, 150 - bb.top, big);
        text(c, "2026", 86, 372, 58, sans, INK);
        text(c, weekday(), 86, 446, 26, serif, MUTED);
        line(c, 86, 512, W - 86, 512, HAIR_LIGHT, 1);

        TextPaint quote = quotePaint(48);
        String[] lines = wrap(quote, texts.quote, 800);
        float quoteH = lines.length * 80f;
        float blockH = quoteH + 118;
        float top = 540, bottom = H - 330;
        float y = top + Math.max(24, (bottom - top - blockH) / 2f);
        for (String l : lines) {
            c.drawText(l, 86, y + 60, quote);
            y += 80;
        }
        text(c, "《" + texts.title + "》", 86, y + 34, 30, serif, INK);
        text(c, join(texts.author, " · ", texts.chapter), 86, y + 82, 22, serif, MUTED);
        seal(c, W - 86 - 84, y + 20, 84, "書");
        stub(c, b, false, M, "paper", "paper", 112);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 2 封面票根 · 夜

    private void dateCoverTicket(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        int left = 132, top = 118, right = W - 132, bottom = 704;
        if (cover != null) {
            c.drawBitmap(cover, centerCrop(left, top, right, bottom),
                    new RectF(left, top, right, bottom), new Paint(Paint.FILTER_BITMAP_FLAG));
        } else {
            plate(c, left, top, right, bottom);
        }
        frame(c, left + 14, top + 14, right - 14, bottom - 14, 0xFFF6E2D8, 1);
        seal(c, left - 40, top - 40, 80, "書");
        text(c, texts.title, left + 24, top + 56, 28, serif, 0xFFF6E2D8);
        text(c, texts.dateLine, M, 742, 22, serif, NIGHT_MUTED);

        TextPaint quote = quotePaint(46);
        String[] lines = wrap(quote, texts.quote, W - 2 * M);
        float blockH = lines.length * 78f + 60;
        float t0 = 800, b0 = H - 380;
        float y = t0 + Math.max(0, (b0 - t0 - blockH) / 2f);
        for (String l : lines) {
            c.drawText(l, M, y + 52, quote);
            y += 78;
        }
        text(c, "/ " + join(texts.title, " · ", texts.chapter), M, y + 20, 24, serif, NIGHT_MUTED);
        stub(c, b, true, M, "night", "night", 112);
        vignette(b);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 3 竖排书脊 · 夜

    private void verticalSpine(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        int spineW = 176;
        Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG);
        sp.setShader(new LinearGradient(0, 0, spineW, 0, 0xFF7A241B, 0xFF9C3025, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, spineW, H, sp);

        float ty0 = 150;
        for (char ch : texts.title.toCharArray()) {
            text(c, String.valueOf(ch), 54, ty0 + 64, 74, serif, 0xFFF7E3D9);
            ty0 += 88;
        }
        float ay = H - 360;
        for (char ch : texts.author.toCharArray()) {
            text(c, String.valueOf(ch), 54, ay, 22, serif, 0xFFE4C2B4);
            ay += 32;
        }
        int rx = W - 120;
        float dy = 120;
        for (char ch : verticalDateChars()) {
            text(c, String.valueOf(ch), rx, dy + 24, 24, serif, NIGHT_MUTED);
            dy += 34;
        }
        line(c, rx - 40, 120, rx - 40, 700, HAIR_DARK, 1);

        int textX = spineW + 70;
        TextPaint quote = quotePaint(44);
        String[] lines = wrap(quote, texts.quote, (rx - 60) - textX);
        float quoteH = lines.length * 78f;
        float blockH = quoteH + 150;
        float t0 = 220, b0 = H - 320;
        float y = t0 + Math.max(20, (b0 - t0 - blockH) / 2f);
        for (String l : lines) {
            c.drawText(l, textX, y + 50, quote);
            y += 78;
        }
        String meta = join(texts.title, " · ", texts.chapter);
        if (meta.length() > 0) {
            text(c, "/ " + meta, textX, y + 26, 24, serif, NIGHT_MUTED);
        }
        seal(c, textX, y + 70, 74, "書");
        stub(c, b, true, spineW + 40, "spine", "night", 108);
        vignette(b);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 4 影像封面 · 纸

    private void coverFullPaper(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        int bandH = 620;
        if (cover != null) {
            c.drawBitmap(cover, centerCrop(0, 0, W, bandH), new RectF(0, 0, W, bandH),
                    new Paint(Paint.FILTER_BITMAP_FLAG));
            Paint scrim = new Paint();
            scrim.setShader(new LinearGradient(0, 0, 0, bandH, 0x33000000, 0x00000000, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, bandH, scrim);
        } else {
            plate(c, 0, 0, W, bandH);
        }
        float vy = 96;
        for (char ch : texts.title.toCharArray()) {
            text(c, String.valueOf(ch), 74, vy + 56, 64, serif, 0xFFFBF3EC);
            vy += 76;
        }
        text(c, texts.author, 74, vy + 40, 24, serif, 0xFFECD2C4);
        seal(c, W - 74 - 74, bandH - 74 - 74, 74, "讀");

        TextPaint quote = quotePaint(46);
        String[] lines = wrap(quote, texts.quote, W - 2 * M);
        float blockH = lines.length * 80f + 70;
        float t0 = bandH + 90, b0 = H - 360;
        float qy = t0 + Math.max(0, (b0 - t0 - blockH) / 2f);
        for (String l : lines) {
            c.drawText(l, M, qy + 52, quote);
            qy += 80;
        }
        text(c, texts.chapter, M, qy + 22, 24, serif, MUTED);
        text(c, join(texts.author, " · ", texts.dateLine), M, qy + 60, 20, serif, MUTED);
        stub(c, b, false, M, "paper", "paper", 108);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 5 朱砂框 · 纸

    private void cinnabarFrame(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        int m = 56;
        frame(c, m, m, W - m, H - m, CINNABAR, 3);
        frame(c, m + 10, m + 10, W - m - 10, H - m - 10, 0xFFE2D1C2, 1);
        text(c, texts.dateLine, m + 40, m + 66, 21, serif, MUTED);
        seal(c, W - m - 40 - 70, m + 36, 70, "書");

        TextPaint quote = quotePaint(52);
        String[] lines = wrap(quote, texts.quote, W - 2 * m - 120);
        float y = (H - lines.length * 88f) / 2f - 40;
        for (String l : lines) {
            float w = quote.measureText(l);
            c.drawText(l, (W - w) / 2f, y + 84, quote);
            y += 88;
        }
        y += 10;
        rect(c, W / 2f - 40, y + 30, W / 2f + 40, y + 32, CINNABAR);
        String t = "《" + texts.title + "》";
        text(c, t, (W - measure(t, 32, serif)) / 2f, y + 76, 32, serif, INK);
        text(c, texts.author, (W - measure(texts.author, 24, serif)) / 2f, y + 122, 24, serif, MUTED);
        stub(c, b, false, M, "paper", "paper", 100);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 6 画廊极简 · 夜

    private void gallery(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        text(c, "“", 58, 360, 320, serif, 0xFF42312B);
        TextPaint quote = quotePaint(54);
        String[] lines = wrap(quote, texts.quote, 800);
        float blockH = lines.length * 92f + 230;
        float t0 = 200, b0 = H - 320;
        float y = t0 + Math.max(20, (b0 - t0 - blockH) / 2f);
        for (String l : lines) {
            c.drawText(l, 150, y + 62, quote);
            y += 92;
        }
        rect(c, 150, y + 34, 150 + 64, y + 37, ACCENT);
        text(c, "《" + texts.title + "》", 150, y + 94, 30, serif, NIGHT_TEXT);
        text(c, join(texts.author, " · ", texts.chapter), 150, y + 138, 22, serif, NIGHT_MUTED);
        seal(c, W - 82 - 78, y + 60, 78, "讀");
        stub(c, b, true, M, "night", "night", 104);
        vignette(b);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 票根页脚

    private void stub(Canvas c, Bitmap ground, boolean dark, int left,
                      String leftNotch, String rightNotch, int qrSize) {
        int qrX = W - 84 - qrSize;
        int qrY = H - 84 - qrSize;
        int perfY = H - 296;
        int hair = dark ? HAIR_DARK : HAIR_LIGHT;
        int muted = dark ? NIGHT_MUTED : MUTED;

        Paint dash = new Paint(Paint.ANTI_ALIAS_FLAG);
        dash.setColor(hair);
        dash.setStyle(Paint.Style.STROKE);
        dash.setStrokeWidth(2f);
        dash.setPathEffect(new DashPathEffect(new float[]{9f, 9f}, 0f));
        c.drawLine(left, perfY, W - 84, perfY, dash);
        Paint notch = new Paint(Paint.ANTI_ALIAS_FLAG);
        notch.setColor(notchColor(leftNotch));
        c.drawCircle(0, perfY, 13, notch);
        notch.setColor(notchColor(rightNotch));
        c.drawCircle(W, perfY, 13, notch);

        Bitmap code = qr(qrSize, dark);
        if (code != null) c.drawBitmap(code, qrX, qrY, null);

        // the scan line and the domain are one group, centred on the code
        float label = 16f;
        while (label > 11f && trackedWidth(texts.scan, label, 0.10f) > qrSize + 26) label -= 1f;
        drawTracked(c, texts.scan, qrX + qrSize / 2f, qrY - 18 - label, label, muted, 0.10f);
        drawTracked(c, texts.urlLabel, qrX + qrSize / 2f, qrY + qrSize + 22, label, muted, 0.06f);

        // colophon: kicker, name, claim, specs
        String[] parts = splitIntro(texts.intro);
        TextPaint claim = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        claim.setTypeface(serif);
        claim.setTextSize(23);
        claim.setColor(dark ? NIGHT_TEXT : INK);
        String[] claimLines = wrapText(claim, parts[1], qrX - left - 46);
        TextPaint spec = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        spec.setTypeface(sans);
        spec.setTextSize(14);
        spec.setColor(muted);
        String[] specLines = parts[2].length() == 0 ? new String[0]
                : wrapText(spec, parts[2], qrX - left - 46);
        float total = 6 + 12 + 26 + claimLines.length * 34f
                + (specLines.length == 0 ? 0 : 8 + specLines.length * 22f);
        float y = qrY - 34;
        if (total > (qrY + qrSize) - (qrY - 34) + 18) y = qrY + qrSize - total;
        rect(c, left, y, left + 34, y + 4, dark ? ACCENT : CINNABAR);
        y += 18;
        drawTracked(c, parts[0], left, y + 18, 19f, dark ? ACCENT : CINNABAR, 0.14f);
        y += 26;
        for (String l : claimLines) {
            c.drawText(l, left, y + 22, claim);
            y += 34;
        }
        for (String l : specLines) {
            y += 22;
            drawTracked(c, l, left, y, 14f, muted, 0.05f);
        }
        if (specLines.length > 0) y += 22;
    }

    private int notchColor(String side) {
        if ("spine".equals(side)) return CINNABAR;
        if ("night".equals(side)) return NIGHT;
        return PAPER;
    }

    // ---------------------------------------------------------------- 绘制工具

    private void grain(Bitmap b, float alpha) {
        if (noise == null) {
            noise = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888);
            java.util.Random rnd = new java.util.Random(7);
            for (int y = 0; y < 160; y++) {
                for (int x = 0; x < 160; x++) {
                    int v = 128 + rnd.nextInt(48) - 24;
                    noise.setPixel(x, y, android.graphics.Color.argb(26, v, v, v));
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

    private void plate(Canvas c, int l, int t, int r, int bt) {
        Paint p = new Paint();
        p.setShader(new LinearGradient(l, t, r, bt,
                new int[]{0xFF7E261E, 0xFF9E3A2B, 0xFFC0503A}, new float[]{0f, 0.6f, 1f},
                Shader.TileMode.CLAMP));
        c.drawRect(l, t, r, bt, p);
    }

    private Bitmap qr(int size, boolean dark) {
        if (qr != null && qrDark == dark && qr.getWidth() == size) return qr;
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

    private float measure(String s, float size, Typeface face) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(face);
        p.setTextSize(size);
        return p.measureText(s == null ? "" : s);
    }

    private TextPaint quotePaint(float size) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(serif);
        p.setTextSize(size);
        p.setColor(INK);
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
        if (s == null || s.length() == 0) return 0;
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

    private void frame(Canvas c, float l, float t, float r, float b, int color, float w) {
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

    private Typeface load(String asset) {
        try {
            return Typeface.createFromAsset(context.getAssets(), asset);
        } catch (Throwable ignored) {
            return Typeface.SERIF;
        }
    }

    private Rect centerCrop(int l, int t, int r, int b) {
        if (cover == null || cover.getWidth() <= 0 || cover.getHeight() <= 0) {
            return new Rect(0, 0, 1, 1);
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
        return new Rect(x0, y0, x0 + Math.round(srcW), y0 + Math.round(srcH));
    }

    /** CJK-aware wrapping that never starts a line with a closing mark. */
    private String[] wrap(TextPaint p, String text, float maxWidth) {
        String body = text == null ? "" : text.trim();
        if (body.length() == 0) return new String[]{""};
        List<String> out = new ArrayList<>();
        for (String para : body.split("\n")) {
            for (String line : wrapText(p, para.trim(), maxWidth)) {
                if (line.length() > 0) out.add(line);
            }
        }
        return out.toArray(new String[0]);
    }

    private String[] wrapText(TextPaint p, String body, float maxWidth) {
        if (body == null || body.length() == 0) return new String[0];
        String noStart = "，。、；：！？）》」』】…·,.;:!?)]}";
        List<String> lines = new ArrayList<>();
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

    /** "OmniShelf：claim，specs。" -> {name, claim, specs}; never drops a word. */
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

    private boolean cjk() {
        String lang = Locale.getDefault().getLanguage();
        return "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
    }

    private String weekday() {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
    }

    private String verticalDateChars() {
        return texts.verticalDate != null && texts.verticalDate.length() > 0
                ? texts.verticalDate.replace(" ", "")
                : new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    /**
     * Deletes cards older than a week. They live in filesDir/share, one PNG per share, and
     * without this the directory only grows.
     */
    public static void pruneOldCards(java.io.File dir) {
        if (dir == null || !dir.isDirectory()) return;
        java.io.File[] files = dir.listFiles();
        if (files == null) return;
        long cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        for (java.io.File f : files) {
            if (f != null && f.isFile() && f.lastModified() < cutoff) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
    }
}
