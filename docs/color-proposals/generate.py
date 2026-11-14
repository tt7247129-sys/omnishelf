from PIL import Image, ImageDraw, ImageFont
import os

OUT = "/Users/huangpeng/Documents/deepseek-harness/default-workspace/omnishelf/docs/color-proposals"
os.makedirs(OUT, exist_ok=True)
FONT = "/System/Library/Fonts/Hiragino Sans GB.ttc"

def f(size, index=0):
    return ImageFont.truetype(FONT, size, index=index)

def lum(c):
    r, g, b = c[:3]
    def ch(v):
        v /= 255.0
        return v / 12.92 if v <= 0.03928 else ((v + 0.055) / 1.055) ** 2.4
    return 0.2126 * ch(r) + 0.7152 * ch(g) + 0.0722 * ch(b)

def ratio(a, b):
    la, lb = lum(a), lum(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)

def hexs(c):
    return "#%02X%02X%02X" % tuple(c[:3])

SCHEMES = [
    dict(
        key="A", name="墨绿·深顶栏", ref="延续现有品牌绿,加深顶栏让图标纯白凸显(近多看/掌阅的深色顶栏感)",
        bar=(0x24, 0x31, 0x2A), icon=(0xFF, 0xFF, 0xFF), tab=(0x24, 0x31, 0x2A),
        tablabel=(0xFF, 0xFF, 0xFF), accent=(0x2E, 0x7D, 0x5B), accent_text=(0xFF, 0xFF, 0xFF), surface=(0xFF, 0xFF, 0xFF),
        text=(0x1B, 0x1B, 0x1B), sub=(0x6B, 0x6B, 0x6B), divider=(0xD8, 0xDD, 0xD9),
    ),
    dict(
        key="B", name="象牙白·微信读书风", ref="浅色顶栏 + 深色图标 + 绿色点缀(微信读书/多看阅读的常见做法)",
        bar=(0xFF, 0xFF, 0xFF), icon=(0x1A, 0x1A, 0x1A), tab=(0xFF, 0xFF, 0xFF),
        tablabel=(0x1A, 0x1A, 0x1A), accent=(0x1F, 0x6B, 0x4C), accent_text=(0xFF, 0xFF, 0xFF), surface=(0xF7, 0xF7, 0xF5),
        text=(0x1A, 0x1A, 0x1A), sub=(0x6B, 0x6B, 0x6B), divider=(0xE6, 0xE6, 0xE3),
    ),
    dict(
        key="C", name="靛青·QQ阅读风", ref="深蓝顶栏 + 蓝紫强调(QQ阅读一类年轻化配色)",
        bar=(0x16, 0x20, 0x2E), icon=(0xFF, 0xFF, 0xFF), tab=(0x16, 0x20, 0x2E),
        tablabel=(0xE8, 0xED, 0xF5), accent=(0x2F, 0x6F, 0xD8), accent_text=(0xFF, 0xFF, 0xFF), surface=(0xFF, 0xFF, 0xFF),
        text=(0x16, 0x20, 0x2E), sub=(0x6B, 0x74, 0x82), divider=(0xDD, 0xE3, 0xEC),
    ),
    dict(
        key="D", name="朱砂·掌阅风", ref="暖红顶栏 + 金色强调(掌阅/番茄一类暖色系,阅读氛围强)",
        bar=(0x8E, 0x2B, 0x21), icon=(0xFF, 0xFF, 0xFF), tab=(0x8E, 0x2B, 0x21),
        tablabel=(0xFF, 0xFF, 0xFF), accent=(0xC8, 0x3A, 0x2A), accent_text=(0xFF, 0xFF, 0xFF), surface=(0xFF, 0xFD, 0xF8),
        text=(0x2A, 0x1B, 0x18), sub=(0x7A, 0x6A, 0x63), divider=(0xEC, 0xDF, 0xD8),
    ),
    dict(
        key="E", name="石墨·极简", ref="中性深灰 + 琥珀高亮(接近 Kindle/极简阅读器的克制风格)",
        bar=(0x26, 0x26, 0x26), icon=(0xFF, 0xFF, 0xFF), tab=(0x26, 0x26, 0x26),
        tablabel=(0xF2, 0xF2, 0xF2), accent=(0x8A, 0x5E, 0x14), accent_text=(0xFF, 0xFF, 0xFF), surface=(0xFA, 0xFA, 0xF8),
        text=(0x1F, 0x1F, 0x1F), sub=(0x70, 0x70, 0x70), divider=(0xE2, 0xE2, 0xE0),
    ),
]

TABS = ["最近", "书库", "文件夹", "收藏", "书签"]

def draw_icon(d, kind, cx, cy, s, col, w=6):
    if kind == "menu":
        for i in (-1, 0, 1):
            d.line([(cx - s, cy + i * s * 0.55), (cx + s, cy + i * s * 0.55)], fill=col, width=w)
    elif kind == "search":
        r = s * 0.75
        d.ellipse([cx - r, cy - r, cx + r, cy + r], outline=col, width=w)
        d.line([(cx + r * 0.7, cy + r * 0.7), (cx + s, cy + s)], fill=col, width=w)
    elif kind == "grid":
        r = s * 0.45
        for dx in (-1, 1):
            for dy in (-1, 1):
                d.rounded_rectangle([cx + dx * s * 0.52 - r, cy + dy * s * 0.52 - r,
                                     cx + dx * s * 0.52 + r, cy + dy * s * 0.52 + r],
                                    radius=4, fill=col)
    elif kind == "moon":
        d.arc([cx - s, cy - s, cx + s, cy + s], 120, 300, fill=col, width=w)
        d.arc([cx - s * 0.7, cy - s * 1.1, cx + s * 0.9, cy + s * 0.5], 150, 30, fill=col, width=w)
    elif kind == "sort":
        d.line([(cx - s, cy - s * 0.6), (cx - s, cy + s), ], fill=col, width=w)
        d.polygon([(cx - s, cy + s * 0.9), (cx - s * 0.55, cy + s * 0.3), (cx - s * 1.45, cy + s * 0.3)], fill=col)
        d.line([(cx, cy - s * 0.9), (cx, cy + s * 0.7)], fill=col, width=w)
        d.line([(cx + s * 0.6, cy - s * 0.9), (cx + s * 0.6, cy + s * 0.7)], fill=col, width=w)
    elif kind == "refresh":
        r = s * 0.85
        d.arc([cx - r, cy - r, cx + r, cy + r], 30, 300, fill=col, width=w)
        d.polygon([(cx + r * 0.75, cy - r * 0.75), (cx + r * 1.25, cy - r * 0.25),
                   (cx + r * 0.45, cy - r * 0.15)], fill=col)
    elif kind == "book":
        d.line([(cx - s * 0.8, cy - s * 0.8), (cx - s * 0.8, cy + s * 0.8)], fill=col, width=w)
        d.line([(cx + s * 0.8, cy - s * 0.8), (cx + s * 0.8, cy + s * 0.8)], fill=col, width=w)
        d.line([(cx - s * 0.8, cy - s * 0.8), (cx + s * 0.8, cy - s * 0.8)], fill=col, width=w)
        d.line([(cx - s * 0.8, cy + s * 0.8), (cx + s * 0.8, cy + s * 0.8)], fill=col, width=w)
        d.line([(cx, cy - s * 0.8), (cx, cy + s * 0.8)], fill=col, width=w)
    elif kind == "clock":
        r = s * 0.82
        d.ellipse([cx - r, cy - r, cx + r, cy + r], outline=col, width=w)
        d.line([(cx, cy - r * 0.55), (cx, cy)], fill=col, width=w)
        d.line([(cx, cy), (cx + r * 0.45, cy + r * 0.3)], fill=col, width=w)
    elif kind == "library":
        d.rounded_rectangle([cx - s, cy - s * 0.8, cx - s * 0.35, cy + s * 0.8], radius=4, fill=col)
        d.rounded_rectangle([cx - s * 0.2, cy - s * 0.8, cx + s * 0.45, cy + s * 0.8], radius=4, fill=col)
        d.rounded_rectangle([cx + s * 0.6, cy - s * 0.6, cx + s, cy + s * 0.8], radius=4, fill=col)
    elif kind == "folder":
        d.rounded_rectangle([cx - s, cy - s * 0.55, cx + s, cy + s * 0.75], radius=8, fill=col)
        d.rounded_rectangle([cx - s, cy - s * 0.9, cx - s * 0.1, cy - s * 0.4], radius=6, fill=col)
    elif kind == "heart":
        d.ellipse([cx - s, cy - s * 0.7, cx, cy + s * 0.3], fill=col)
        d.ellipse([cx, cy - s * 0.7, cx + s, cy + s * 0.3], fill=col)
        d.polygon([(cx - s, cy), (cx + s, cy), (cx, cy + s)], fill=col)
    elif kind == "star":
        d.polygon([(cx, cy - s), (cx + s * 0.3, cy - s * 0.3), (cx + s, cy - s * 0.25),
                   (cx + s * 0.45, cy + s * 0.25), (cx + s * 0.6, cy + s), (cx, cy + s * 0.55),
                   (cx - s * 0.6, cy + s), (cx - s * 0.45, cy + s * 0.25), (cx - s, cy - s * 0.25),
                   (cx - s * 0.3, cy - s * 0.3)], fill=col)

def card(sch, W=1000, H=1320):
    im = Image.new("RGB", (W, H), (245, 245, 242))
    d = ImageDraw.Draw(im)
    # title block
    d.text((40, 28), f"{sch['key']}  {sch['name']}", font=f(44), fill=(25, 25, 25))
    d.text((40, 88), sch["ref"], font=f(24), fill=(90, 90, 90))
    y = 140
    # ---- mock: status strip + top bar with icons
    d.rectangle([40, y, W - 40, y + 40], fill=sch["bar"])
    d.text((60, y + 8), "9:41", font=f(20), fill=sch["icon"])
    d.text((W - 240, y + 8), "▓▓▓  88", font=f(20), fill=sch["icon"])
    bar_top = y + 40
    bar_h = 96
    d.rectangle([40, bar_top, W - 40, bar_top + bar_h], fill=sch["bar"])
    draw_icon(d, "menu", 92, bar_top + bar_h // 2, 22, sch["icon"])
    d.text((140, bar_top + 26), "书库", font=f(30), fill=sch["icon"])
    for i, kind in enumerate(["refresh", "sort", "moon", "grid"]):
        draw_icon(d, kind, W - 300 + i * 78, bar_top + bar_h // 2, 20, sch["icon"])
    # search field mock
    d.rounded_rectangle([40, bar_top + bar_h + 14, W - 40, bar_top + bar_h + 78], radius=32, fill=sch["surface"])
    d.text((70, bar_top + bar_h + 30), "搜索书名、作者", font=f(24), fill=sch["sub"])
    # ---- content card
    cy = bar_top + bar_h + 110
    d.rounded_rectangle([40, cy, W - 40, cy + 230], radius=18, fill=sch["surface"], outline=sch["divider"], width=2)
    d.rectangle([64, cy + 24, 64 + 96, cy + 200], fill=sch["divider"])
    d.text((190, cy + 30), "继续阅读", font=f(24), fill=sch["sub"])
    d.text((190, cy + 70), "Doctor veterinarian Apa awaiting", font=f(30), fill=sch["text"])
    d.text((190, cy + 112), "PDF · 453kB · 20%", font=f(22), fill=sch["sub"])
    d.rounded_rectangle([190, cy + 160, 640, cy + 172], radius=6, fill=sch["divider"])
    d.rounded_rectangle([190, cy + 160, 350, cy + 172], radius=6, fill=sch["accent"])
    # accent chip
    d.rounded_rectangle([W - 300, cy + 40, W - 70, cy + 96], radius=28, fill=sch["accent"])
    d.text((W - 275, cy + 54), "置顶", font=f(26), fill=sch["accent_text"])
    # ---- tab bar
    ty = cy + 300
    d.rectangle([40, ty, W - 40, ty + 150], fill=sch["tab"])
    kinds = ["clock", "library", "folder", "heart", "star"]
    for i, (kind, label) in enumerate(zip(kinds, TABS)):
        cx = 40 + (W - 80) * (i + 0.5) / 5
        draw_icon(d, kind, cx, ty + 52, 22, sch["tablabel"])
        tw = d.textlength(label, font=f(26))
        d.text((cx - tw / 2, ty + 92), label, font=f(26), fill=sch["tablabel"])
    # ---- metrics
    my = ty + 180
    rows = [
        ("图标 / 顶栏", sch["icon"], sch["bar"], ratio(sch["icon"], sch["bar"]), 4.5),
        ("标签 / 底部栏", sch["tablabel"], sch["tab"], ratio(sch["tablabel"], sch["tab"]), 4.5),
        ("正文 / 卡片", sch["text"], sch["surface"], ratio(sch["text"], sch["surface"]), 4.5),
        ("强调色 / 卡片", sch["accent"], sch["surface"], ratio(sch["accent"], sch["surface"]), 3.0),
        ("强调上的文字", sch["accent_text"], sch["accent"], ratio(sch["accent_text"], sch["accent"]), 4.5),
    ]
    d.text((40, my), "对比度实测(WCAG)", font=f(30), fill=(25, 25, 25))
    for i, (label, fg, bg, r, need) in enumerate(rows):
        yy = my + 46 + i * 42
        d.rounded_rectangle([40, yy + 6, 76, yy + 34], radius=6, fill=bg, outline=(200, 200, 200))
        d.rounded_rectangle([46, yy + 12, 70, yy + 28], radius=4, fill=fg)
        d.text((92, yy), f"{label}: {hexs(fg)} on {hexs(bg)}", font=f(24), fill=(60, 60, 60))
        ok = "达标" if r >= need else "不足"
        col = (30, 140, 80) if r >= need else (190, 40, 40)
        d.text((760, yy), f"{r:.1f}:1  {ok}", font=f(24), fill=col)
    # swatches
    sy = my + 46 + len(rows) * 42 + 20
    d.text((40, sy), "色板", font=f(28), fill=(25, 25, 25))
    sw = [("顶栏", sch["bar"]), ("图标", sch["icon"]), ("强调", sch["accent"]),
          ("卡片", sch["surface"]), ("正文", sch["text"]), ("次要", sch["sub"])]
    for i, (nm, col) in enumerate(sw):
        x = 40 + i * 150
        d.rounded_rectangle([x, sy + 44, x + 128, sy + 128], radius=10, fill=col, outline=(190, 190, 190))
        d.text((x, sy + 136), nm, font=f(20), fill=(70, 70, 70))
        d.text((x, sy + 160), hexs(col), font=f(18), fill=(110, 110, 110))
    im.save(f"{OUT}/{sch['key']}-{sch['name']}.png")
    return im

cards = [card(s) for s in SCHEMES]

# combined sheet
CW, CH = 520, 1320
sheet = Image.new("RGB", (CW * 5 + 60, CH + 120), (250, 250, 248))
sd = ImageDraw.Draw(sheet)
sd.text((30, 24), "OmniShelf 界面配色 5 方案对比(图标/标签对比度均已达标)", font=f(34), fill=(25, 25, 25))
for i, (c, s) in enumerate(zip(cards, SCHEMES)):
    thumb = c.resize((CW - 20, int(CH * (CW - 20) / 1000)))
    sheet.paste(thumb, (30 + i * CW, 90))
sheet.save(f"{OUT}/00-all-schemes.png")
print("written:", len(cards) + 1, "files")
