# -*- coding: utf-8 -*-
"""OmniShelf share cards, art direction pass.

Ideas behind this pass: a cinnabar seal as the one recognisable mark, paper grain and a
night vignette so a card looks printed rather than generated, a perforated "ticket stub"
for the QR, and Songti for the quote so it reads like a book rather than a notification.
"""
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import pathlib, random, math

OUT = pathlib.Path("docs/share-card/v3"); OUT.mkdir(parents=True, exist_ok=True)
W, H = 1080, 1440

PAPER = (0xFB, 0xF8, 0xF2)
PAPER_DEEP = (0xF3, 0xEC, 0xE0)
INK = (0x24, 0x18, 0x15)
MUTED = (0x7C, 0x6B, 0x63)
NIGHT = (0x15, 0x14, 0x12)
NIGHT_TEXT = (0xEF, 0xE4, 0xDC)
NIGHT_MUTED = (0xA3, 0x93, 0x8B)
CINNABAR = (0x8E, 0x2B, 0x21)
CINNABAR_LIGHT = (0xB4, 0x44, 0x33)
ACCENT = (0xD2, 0x50, 0x3C)
WARM = (0xE7, 0xBE, 0x96)
HAIR_DARK = (0x39, 0x2D, 0x28)
HAIR_LIGHT = (0xD8, 0xCB, 0xBE)

CJK = "/System/Library/Fonts/Hiragino Sans GB.ttc"
SONG = "/System/Library/Fonts/Supplemental/Songti.ttc"
HELV = "/System/Library/Fonts/HelveticaNeue.ttc"
def f_cjk(sz, bold=False): return ImageFont.truetype(CJK, sz, index=2 if bold else 0)
def f_song(sz, weight=0):
    # Songti: 0 regular, 1 bold, 2 light; fall back to Hiragino if the face is missing
    try: return ImageFont.truetype(SONG, sz, index=weight)
    except Exception: return f_cjk(sz, weight > 0)
def f_helv(sz, bold=False):
    try: return ImageFont.truetype(HELV, sz, index=1 if bold else 0)
    except Exception: return f_cjk(sz, bold)

QUOTE = "读书不是把眼睛放在纸上，而是把思想放在文字之外。一个人读得多并不稀奇，稀奇的是他把书读薄了。"
TITLE, AUTHOR, CHAPTER = "读书的方法", "佚名", "第一章 阅读的姿势"
DATE_SLASH, WEEKDAY = "2026/11/21", "星期六"
INTRO_ZH = "OmniShelf：一个不给你看广告的阅读器，34 种格式、离线朗读、55 种语言。"
INTRO_EN = "OmniShelf: a reader with no ads. 34 formats, offline text-to-speech, 55 languages."
SCAN_ZH, SCAN_EN = "扫码 · 读同款", "Scan to read"

NO_LINE_START = "，。、；：！？）》」』】…·,.;:!?)]}"

def wrap_cjk(text, font, maxw, d):
    lines, cur = [], ""
    for ch in text:
        if d.textlength(cur + ch, font=font) > maxw and cur:
            if ch in NO_LINE_START:
                lines.append(cur[:-1]); cur = cur[-1] + ch
            else:
                lines.append(cur); cur = ch
        else:
            cur += ch
    if cur: lines.append(cur)
    # a two-character tail looks like a mistake; borrow two characters from the line above
    if len(lines) > 1 and len(lines[-1]) <= 2:
        prev = lines[-2]
        move = prev[-2:]
        if len(prev) > 4:
            lines[-2] = prev[:-2]
            lines[-1] = move + lines[-1]
    return lines

def letterspaced(d, text, x, y, font, fill, track=0.18):
    """Draws text with tracking, returns the width."""
    cx = x
    for ch in text:
        d.text((cx, y), ch, font=font, fill=fill)
        cx += d.textlength(ch, font=font) + font.size * track
    return cx - x - font.size * track

def paper_grain(im, alpha=10):
    """A whisper of noise: enough to feel printed, not enough to notice."""
    noise = Image.effect_noise((W, H), 22).convert("L").point(lambda v: 128 + (v - 128) // 6)
    grain = Image.merge("RGB", (noise, noise, noise))
    return Image.blend(im, grain, alpha / 100.0)

def vignette(im, strength=0.55):
    mask = Image.new("L", (W, H), 0)
    md = ImageDraw.Draw(mask)
    for i in range(60):
        v = int(255 * (i / 60) ** 1.5 * strength)
        md.rectangle([i * 6, i * 6, W - i * 6, H - i * 6], outline=v, width=6)
    dark = Image.new("RGB", (W, H), (0, 0, 0))
    return Image.composite(dark, im, mask.filter(ImageFilter.GaussianBlur(40)))

def seal(im, x, y, size=86, text="書", alpha=255):
    """A cinnabar seal with a white character - the one mark that says OmniShelf."""
    seal_im = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    sd = ImageDraw.Draw(seal_im)
    sd.rounded_rectangle([1, 1, size - 2, size - 2], radius=8,
                         fill=CINNABAR + (alpha,))
    # slightly rough edge, like a stamp that did not press evenly
    random.seed(7)
    for _ in range(90):
        px, py = random.randint(0, size - 1), random.randint(0, size - 1)
        sd.point((px, py), fill=(0xFB, 0xF8, 0xF2, max(0, 40 - random.randint(0, 40))))
    font = f_song(int(size * 0.56), 1)
    tw = sd.textlength(text, font=font)
    bbox = font.getbbox(text)
    th = bbox[3] - bbox[1]
    sd.text(((size - tw) / 2, (size - th) / 2 - bbox[1]), text, font=font,
            fill=(0xFB, 0xF8, 0xF2, alpha))
    im.paste(seal_im, (x, y), seal_im)

def wordmark(d, x, y, fill, size=22):
    f = f_helv(size, False)
    letterspaced(d, "OMNISHELF", x, y, f, fill, track=0.28)

def stub(im, d, intro, dark, qr_path, cta, qr_size=112, notches=("paper", "paper")):
    """The bottom of the card, a ticket stub: perforation, intro on the left, QR on the right.

    There is deliberately no wordmark down here - the intro already opens with the name,
    and a second one inside the same 200 pixels read as repetition.
    """
    fg = NIGHT_MUTED if dark else MUTED
    line = HAIR_DARK if dark else HAIR_LIGHT
    qr_x = W - 84 - qr_size
    qr_y = H - 84 - qr_size
    perforation_y = H - 296
    x = 84
    while x < W - 84:
        d.line([(x, perforation_y), (min(x + 9, W - 84), perforation_y)], fill=line, width=2)
        x += 18
    edge = {"paper": (0xFB, 0xF8, 0xF2), "night": NIGHT, "spine": CINNABAR, "none": None}
    for cx, side in ((0, notches[0]), (W, notches[1])):
        fill = edge.get(side)
        if fill is not None:
            d.ellipse([cx - 13, perforation_y - 13, cx + 13, perforation_y + 13], fill=fill)
    # CTA right-aligned over the QR
    cf = f_song(19, 0)
    cw = d.textlength(cta, font=cf)
    d.text((qr_x + qr_size - cw, qr_y - 34), cta, font=cf, fill=fg)
    qr = Image.open(qr_path).convert("RGB").resize((qr_size, qr_size), Image.LANCZOS)
    im.paste(qr, (qr_x, qr_y))
    cap = f_helv(15, False)
    capw = d.textlength("omnishelf.cc", font=cap)
    d.text((qr_x + (qr_size - capw) / 2, qr_y + qr_size + 7), "omnishelf.cc", font=cap, fill=fg)
    # intro: two lines at most, bottom aligned with the QR
    font = f_cjk(22)
    maxw = qr_x - 84 - 48
    lines = wrap_cjk(intro, font, maxw, d)
    if len(lines) > 1:
        cut = intro.rfind("，")
        if cut > 0:
            a, b = intro[:cut + 1], intro[cut + 1:].strip()
            if d.textlength(a, font=font) <= maxw and d.textlength(b, font=font) <= maxw:
                lines = [a, b]
    y0 = qr_y + qr_size - (len(lines) - 1) * 31
    for i, line in enumerate(lines):
        d.text((84, y0 + i * 31), line, font=font, fill=fg)

def rule(d, x1, y, x2, fill, w=1):
    """Horizontal hairline; y may be passed as the fourth argument."""
    if x2 is None:
        return
    d.line([(x1, y), (x2, y)], fill=fill, width=w)

# ---------------------------------------------------------------- 1 日期 · 纸 · 印章
def card1(intro, name="01-date-paper", tag="date"):
    im = Image.new("RGB", (W, H), PAPER)
    d = ImageDraw.Draw(im)
    rule(d, 84, 92, 84 + 46, CINNABAR, 2)
    d.text((84, 116), "NOVEMBER", font=f_helv(22, False), fill=MUTED)
    big = f_helv(200, True)
    bbox = big.getbbox("21")
    d.text((84 - bbox[0], 150 - bbox[1]), "21", font=big, fill=INK)
    d.text((86, 372), "2026", font=f_helv(58, False), fill=INK)
    d.text((86, 446), WEEKDAY, font=f_song(26, 0), fill=MUTED)
    rule(d, 86, 512, W - 86, HAIR_LIGHT, 1)

    qf = f_song(48, 0)
    lines = wrap_cjk(QUOTE, qf, 800, d)
    quote_h = len(lines) * 80
    meta_h = 118
    block_h = quote_h + meta_h
    top, bottom = 540, H - 330            # the stub starts at H-250
    y = top + max(24, (bottom - top - block_h) // 2)
    for i, line in enumerate(lines):
        d.text((86, y + i * 80), line, font=qf, fill=INK)
    y += quote_h
    d.text((86, y + 34), "《" + TITLE + "》", font=f_song(30, 1), fill=INK)
    d.text((86, y + 82), AUTHOR + " · " + CHAPTER, font=f_song(22, 0), fill=MUTED)
    # the seal closes the block, level with the book line
    seal(im, W - 86 - 84, y + 20, 84)
    stub(im, d, intro, dark=False, qr_path="docs/share-card/v2/qr-paper.png", cta=SCAN_ZH)
    im = paper_grain(im, 5)
    im.save(OUT / f"{name}.png"); return im

# ---------------------------------------------------------------- 2 封面票根 · 夜
def card2(intro, name="02-cover-ticket-night", cta=SCAN_ZH):
    im = Image.new("RGB", (W, H), NIGHT); d = ImageDraw.Draw(im)
    # cover plate
    plate = (132, 118, W - 132, 704)
    cover = Image.new("RGB", (plate[2] - plate[0], plate[3] - plate[1]))
    cd = ImageDraw.Draw(cover)
    for i in range(cover.height):
        t = i / cover.height
        cd.line([(0, i), (cover.width, i)],
                fill=(min(255, int(0x7E + 90 * t)), min(255, int(0x26 + 55 * t)), min(255, int(0x1E + 44 * t))))
    cd.text((40, 40), "读书的方法", font=f_song(30, 1), fill=(0xF6, 0xE2, 0xD8))
    # a thin keyline inside the plate
    cd.rectangle([14, 14, cover.width - 15, cover.height - 15], outline=(0xF6, 0xE2, 0xD8), width=1)
    im.paste(cover, (plate[0], plate[1]))
    seal(im, plate[0] - 40, plate[1] - 40, 80)
    d.text((82, 742), "摘录于 " + DATE_SLASH, font=f_song(22, 0), fill=NIGHT_MUTED)
    qf = f_song(46, 0)
    y = 796
    lines = wrap_cjk(QUOTE, qf, W - 164, d)
    for i, line in enumerate(lines):
        d.text((82, y + i * 78), line, font=qf, fill=NIGHT_TEXT)
    y += len(lines) * 78
    d.text((82, y + 16), "/ " + TITLE + " · " + CHAPTER, font=f_song(24, 0), fill=NIGHT_MUTED)
    stub(im, d, intro, dark=True, qr_path="docs/share-card/v2/qr-night.png", cta=cta, qr_size=108)
    im = vignette(im, 0.5)
    im = paper_grain(im, 5)
    im.save(OUT / f"{name}.png"); return im

# ---------------------------------------------------------------- 3 竖排书脊 · 夜
def card3(intro, name="03-vertical-spine-night"):
    im = Image.new("RGB", (W, H), NIGHT); d = ImageDraw.Draw(im)
    # left spine band in cinnabar, the way a cloth-bound book is bound
    spine_w = 176
    spine = Image.new("RGB", (spine_w, H))
    sd = ImageDraw.Draw(spine)
    for i in range(spine_w):
        t = i / spine_w
        sd.line([(i, 0), (i, H)], fill=(min(255, int(0x7A + 34 * t)), min(255, int(0x24 + 26 * t)), min(255, int(0x1B + 20 * t))))
    im.paste(spine, (0, 0))
    tf = f_song(74, 1)
    y = 150
    for ch in TITLE:
        d.text((54, y), ch, font=tf, fill=(0xF7, 0xE3, 0xD9))
        y += 88
    # author at the foot of the spine, the way a cloth spine carries it
    ay = H - 360
    for ch in AUTHOR:
        d.text((54, ay), ch, font=f_song(22, 0), fill=(0xE4, 0xC2, 0xB4))
        ay += 32
    # right date column
    rx = W - 120
    dy = 120
    for ch in "二〇二六年十一月二十一日":
        d.text((rx, dy), ch, font=f_song(24, 0), fill=NIGHT_MUTED)
        dy += 34
    rule(d, rx - 40, 120, rx - 40, HAIR_DARK, 1)
    d.line([(rx - 40, 120), (rx - 40, 700)], fill=HAIR_DARK, width=1)
    qf = f_song(44, 0)
    text_x = spine_w + 70
    lines = wrap_cjk(QUOTE, qf, (rx - 60) - text_x, d)
    quote_h = len(lines) * 78
    block_h = quote_h + 150
    top, bottom = 220, H - 320
    y = top + max(20, (bottom - top - block_h) // 2)
    for i, line in enumerate(lines):
        d.text((text_x, y + i * 78), line, font=qf, fill=NIGHT_TEXT)
    y += quote_h
    d.text((text_x, y + 26), "/ " + CHAPTER, font=f_song(24, 0), fill=NIGHT_MUTED)
    seal(im, text_x, y + 70, 74)
    stub(im, d, intro, dark=True, qr_path="docs/share-card/v2/qr-night.png", cta=SCAN_ZH, qr_size=108,
         notches=("spine", "night"))
    im = vignette(im, 0.45)
    im = paper_grain(im, 5)
    im.save(OUT / f"{name}.png"); return im

# ---------------------------------------------------------------- 4 影像封面 · 纸
def card4(intro, name="04-cover-full-paper"):
    im = Image.new("RGB", (W, H), PAPER); d = ImageDraw.Draw(im)
    band_h = 620
    cover = Image.new("RGB", (W, band_h))
    cd = ImageDraw.Draw(cover)
    for i in range(band_h):
        t = i / band_h
        cd.line([(0, i), (W, i)],
                fill=(min(255, int(0x86 + 96 * t)), min(255, int(0x28 + 62 * t)), min(255, int(0x1F + 48 * t))))
    # a warm light in the middle, like a lamp on paper
    glow = Image.new("RGB", (W, band_h), (0, 0, 0))
    gd = ImageDraw.Draw(glow)
    gd.ellipse([W * 0.15, -band_h * 0.35, W * 0.85, band_h * 1.1], fill=(26, 16, 8))
    glow = glow.filter(ImageFilter.GaussianBlur(160))
    cover = Image.blend(cover, Image.blend(cover, glow, 0.35), 0.5)
    im.paste(cover, (0, 0))
    tf = f_song(64, 1)
    y = 96
    for ch in TITLE:
        d.text((74, y), ch, font=tf, fill=(0xFB, 0xF3, 0xEC)); y += 76
    d.text((74, y + 15), AUTHOR, font=f_song(24, 0), fill=(0xEC, 0xD2, 0xC4))
    seal(im, W - 74 - 74, band_h - 74 - 74, 74, text="讀")
    qf = f_song(46, 0)
    y = band_h + 76
    for i, line in enumerate(wrap_cjk(QUOTE, qf, W - 164, d)):
        d.text((82, y + i * 80), line, font=qf, fill=INK)
    y += len(wrap_cjk(QUOTE, qf, W - 164, d)) * 80
    d.text((82, y + 22), CHAPTER, font=f_song(24, 0), fill=MUTED)
    d.text((82, y + 62), AUTHOR + " · " + DATE_SLASH, font=f_song(20, 0), fill=MUTED)
    stub(im, d, intro, dark=False, qr_path="docs/share-card/v2/qr-paper.png", cta=SCAN_ZH, qr_size=108)
    im = paper_grain(im, 8)
    im.save(OUT / f"{name}.png"); return im

# ---------------------------------------------------------------- 5 朱砂框 · 纸
def card5(intro, name="05-cinnabar-frame-paper"):
    im = Image.new("RGB", (W, H), PAPER); d = ImageDraw.Draw(im)
    m = 56
    d.rectangle([m, m, W - m, H - m], outline=CINNABAR, width=3)
    d.rectangle([m + 10, m + 10, W - m - 10, H - m - 10], outline=(0xE2, 0xD1, 0xC2), width=1)
    d.text((m + 40, m + 44), "摘录 · 二〇二六年十一月", font=f_song(21, 0), fill=MUTED)
    seal(im, W - m - 40 - 70, m + 36, 70, text="書")
    qf = f_song(52, 0)
    lines = wrap_cjk(QUOTE, qf, W - 2 * m - 120, d)
    y = (H - len(lines) * 88) // 2 - 40
    for i, line in enumerate(lines):
        d.text(((W - d.textlength(line, font=qf)) / 2, y + i * 88), line, font=qf, fill=INK)
    y += len(lines) * 88
    rule(d, W / 2 - 40, y + 40, W / 2 + 40, CINNABAR, 2)
    d.text(((W - d.textlength("《" + TITLE + "》", font=f_song(32, 1))) / 2, y + 66),
           "《" + TITLE + "》", font=f_song(32, 1), fill=INK)
    d.text(((W - d.textlength(AUTHOR, font=f_song(24, 0))) / 2, y + 116), AUTHOR,
           font=f_song(24, 0), fill=MUTED)
    stub(im, d, intro, dark=False, qr_path="docs/share-card/v2/qr-paper.png", cta=SCAN_ZH, qr_size=100)
    im = paper_grain(im, 9)
    im.save(OUT / f"{name}.png"); return im

# ---------------------------------------------------------------- 6 画廊极简 · 夜
def card6(intro, name="06-gallery-night"):
    im = Image.new("RGB", (W, H), NIGHT); d = ImageDraw.Draw(im)
    # the oversized mark sits behind the first line, half-cropped by it
    d.text((104, 210), "“", font=f_song(330, 1), fill=(0x3B, 0x2C, 0x27))
    qf = f_song(54, 0)
    lines = wrap_cjk(QUOTE, qf, 800, d)
    quote_h = len(lines) * 92
    block_h = quote_h + 230
    top, bottom = 200, H - 320
    y = top + max(20, (bottom - top - block_h) // 2)
    for i, line in enumerate(lines):
        d.text((150, y + i * 92), line, font=qf, fill=NIGHT_TEXT)
    y += quote_h
    rule(d, 150, y + 34, 150 + 64, ACCENT, 3)
    d.text((150, y + 64), "《" + TITLE + "》", font=f_song(30, 1), fill=NIGHT_TEXT)
    d.text((150, y + 112), AUTHOR + " · " + CHAPTER, font=f_song(22, 0), fill=NIGHT_MUTED)
    seal(im, W - 82 - 78, y + 40, 78, text="讀")
    stub(im, d, intro, dark=True, qr_path="docs/share-card/v2/qr-night.png", cta=SCAN_ZH, qr_size=104)
    im = vignette(im, 0.6)
    im = paper_grain(im, 5)
    im.save(OUT / f"{name}.png"); return im

cards = [fn(INTRO_ZH) for fn in (card1, card2, card3, card4, card5, card6)]
card2(INTRO_EN, "02-cover-ticket-night-en", cta=SCAN_EN)

thumb_w = 420; th = int(thumb_w * H / W)
sheet = Image.new("RGB", (thumb_w * 3 + 80, th * 2 + 60), (238, 234, 228))
for i, im in enumerate(cards):
    t = im.resize((thumb_w, th), Image.LANCZOS)
    sheet.paste(t, (20 + (i % 3) * (thumb_w + 10), 20 + (i // 3) * (th + 10)))
sheet.save(OUT / "all-six.png")
print("rendered", len(cards))
