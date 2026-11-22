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
 * <p>The geometry follows the approved art, but every block of text is fitted to its own
 * safe area before it is drawn: a long title shrinks, a long quote shrinks, and the
 * decorations (seal, code, frame) reserve their space first. Nothing is ever clipped or
 * covered - the card is allowed to look quieter, never to lose a word.
 *
 * <p>Dates come from the device clock and locale, so the card is always the card of today.
 */
public class ShareQuoteCard {

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
        public String day = "";
        public String monthLabel = "";
        public String year = "";
        public String weekday = "";
        public String dateLine = "";
        public String monthYearLine = "";
        public String verticalDate = "";
    }

    /** A block of text that has been fitted: its lines, size and line step. */
    private static final class Block {
        final String[] lines;
        final float size;
        final float leading;

        Block(String[] lines, float size, float leading) {
            this.lines = lines;
            this.size = size;
            this.leading = leading;
        }

        float height() {
            return lines.length * leading;
        }
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
        TextPaint big = paint(Typeface.create(sans, Typeface.BOLD), 200, INK);
        Rect bounds = new Rect();
        big.getTextBounds(texts.day, 0, texts.day.length(), bounds);
        c.drawText(texts.day, 84 - bounds.left, 150 - bounds.top, big);
        textTop(c, texts.year, 86, 372, 58, sans, INK);
        textTop(c, texts.weekday, 86, 446, 26, serif, MUTED);
        line(c, 86, 512, W - 86, 512, HAIR_LIGHT, 1);

        final float left = 86, maxW = 800;          // the seal starts at x=910
        final float quoteTop = 540, quoteBottom = H - 330;

        Block title = fit("《" + texts.title + "》", maxW, 2, 30, 18, 1.15f, serif);
        Block meta = fit(join(texts.author, " · ", texts.chapter), maxW, 2, 22, 14, 1.3f, serif);
        float reserved = 34 + title.height() + 16 + meta.height() + 26;
        Block quote = fit(texts.quote, maxW, quoteBottom - quoteTop - reserved, 48, 20, 80f / 48f, serif);

        float y = quoteTop + Math.max(0, (quoteBottom - quoteTop - reserved - quote.height()) / 2f);
        drawBlock(c, quote, left, y, serif, INK);
        y += quote.height() + 34;
        drawBlock(c, title, left, y, serif, INK);
        y += title.height() + 16;
        drawBlock(c, meta, left, y, serif, MUTED);

        seal(c, W - 86 - 84, quoteTop + Math.max(0, (quoteBottom - quoteTop - 104) / 2f), 84, "書");
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

        // A title set down the spine only works while the characters stay readable. Past
        // that it is set across the top of the dark column instead - smaller, never clipped.
        float titleTop = 150, titleBottom = H - 380;
        float contentTop = 220;
        int titleChars = texts.title.trim().length();
        float titleStep = (titleBottom - titleTop) / Math.max(1, titleChars);
        boolean spineVertical = titleStep >= 34;
        if (spineVertical) {
            float titleSize = Math.min(74, titleStep * 0.84f);
            float ty = titleTop;
            for (char ch : texts.title.toCharArray()) {
                textTop(c, String.valueOf(ch), 54, ty, titleSize, Typeface.create(serif, Typeface.BOLD), 0xFFF7E3D9);
                ty += titleStep;
            }
            int authorChars = Math.max(1, texts.author.trim().length());
            float authorStep = 380f / authorChars;
            if (authorStep >= 16) {
                float authorSize = Math.min(22, authorStep * 0.7f);
                float ay = H - 360;
                for (char ch : texts.author.toCharArray()) {
                    textTop(c, String.valueOf(ch), 54, ay, authorSize, serif, 0xFFE4C2B4);
                    ay += authorStep;
                }
            } else {
                drawBlock(c, fit(texts.author, spineW - 90, 3, 18, 12, 1.3f, serif),
                        54, H - 220, serif, 0xFFE4C2B4);
            }
        } else {
            float top = 200, maxW = spineW + 470;
            Block title = fit(texts.title, maxW, 3, 46, 22, 1.18f, Typeface.create(serif, Typeface.BOLD));
            drawBlock(c, title, spineW + 70, top, Typeface.create(serif, Typeface.BOLD), 0xFFF7E3D9);
            Block author = fit(texts.author, maxW, 3, 20, 13, 1.3f, serif);
            drawBlock(c, author, spineW + 70, top + title.height() + 18, serif, 0xFFE4C2B4);
            // the quote starts under the title that fell back to horizontal
            contentTop = Math.max(contentTop, top + title.height() + 18 + author.height() + 40);
        }
        int rx = W - 120;
        float dy = 120;
        for (char ch : texts.verticalDate.toCharArray()) {
            textTop(c, String.valueOf(ch), rx, dy, 24, serif, NIGHT_MUTED);
            dy += 34;
        }
        line(c, rx - 40, 120, rx - 40, 700, HAIR_DARK, 1);

        int textX = spineW + 70;
        float maxW = (rx - 60) - textX;
        float top = contentTop, bottom = H - 320;
        Block meta = fit(pathLine(texts.chapter), maxW, 2, 24, 14, 1.35f, serif);
        float reserved = 26 + meta.height() + 20 + 74 + 20;
        Block quote = fit(texts.quote, maxW, bottom - top - reserved, 44, 18, 78f / 44f, serif);

        float qy = top + Math.max(0, (bottom - top - reserved - quote.height()) / 2f);
        drawBlock(c, quote, textX, qy, serif, NIGHT_TEXT);
        qy += quote.height() + 26;
        drawBlock(c, meta, textX, qy, serif, NIGHT_MUTED);
        qy += meta.height() + 20;
        seal(c, textX, qy, 74, "書");
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
            Paint lamp = new Paint();
            lamp.setShader(new RadialGradient(W / 2f, bandH * 0.42f, W * 0.62f,
                    new int[]{0x33FFD9B0, 0x00000000}, new float[]{0f, 1f}, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, bandH, lamp);
        }

        // The title is set down the band. If it cannot fit vertically at a readable size it
        // is set across the top instead - the one layout exception, and it never truncates.
        float titleTop = 96, titleBottom = bandH - 180;
        float step = stepFor(texts.title, titleTop, titleBottom);
        Block author = fit(texts.author, 768, 2, 24, 16, 1.3f, serif);
        if (step >= 30) {
            float size = Math.min(64, Math.max(26, step * 0.84f));
            float y = titleTop;
            for (char ch : texts.title.toCharArray()) {
                textTop(c, String.valueOf(ch), 74, y, size, Typeface.create(serif, Typeface.BOLD), 0xFFFBF3EC);
                y += step;
            }
            drawBlock(c, author, 74, y + 12, serif, 0xFFECD2C4);
        } else {
            Block title = fit(texts.title, 768, 3, 54, 24, 1.2f, Typeface.create(serif, Typeface.BOLD));
            drawBlock(c, title, 74, titleTop, Typeface.create(serif, Typeface.BOLD), 0xFFFBF3EC);
            drawBlock(c, author, 74, titleTop + title.height() + 16, serif, 0xFFECD2C4);
        }
        seal(c, W - 74 - 74, bandH - 74 - 74, 74, "讀");

        float quoteTop = bandH + 76, quoteBottom = (H - 296) - 24;
        float maxW = W - 164;
        Block chapter = fit(texts.chapter, maxW, 2, 24, 14, 1.35f, serif);
        Block meta = fit(join(texts.author, " · ", texts.dateLine), maxW, 2, 20, 13, 1.35f, serif);
        float reserved = 22 + chapter.height() + 14 + meta.height();
        Block quote = fit(texts.quote, maxW, quoteBottom - quoteTop - reserved, 46, 18, 80f / 46f, serif);

        float qy = quoteTop;
        drawBlock(c, quote, 82, qy, serif, INK);
        qy += quote.height() + 22;
        drawBlock(c, chapter, 82, qy, serif, MUTED);
        qy += chapter.height() + 14;
        drawBlock(c, meta, 82, qy, serif, MUTED);
        stub(c, b, false, M, "paper", "paper", 108);
        grain(b, 0.08f);
    }

    // ---------------------------------------------------------------- 4 朱砂框 · 纸

    private void cinnabarFrame(Canvas c, Bitmap b) {
        c.drawColor(PAPER);
        int m = 56;
        frame(c, m, m, W - m, H - m, CINNABAR, 3);
        frame(c, m + 10, m + 10, W - m - 10, H - m - 10, 0xFFE2D1C2, 1);

        // the date sits left of the seal and must never run under it
        float sealX = W - m - 40 - 70;
        Block dateLine = fit(cjkLine(texts), sealX - (m + 40) - 16, 2, 21, 14, 1.3f, serif);
        drawBlock(c, dateLine, m + 40, m + 44, serif, MUTED);
        seal(c, sealX, m + 36, 70, "書");

        float innerW = W - 2 * m - 120;
        float top = m + 120;
        Block title = fit("《" + texts.title + "》", W - 2 * m - 40, 2, 32, 20, 1.25f, serif);
        Block author = fit(texts.author, W - 2 * m - 40, 2, 24, 16, 1.3f, serif);
        float reserved = 10 + 2 + title.height() + 16 + author.height();
        Block quote = fit(texts.quote, innerW, (H - m - 60) - top - reserved, 52, 20, 88f / 52f, serif);

        float y = top + Math.max(0, ((H - m - 60) - top - reserved - quote.height()) / 2f);
        float quoteH = quote.height();
        drawBlockCentered(c, quote, y, serif, INK);
        y += quoteH + 16;
        line(c, W / 2f - 40, y, W / 2f + 40, y, CINNABAR, 2);
        drawBlockCentered(c, title, y + 14, serif, INK);
        drawBlockCentered(c, author, y + 14 + title.height() + 12, serif, MUTED);
        stub(c, b, false, M, "paper", "paper", 100);
        grain(b, 0.09f);
    }

    // ---------------------------------------------------------------- 5 画廊极简 · 夜

    private void gallery(Canvas c, Bitmap b) {
        c.drawColor(NIGHT);
        // The oversized opening quote mark owns the top-left corner; text starts below it.
        textTop(c, "“", 104, 210, 330, Typeface.create(serif, Typeface.BOLD), 0xFF3B2C27);
        float quoteTop = 540, quoteBottom = H - 320;
        float maxW = 800;
        Block title = fit("《" + texts.title + "》", 754, 2, 30, 18, 1.2f, serif);
        Block meta = fit(join(texts.author, " · ", texts.chapter), 754, 2, 22, 14, 1.3f, serif);
        float reserved = 34 + 3 + title.height() + 16 + meta.height();
        Block quote = fit(texts.quote, maxW, quoteBottom - quoteTop - reserved, 54, 20, 92f / 54f, serif);

        float y = quoteTop + Math.max(0, (quoteBottom - quoteTop - reserved - quote.height()) / 2f);
        drawBlock(c, quote, 150, y, serif, NIGHT_TEXT);
        y += quote.height() + 34;
        line(c, 150, y, 150 + 64, y, ACCENT, 3);
        y += 3;
        drawBlock(c, title, 150, y + 12, serif, NIGHT_TEXT);
        drawBlock(c, meta, 150, y + 12 + title.height() + 16, serif, NIGHT_MUTED);
        seal(c, W - 82 - 78, y + 20, 78, "讀");
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

        for (int x = left; x < W - 84; x += 18) {
            line(c, x, perfY, Math.min(x + 9, W - 84), perfY, hair, 2);
        }
        Paint notch = new Paint(Paint.ANTI_ALIAS_FLAG);
        notch.setColor(notchColor(leftNotch));
        c.drawCircle(0, perfY, 13, notch);
        notch.setColor(notchColor(rightNotch));
        c.drawCircle(W, perfY, 13, notch);

        float qrCx = qrX + qrSize / 2f;
        float label = 16f;
        while (label > 11f && trackedWidth(texts.scan, label, 0.10f, sans) > qrSize + 26) {
            label -= 1f;
        }
        drawTrackedCentered(c, texts.scan, qrCx, qrY - 18 - label, label, muted, 0.10f, sans);
        Bitmap code = qr(qrSize, dark);
        if (code != null) c.drawBitmap(code, qrX, qrY, null);
        drawTrackedCentered(c, texts.urlLabel, qrCx, qrY + qrSize + 10, label, muted, 0.06f, sans);

        colophon(c, dark, left, qrY - 34, qrY + qrSize, qrX - left - 46);
    }

    private int notchColor(String side) {
        if ("spine".equals(side)) return CINNABAR;
        if ("night".equals(side)) return NIGHT;
        return PAPER;
    }

    /** Three tiers, fitted so they can never grow down into the code. */
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

        Block specBlock = specs.length() == 0
                ? new Block(new String[0], 14, 22)
                : fit(specs, maxw, 3, 14, 10, 22f / 14f, sans);
        float specArea = specBlock.lines.length == 0 ? 0 : 8 + specBlock.height();
        // brand 26 + kicker 18 + the claim line step; shrink the claim until the column fits
        float claimArea = (bottomY - topY) - 18 - 26 - specArea;
        Block claimBlock = fit(claim, maxw, 99, 23, 15, 34f / 23f, serif);
        if (claimBlock.height() > claimArea && claimArea > 0) {
            claimBlock = fitToHeight(claim, maxw, claimArea, 23, 12, 34f / 23f, serif);
        }

        float y = topY;
        rect(c, left, y, left + 34, y + 4, accent);
        y += 18;
        drawTrackedLeft(c, brand, left, y, 19, accent, 0.14f, serif);
        y += 26;
        drawBlock(c, claimBlock, left, y, serif, strong);
        y += claimBlock.height();
        if (specBlock.lines.length > 0) {
            y += 8;
            for (int i = 0; i < specBlock.lines.length; i++) {
                drawTrackedLeft(c, specBlock.lines[i], left, y + i * specBlock.leading,
                        specBlock.size, fg, 0.05f, sans);
            }
        }
    }

    // ---------------------------------------------------------------- fitting

    /** Fits text into a width and height: the largest size whose wrapped lines fit both. */
    private Block fit(String text, float maxWidth, float maxHeight, float maxSize, float minSize,
                      float leadingRatio, Typeface face) {
        if (text == null || text.trim().length() == 0) return new Block(new String[0], maxSize, maxSize * leadingRatio);
        for (float size = maxSize; size >= minSize; size -= 1f) {
            Block block = wrapAt(text, size, leadingRatio, maxWidth, face);
            if (block.height() <= maxHeight + 0.5f) return block;
        }
        // Still too long at the smallest size: keep going rather than clip a word.
        for (float size = minSize - 1f; size >= 9f; size -= 1f) {
            Block block = wrapAt(text, size, leadingRatio, maxWidth, face);
            if (block.height() <= maxHeight + 0.5f) return block;
        }
        return wrapAt(text, 9f, leadingRatio, maxWidth, face);
    }

    /** Fits text into a width and a number of lines (titles and bylines). */
    private Block fit(String text, float maxWidth, int maxLines, float maxSize, float minSize,
                      float leadingRatio, Typeface face) {
        if (text == null || text.trim().length() == 0) {
            return new Block(new String[0], maxSize, maxSize * leadingRatio);
        }
        for (float size = maxSize; size >= minSize; size -= 1f) {
            Block block = wrapAt(text, size, leadingRatio, maxWidth, face);
            if (block.lines.length <= maxLines) return block;
        }
        // At the floor, allow the extra lines: a wrapped title is readable, a clipped one is not.
        return wrapAt(text, minSize, leadingRatio, maxWidth, face);
    }

    /** Fits by height alone, used when the column has fixed room for the claim. */
    private Block fitToHeight(String text, float maxWidth, float maxHeight, float maxSize,
                              float minSize, float leadingRatio, Typeface face) {
        for (float size = maxSize; size >= 9f; size -= 1f) {
            Block block = wrapAt(text, size, leadingRatio, maxWidth, face);
            if (block.height() <= maxHeight + 0.5f) return block;
        }
        return wrapAt(text, 9f, leadingRatio, maxWidth, face);
    }

    private Block wrapAt(String text, float size, float leadingRatio, float maxWidth, Typeface face) {
        TextPaint p = paint(face, size, INK);
        String[] lines = wrap(p, text, maxWidth);
        return new Block(lines, size, size * leadingRatio);
    }

    private float stepFor(String text, float top, float bottom) {
        int count = text == null ? 0 : Math.max(1, text.trim().length());
        return Math.min(88f, Math.max(20f, (bottom - top) / count));
    }

    // ---------------------------------------------------------------- 绘制工具

    private void drawBlock(Canvas c, Block block, float x, float topY, Typeface face, int color) {
        for (int i = 0; i < block.lines.length; i++) {
            textTop(c, block.lines[i], x, topY + i * block.leading, block.size, face, color);
        }
    }

    private void drawBlockCentered(Canvas c, Block block, float topY, Typeface face, int color) {
        for (int i = 0; i < block.lines.length; i++) {
            float w = measure(block.lines[i], face, block.size);
            textTop(c, block.lines[i], (W - w) / 2f, topY + i * block.leading, block.size, face, color);
        }
    }

    private void textTop(Canvas c, String s, float x, float topY, float size, Typeface face, int color) {
        if (s == null || s.length() == 0) return;
        TextPaint p = paint(face, size, color);
        c.drawText(s, x, topY - p.getFontMetrics().ascent, p);
    }

    private void drawTrackedLeft(Canvas c, String s, float x, float topY, float size, int color,
                                 float track, Typeface face) {
        if (s == null || s.length() == 0) return;
        TextPaint p = paint(face, size, color);
        float cx = x;
        float baseline = topY - p.getFontMetrics().ascent;
        for (char ch : s.toCharArray()) {
            c.drawText(String.valueOf(ch), cx, baseline, p);
            cx += p.measureText(String.valueOf(ch)) + size * track;
        }
    }

    private void drawTrackedCentered(Canvas c, String s, float centerX, float topY, float size,
                                     int color, float track, Typeface face) {
        if (s == null || s.length() == 0) return;
        float w = trackedWidth(s, size, track, face);
        drawTrackedLeft(c, s, centerX - w / 2f, topY, size, color, track, face);
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
            int minX = m.getWidth(), minY = m.getHeight(), maxX = -1, maxY = -1;
            for (int y = 0; y < m.getHeight(); y++) {
                for (int x = 0; x < m.getWidth(); x++) {
                    if (m.get(x, y)) {
                        minX = Math.min(minX, x); maxX = Math.max(maxX, x);
                        minY = Math.min(minY, y); maxY = Math.max(maxY, y);
                    }
                }
            }
            if (maxX < 0) return null;
            int modules = Math.max(maxX - minX + 1, maxY - minY + 1);
            float scale = size / (float) modules;
            qr = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(qr);
            Paint modulePaint = new Paint();
            modulePaint.setColor(dark ? 0xFFF2E7E2 : 0xFF201814);
            modulePaint.setAntiAlias(false);
            for (int y = minY; y <= maxY; y++) {
                int top = Math.round((y - minY) * scale);
                int bottom = Math.round((y - minY + 1) * scale);
                for (int x = minX; x <= maxX; x++) {
                    if (m.get(x, y)) {
                        int left = Math.round((x - minX) * scale);
                        int right = Math.round((x - minX + 1) * scale);
                        c.drawRect(left, top, right, bottom, modulePaint);
                    }
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
        c.drawText(glyph, x + (size - bounds.width()) / 2f - bounds.left,
                y + (size - bounds.height()) / 2f - bounds.top, t);
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
        if (body.indexOf(' ') >= 0 && !hasCjk(body)) {
            return wrapWords(p, body, maxWidth);
        }
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

    /** Latin text breaks between words, because a word split in half is not a word. */
    private String[] wrapWords(TextPaint p, String body, float maxWidth) {
        String[] words = body.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : words) {
            String candidate = cur.length() == 0 ? word : cur + " " + word;
            if (p.measureText(candidate) > maxWidth && cur.length() > 0) {
                lines.add(cur.toString());
                cur.setLength(0);
                cur.append(word);
            } else {
                cur.setLength(0);
                cur.append(candidate);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines.toArray(new String[0]);
    }

    private boolean hasCjk(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= 0x2E80 && ch <= 0x9FFF) return true;
        }
        return false;
    }

    private String join(String a, String sep, String b) {
        String x = a == null ? "" : a.trim();
        String y = b == null ? "" : b.trim();
        if (x.length() == 0) return y;
        if (y.length() == 0) return x;
        return x + sep + y;
    }

    private String pathLine(String chapter) {
        String c = chapter == null ? "" : chapter.trim();
        return c.length() == 0 ? "" : "/ " + c;
    }

    private boolean cjk() {
        String lang = Locale.getDefault().getLanguage();
        return "zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang);
    }

    private String cjkLine(Texts t) {
        return cjk() && t.monthYearLine != null && t.monthYearLine.length() > 0
                ? "摘录 · " + t.monthYearLine
                : (t.monthYearLine != null && t.monthYearLine.length() > 0 ? t.monthYearLine : t.dateLine);
    }

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
