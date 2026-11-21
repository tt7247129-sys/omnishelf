package com.foobnix.ui2.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
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
import java.util.Random;

/**
 * Renders a quote card. Five layouts, all drawn from the app's palette.
 *
 * <p>This is a line-by-line port of docs/share-card/v3/render-cards.py: the same coordinates,
 * the same sizes, the same tracking and the same top-anchored text, so a card rendered on a
 * phone is the card that was approved. Only the typefaces differ in file, not in role: the
 * approved art used Songti and Helvetica, the phone has the bundled Noto Serif SC and the
 * system sans, which take their places.
 *
 * <p>Every date comes from {@link Date} and the reader's locale, so the card is always the
 * card of today.
 */
public class ShareQuoteCard {

    // The five approved layouts. The cover-ticket card was cut by request.
    public static final int T_DATE_PAPER = 0;
    public static final int T_VERTICAL_SPINE_NIGHT = 1;
    public static final int T_COVER_FULL_PAPER = 2;
    public static final int T_CINNABAR_FRAME_PAPER = 3;
    public static final int T_GALLERY_NIGHT = 4;
    public static final int TEMPLATE_COUNT = 5;

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
        /** The day as "21", from the device clock. */
        public String day = "";
        /** Month label for the date card: NOVEMBER, or 十一月 in Chinese. */
        public String monthLabel = "";
        /** The year as "2026". */
        public String year = "";
        /** Weekday in the reader's language. */
        public String weekday = "";
        /** Full date, e.g. 2026 年 11 月 21 日 or Nov 21, 2026. */
        public String dateLine = "";
        /** Month and year, e.g. 二〇二六年十一月 or November 2026. */
        public String monthYearLine = "";
        /** A column of date characters for the spine layout. */
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
        rect(c, 84, 92, 84 + 46, 96, CINNABAR);
        textTop(c, texts.monthLabel, 84, 116, 22, sans, MUTED);
        // the day is drawn as a block, optically aligned to the margin
        TextPaint big = paint(Typeface.create(sans, Typeface.BOLD), 200, INK);
        Rect bounds = new Rect();
        big.getTextBounds(texts.day, 0, texts.day.length(), bounds);
        c.drawText(texts.day, 84 - bounds.left, 150 - bounds.top, big);
        textTop(c, texts.year, 86, 372, 58, sans, INK);
        textTop(c, texts.weekday, 86, 446, 26, serif, MUTED);
        line(c, 86, 512, W - 86, 512, HAIR_LIGHT, 1);

        TextPaint quote = paint(serif, 48, INK);
        String[] lines = wrap(quote, texts.quote, 800);
        float quoteH = lines.length * 80f;
        float blockH = quoteH + 118;
        float top = 540, bottom = H - 330;
        float y = top + Math.max(24, (bottom - top - blockH) / 2f);
        for (int i = 0; i < lines.length; i++) {
            textTop(c, lines[i], 86, y + i * 80, 48, serif, INK);
        }
        y += quoteH;
        textTop(c, "《" + texts.title + "》", 86, y + 34, 30, Typeface.create(serif, Typeface.BOLD), INK);
        textTop(c, join(texts.author, " · ", texts.chapter), 86, y + 82, 22, serif, MUTED);
        seal(c, W - 86 - 84, y + 20, 84, "書");
        stub(c, b, false, M, "paper", "paper", 112);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 2 竖排书脊 · 夜

    private void verticalSpine(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        int spineW = 176;
        Paint spine = new Paint(Paint.ANTI_ALIAS_FLAG);
        spine.setShader(new LinearGradient(0, 0, spineW, 0, 0xFF7A241B, 0xFF9E3025, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, spineW, H, spine);

        float y = 150;
        Typeface titleFace = Typeface.create(serif, Typeface.BOLD);
        for (char ch : texts.title.toCharArray()) {
            textTop(c, String.valueOf(ch), 54, y, 74, titleFace, 0xFFF7E3D9);
            y += 88;
        }
        float ay = H - 360;
        for (char ch : texts.author.toCharArray()) {
            textTop(c, String.valueOf(ch), 54, ay, 22, serif, 0xFFE4C2B4);
            ay += 32;
        }
        int rx = W - 120;
        float dy = 120;
        for (char ch : texts.verticalDate.toCharArray()) {
            textTop(c, String.valueOf(ch), rx, dy, 24, serif, NIGHT_MUTED);
            dy += 34;
        }
        line(c, rx - 40, 120, rx - 40, 700, HAIR_DARK, 1);

        int textX = spineW + 70;
        TextPaint quote = paint(serif, 44, NIGHT_TEXT);
        String[] lines = wrap(quote, texts.quote, (rx - 60) - textX);
        float quoteH = lines.length * 78f;
        float blockH = quoteH + 150;
        float top = 220, bottom = H - 320;
        float qy = top + Math.max(20, (bottom - top - blockH) / 2f);
        for (int i = 0; i < lines.length; i++) {
            textTop(c, lines[i], textX, qy + i * 78, 44, serif, NIGHT_TEXT);
        }
        qy += quoteH;
        textTop(c, "/ " + texts.chapter, textX, qy + 26, 24, serif, NIGHT_MUTED);
        seal(c, textX, qy + 70, 74, "書");
        stub(c, b, true, spineW + 40, "spine", "night", 108);
        vignette(b, 0.45f);
        grain(b, 0.05f);
    }

    // ---------------------------------------------------------------- 3 影像封面 · 纸

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
            Paint plate = new Paint();
            plate.setShader(new LinearGradient(0, 0, W, bandH,
                    new int[]{0xFF86281F, 0xFF9E3A2B, 0xFFC0503A}, new float[]{0f, 0.55f, 1f},
                    Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, bandH, plate);
            // the warm lamp of the reference art
            Paint lamp = new Paint();
            lamp.setShader(new RadialGradient(W / 2f, bandH * 0.42f, W * 0.62f,
                    new int[]{0x33FFD9B0, 0x00000000}, new float[]{0f, 1f}, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, bandH, lamp);
        }
        float vy = 96;
        Typeface titleFace = Typeface.create(serif, Typeface.BOLD);
        for (char ch : texts.title.toCharArray()) {
            textTop(c, String.valueOf(ch), 74, vy, 64, titleFace, 0xFFFBF3EC);
            vy += 76;
        }
        textTop(c, texts.author, 74, vy + 15, 24, serif, 0xFFECD2C4);
        seal(c, W - 74 - 74, bandH - 74 - 74, 74, "讀");

        TextPaint quote = paint(serif, 46, INK);
        String[] lines = wrap(quote, texts.quote, W - 164);
        float qy = bandH + 76;
        for (int i = 0; i < lines.length; i++) {
            textTop(c, lines[i], 82, qy + i * 80, 46, serif, INK);
        }
        qy += lines.length * 80f;
        textTop(c, texts.chapter, 82, qy + 22, 24, serif, MUTED);
        textTop(c, join(texts.author, " · ", texts.dateLine), 82, qy + 62, 20, serif, MUTED);
        stub(c, b, false, M, "paper", "paper", 108);
        grain(b, 0.08f);
    }

    // ---------------------------------------------------------------- 4 朱砂框 · 纸

    private void cinnabarFrame(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        int m = 56;
        frame(c, m, m, W - m, H - m, CINNABAR, 3);
        frame(c, m + 10, m + 10, W - m - 10, H - m - 10, 0xFFE2D1C2, 1);
        textTop(c, cjk() ? "摘录 · " + texts.monthYearLine : texts.monthYearLine, m + 40, m + 44,
                21, serif, MUTED);
        seal(c, W - m - 40 - 70, m + 36, 70, "書");

        TextPaint quote = paint(serif, 52, INK);
        String[] lines = wrap(quote, texts.quote, W - 2 * m - 120);
        float y = (H - lines.length * 88f) / 2f - 40;
        for (int i = 0; i < lines.length; i++) {
            float w = quote.measureText(lines[i]);
            textTop(c, lines[i], (W - w) / 2f, y + i * 88, 52, serif, INK);
        }
        y += lines.length * 88f;
        line(c, W / 2f - 40, y + 40, W / 2f + 40, y + 40, CINNABAR, 2);
        Typeface titleFace = Typeface.create(serif, Typeface.BOLD);
        String t = "《" + texts.title + "》";
        float tw = measure(t, titleFace, 32);
        textTop(c, t, (W - tw) / 2f, y + 66, 32, titleFace, INK);
        float aw = measure(texts.author, serif, 24);
        textTop(c, texts.author, (W - aw) / 2f, y + 116, 24, serif, MUTED);
        stub(c, b, false, M, "paper", "paper", 100);
        grain(b, 0.09f);
    }

    // ---------------------------------------------------------------- 5 画廊极简 · 夜

    private void gallery(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        textTop(c, "“", 104, 210, 330, Typeface.create(serif, Typeface.BOLD), 0xFF3B2C27);
        TextPaint quote = paint(serif, 54, NIGHT_TEXT);
        String[] lines = wrap(quote, texts.quote, 800);
        float quoteH = lines.length * 92f;
        float blockH = quoteH + 230;
        float top = 200, bottom = H - 320;
        float y = top + Math.max(20, (bottom - top - blockH) / 2f);
        for (int i = 0; i < lines.length; i++) {
            textTop(c, lines[i], 150, y + i * 92, 54, serif, NIGHT_TEXT);
        }
        y += quoteH;
        line(c, 150, y + 34, 150 + 64, y + 34, ACCENT, 3);
        Typeface titleFace = Typeface.create(serif, Typeface.BOLD);
        textTop(c, "《" + texts.title + "》", 150, y + 64, 30, titleFace, NIGHT_TEXT);
        textTop(c, join(texts.author, " · ", texts.chapter), 150, y + 112, 22, serif, NIGHT_MUTED);
        seal(c, W - 82 - 78, y + 40, 78, "讀");
        stub(c, b, true, M, "night", "night", 104);
        vignette(b, 0.60f);
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

        // the perforation is drawn as segments, exactly as the approved art drew it
        for (int x = left; x < W - 84; x += 18) {
            line(c, x, perfY, Math.min(x + 9, W - 84), perfY, hair, 2);
        }
        Paint notch = new Paint(Paint.ANTI_ALIAS_FLAG);
        notch.setColor(notchColor(leftNotch));
        c.drawCircle(0, perfY, 13, notch);
        notch.setColor(notchColor(rightNotch));
        c.drawCircle(W, perfY, 13, notch);

        // one group: the scan line, the code, the domain - same size, same tracking, same axis
        float qrCx = qrX + qrSize / 2f;
        float label = 16f;
        while (label > 11f && trackedWidth(texts.scan, label, 0.10f, sans) > qrSize + 26) {
            label -= 1f;
        }
        drawTrackedCentered(c, texts.scan, qrCx, qrY - 18 - label, label, muted, 0.10f);
        Bitmap code = qr(qrSize, dark);
        if (code != null) c.drawBitmap(code, qrX, qrY, null);
        drawTrackedCentered(c, texts.urlLabel, qrCx, qrY + qrSize + 10, label, muted, 0.06f);

        colophon(c, dark, left, qrY - 34, qrY + qrSize, qrX - left - 46);
    }

    /** Three tiers: kicker, tracked name, claim, specs. Port of intro_block(). */
    private void colophon(Canvas c, boolean dark, int left, float topY, float bottomY, int maxw) {
        int fg = dark ? NIGHT_MUTED : MUTED;
        int strong = dark ? NIGHT_TEXT : INK;
        int accent = dark ? ACCENT : CINNABAR;

        String brand = "OmniShelf", claim = texts.intro, specs = "";
        int sep = texts.intro.indexOf('：');
        if (sep < 0) sep = texts.intro.indexOf(':');
        if (sep > 0) {
            brand = texts.intro.substring(0, sep).trim();
            claim = texts.intro.substring(sep + 1).trim();
        }
        int cut = claim.lastIndexOf('，');
        if (cut < 0) cut = claim.lastIndexOf(',');
        if (cut > 0) {
            specs = claim.substring(cut + 1).trim();
            claim = claim.substring(0, cut).trim() + (claim.indexOf('，') >= 0 ? "。" : ".");
        }

        TextPaint claimPaint = paint(serif, 23, strong);
        TextPaint specPaint = paint(sans, 14, fg);
        String[] claimLines = wrap(claimPaint, claim, maxw);
        String[] specLines = specs.length() == 0 ? new String[0] : wrap(specPaint, specs, maxw);

        float brandH = 26, claimH = claimLines.length * 34f;
        float specH = specLines.length * 22f;
        float total = 6 + 12 + brandH + claimH + (specs.length() == 0 ? 0 : 8 + specH);
        float y = total <= (bottomY - topY) + 18 ? topY : bottomY - total;

        rect(c, left, y, left + 34, y + 4, accent);
        y += 18;
        drawTrackedLeft(c, brand, left, y, 19, accent, 0.14f);
        y += brandH;
        for (int i = 0; i < claimLines.length; i++) {
            textTop(c, claimLines[i], left, y + i * 34, 23, serif, strong);
        }
        y += claimH;
        if (specs.length() > 0) {
            y += 8;
            for (int i = 0; i < specLines.length; i++) {
                drawTrackedLeft(c, specLines[i], left, y + i * 22, 14, fg, 0.05f);
            }
        }
    }

    private int notchColor(String side) {
        if ("spine".equals(side)) return CINNABAR;
        if ("night".equals(side)) return NIGHT;
        return PAPER;
    }

    // ---------------------------------------------------------------- 绘制工具

    /** Top-anchored text: the y the approved art used is the top of the ascender. */
    private void textTop(Canvas c, String s, float x, float topY, float size, Typeface face, int color) {
        if (s == null || s.length() == 0) return;
        TextPaint p = paint(face, size, color);
        c.drawText(s, x, topY - p.getFontMetrics().ascent, p);
    }

    private void drawTrackedLeft(Canvas c, String s, float x, float topY, float size, int color, float track) {
        if (s == null || s.length() == 0) return;
        TextPaint p = paint(sans, size, color);
        float cx = x;
        float baseline = topY - p.getFontMetrics().ascent;
        for (char ch : s.toCharArray()) {
            c.drawText(String.valueOf(ch), cx, baseline, p);
            cx += p.measureText(String.valueOf(ch)) + size * track;
        }
    }

    private void drawTrackedCentered(Canvas c, String s, float centerX, float topY, float size,
                                     int color, float track) {
        if (s == null || s.length() == 0) return;
        float w = trackedWidth(s, size, track, sans);
        drawTrackedLeft(c, s, centerX - w / 2f, topY, size, color, track);
    }

    private float trackedWidth(String s, float size, float track, Typeface face) {
        if (s == null || s.length() == 0) return 0;
        TextPaint p = paint(face, size, INK);
        float sum = 0;
        for (char ch : s.toCharArray()) sum += p.measureText(String.valueOf(ch));
        return sum + track * size * Math.max(0, s.length() - 1);
    }

    private void grain(Bitmap b, float alpha) {
        if (noise == null) {
            noise = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888);
            Random rnd = new Random(7);
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

    private void vignette(Bitmap b, float strength) {
        Paint p = new Paint();
        p.setShader(new RadialGradient(W / 2f, H / 2f, W * 0.72f,
                new int[]{0x00000000, 0x00000000, (int) (0x66 * strength / 0.55f) << 24},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));
        new Canvas(b).drawRect(0, 0, W, H, p);
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
        // a few specks off the edge, as if the stamp pressed unevenly
        Random rnd = new Random(7);
        Paint speck = new Paint(Paint.ANTI_ALIAS_FLAG);
        speck.setColor(PAPER);
        speck.setAlpha(30);
        for (int i = 0; i < 90; i++) {
            c.drawCircle(x + rnd.nextInt(size), y + rnd.nextInt(size), 1.1f, speck);
        }
        Typeface face = Typeface.create(serif, Typeface.BOLD);
        TextPaint t = paint(face, size * 0.56f, PAPER);
        Rect bounds = new Rect();
        t.getTextBounds(glyph, 0, glyph.length(), bounds);
        float gx = x + (size - bounds.width()) / 2f - bounds.left;
        float gy = y + (size - bounds.height()) / 2f - bounds.top;
        c.drawText(glyph, gx, gy, t);
    }

    private TextPaint paint(Typeface face, float size, int color) {
        TextPaint p = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(face);
        p.setTextSize(size);
        p.setColor(color);
        return p;
    }

    private float measure(String s, Typeface face, float size) {
        if (s == null || s.length() == 0) return 0;
        return paint(face, size, INK).measureText(s);
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

    /** CJK-aware wrapping with the reference's no-leading-punctuation and orphan rules. */
    private String[] wrap(TextPaint p, String text, float maxWidth) {
        String body = text == null ? "" : text.trim();
        if (body.length() == 0) return new String[]{""};
        List<String> out = new ArrayList<>();
        for (String para : body.split("\n")) {
            for (String line : wrapLine(p, para.trim(), maxWidth)) {
                if (line.length() > 0) out.add(line);
            }
        }
        return out.toArray(new String[0]);
    }

    private String[] wrapLine(TextPaint p, String body, float maxWidth) {
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
        if (lines.size() > 1 && lines.get(lines.size() - 1).length() <= 2) {
            String prev = lines.get(lines.size() - 2);
            if (prev.length() > 4) {
                lines.set(lines.size() - 2, prev.substring(0, prev.length() - 2));
                lines.set(lines.size() - 1, prev.substring(prev.length() - 2) + lines.get(lines.size() - 1));
            }
        }
        return lines.toArray(new String[0]);
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

    /** Builds every date the five layouts use, from the device clock and locale. */
    public static void fillDates(Texts texts) {
        Date now = new Date();
        Locale locale = Locale.getDefault();
        boolean cjk = "zh".equals(locale.getLanguage()) || "ja".equals(locale.getLanguage())
                || "ko".equals(locale.getLanguage());
        texts.day = new SimpleDateFormat("dd", locale).format(now);
        texts.year = new SimpleDateFormat("yyyy", locale).format(now);
        texts.weekday = new SimpleDateFormat("EEEE", locale).format(now);
        texts.dateLine = new SimpleDateFormat(cjk ? "yyyy 年 M 月 d 日" : "MMM d, yyyy", locale).format(now);
        if (cjk) {
            texts.monthLabel = chineseMonth(now);
            texts.monthYearLine = chineseYearMonth(now);
            texts.verticalDate = chineseYearMonthDay(now);
        } else {
            texts.monthLabel = new SimpleDateFormat("MMMM", locale).format(now).toUpperCase(locale);
            texts.monthYearLine = new SimpleDateFormat("MMMM yyyy", locale).format(now);
            texts.verticalDate = new SimpleDateFormat("yyyy.MM.dd", locale).format(now);
        }
    }

    private static String chineseMonth(Date d) {
        String[] names = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
        int month = Integer.parseInt(new SimpleDateFormat("M", Locale.CHINA).format(d));
        return names[Math.min(11, Math.max(0, month - 1))];
    }

    private static String chineseYearMonth(Date d) {
        return chineseYear(d) + "年" + chineseMonth(d);
    }

    private static String chineseYearMonthDay(Date d) {
        int month = Integer.parseInt(new SimpleDateFormat("M", Locale.CHINA).format(d));
        int day = Integer.parseInt(new SimpleDateFormat("d", Locale.CHINA).format(d));
        return chineseYear(d) + "年" + chineseNumber(month) + "月" + chineseNumber(day) + "日";
    }

    private static String chineseYear(Date d) {
        String digits = "〇一二三四五六七八九";
        StringBuilder out = new StringBuilder();
        for (char ch : new SimpleDateFormat("yyyy", Locale.CHINA).format(d).toCharArray()) {
            out.append(digits.charAt(ch - '0'));
        }
        return out.toString();
    }

    private static String chineseNumber(int n) {
        String digits = "〇一二三四五六七八九";
        if (n < 10) return String.valueOf(digits.charAt(n));
        if (n == 10) return "十";
        if (n < 20) return "十" + digits.charAt(n - 10);
        int tens = n / 10, ones = n % 10;
        return digits.charAt(tens) + "十" + (ones == 0 ? "" : String.valueOf(digits.charAt(ones)));
    }
}
