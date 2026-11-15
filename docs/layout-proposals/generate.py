from PIL import Image, ImageDraw, ImageFont
import os
OUT = "/Users/huangpeng/Documents/deepseek-harness/default-workspace/omnishelf/docs/layout-proposals"
os.makedirs(OUT, exist_ok=True)
F = "/System/Library/Fonts/Hiragino Sans GB.ttc"
def f(s, i=0): return ImageFont.truetype(F, s, index=i)

RED = (0x8E, 0x2B, 0x21)
RED_L = (0xA8, 0x3A, 0x2E)
CREAM = (0xFF, 0xFD, 0xF8)
WHITE = (255, 255, 255)
INK = (0x2A, 0x1B, 0x18)
GREY = (0x7A, 0x6A, 0x63)
LINE = (0xEA, 0xDF, 0xD8)

def icon(d, kind, cx, cy, s, col, w=6):
    if kind == "menu":
        for i in (-1,0,1): d.line([(cx-s, cy+i*s*0.55), (cx+s, cy+i*s*0.55)], fill=col, width=w)
    elif kind == "search":
        r=s*0.72; d.ellipse([cx-r,cy-r,cx+r,cy+r], outline=col, width=w)
        d.line([(cx+r*0.7,cy+r*0.7),(cx+s,cy+s)], fill=col, width=w)
    elif kind == "grid":
        r=s*0.42
        for dx in (-1,1):
            for dy in (-1,1):
                d.rounded_rectangle([cx+dx*s*0.52-r, cy+dy*s*0.52-r, cx+dx*s*0.52+r, cy+dy*s*0.52+r], radius=3, fill=col)
    elif kind == "sort":
        d.line([(cx-s*0.8,cy-s*0.7),(cx-s*0.8,cy+s*0.8)], fill=col, width=w)
        d.polygon([(cx-s*0.8,cy+s*0.85),(cx-s*0.4,cy+s*0.25),(cx-s*1.2,cy+s*0.25)], fill=col)
        for k,xx in enumerate((cx-s*0.1, cx+s*0.4, cx+s*0.9)):
            d.line([(xx, cy-s*0.7),(xx, cy+s*0.4-k*s*0.2)], fill=col, width=w)
    elif kind == "refresh":
        r=s*0.8; d.arc([cx-r,cy-r,cx+r,cy+r], 30, 300, fill=col, width=w)
        d.polygon([(cx+r*0.7,cy-r*0.75),(cx+r*1.2,cy-r*0.2),(cx+r*0.35,cy-r*0.1)], fill=col)
    elif kind == "gear":
        r=s*0.55; d.ellipse([cx-r,cy-r,cx+r,cy+r], outline=col, width=w)
        for a in range(0,360,45):
            import math
            x1=cx+math.cos(math.radians(a))*r*1.1; y1=cy+math.sin(math.radians(a))*r*1.1
            x2=cx+math.cos(math.radians(a))*r*1.6; y2=cy+math.sin(math.radians(a))*r*1.6
            d.line([(x1,y1),(x2,y2)], fill=col, width=w-1)
    elif kind == "filter":
        d.polygon([(cx-s,cy-s*0.7),(cx+s,cy-s*0.7),(cx+s*0.25,cy+s*0.6),(cx+s*0.25,cy+s),(cx-s*0.25,cy+s),(cx-s*0.25,cy+s*0.6)], fill=col)

def phone(d, x, y, w, h, radius=26):
    d.rounded_rectangle([x,y,x+w,y+h], radius=radius, fill=(250,250,248), outline=(220,220,215), width=2)

def card(no, title, subtitle, note, draw_fn, W=980, H=780):
    im = Image.new("RGB", (W,H), (247,246,243)); d = ImageDraw.Draw(im)
    d.text((36,24), f"{no}  {title}", font=f(38), fill=(25,25,25))
    d.text((36,78), subtitle, font=f(21), fill=(95,95,95))
    d.text((36,110), "结构:" + note, font=f(21), fill=(60,110,80))
    draw_fn(d, 36, 156, W-72, H-210)
    return im

def bar(d, x, y, w, h, col=RED):
    d.rectangle([x,y,x+w,y+h], fill=col)

def base_content(d, x, y, w, h):
    d.rectangle([x,y,x+w,y+h], fill=CREAM)
    for i,(cw,ch_,dx,dy) in enumerate([(150,210,20,20),(150,210,190,20),(150,210,360,20)]):
        d.rounded_rectangle([x+dx,y+dy,x+dx+cw,y+dy+ch_], radius=8, fill=(255,255,255), outline=LINE)
        d.rectangle([x+dx+10,y+dy+10,x+dx+cw-10,y+dy+ch_-70], fill=(0xE7,0xE0,0xD8))
        d.text((x+dx+12,y+dy+ch_-52), "Cn" if i==0 else ("Meditations" if i==1 else "Alice"), font=f(20), fill=INK)
    d.line([(x+18,y+250),(x+w-18,y+250)], fill=LINE, width=3)

TABS = ["最近","书库","文件夹","收藏","书签"]
def bottom_tabs(d, x, y, w, h):
    bar(d, x, y, w, h)
    for i,t in enumerate(TABS):
        cx = x + w*(i+0.5)/5
        icon(d, "grid" if i==1 else ("search" if i==0 else "gear"), cx, y+h*0.38, 16, WHITE)
        tw = d.textlength(t, font=f(20))
        d.text((cx-tw/2, y+h*0.62), t, font=f(20), fill=WHITE)

# 1. single row bar
def s1(d, x, y, w, h):
    phone(d,x,y,w,h)
    bar(d,x,y+8,x+w,96)
    icon(d,"menu",x+46,y+56,17,WHITE)
    d.text((x+92,y+42),"书库  4",font=f(26),fill=WHITE)
    r=[x+210,y+38,x+w-260,y+76]; d.rounded_rectangle(r,radius=19,fill=(208,90,76))
    d.text((x+232,y+44),"搜索书名、作者",font=f(20),fill=(255,235,230))
    for i,k in enumerate(["refresh","sort","grid"]):
        icon(d,k,x+w-210+i*72,y+56,15,WHITE)
    base_content(d,x,y+104,w,h-200)
    bottom_tabs(d,x,y+h-92,w,92)


# 2. no hamburger (wechat style)
def s2(d, x, y, w, h):
    phone(d,x,y,w,h)
    bar(d,x,y+8,x+w,96)
    d.text((x+28,y+40),"书库  4",font=f(28),fill=WHITE)
    r=[x+150,y+38,x+w-170,y+76]; d.rounded_rectangle(r,radius=19,fill=(208,90,76))
    d.text((x+172,y+44),"搜索书名、作者",font=f(20),fill=(255,235,230))
    icon(d,"gear",x+w-110,y+56,16,WHITE)
    icon(d,"grid",x+w-46,y+56,15,WHITE)
    base_content(d,x,y+104,w,h-200)
    bottom_tabs(d,x,y+h-92,w,92)


# 3. tabs in the top bar
def s3(d, x, y, w, h):
    phone(d,x,y,w,h)
    bar(d,x,y+8,x+w,96)
    icon(d,"menu",x+46,y+56,17,WHITE)
    for i,t in enumerate(["最近","书库","文件夹","收藏"]):
        tx = x+110+i*118
        if i==1:
            d.rounded_rectangle([tx-30,y+30,tx+66,y+82],radius=16,fill=(208,90,76))
        d.text((tx-10,y+44),t,font=f(22),fill=WHITE)
    icon(d,"search",x+w-90,y+56,16,WHITE)
    icon(d,"grid",x+w-36,y+56,15,WHITE)
    base_content(d,x,y+104,w,h-160)


# 4. two rows, distinct weights
def s4(d, x, y, w, h):
    phone(d,x,y,w,h)
    bar(d,x,y+8,x+w,72,(0x6E,0x1F,0x17))
    icon(d,"menu",x+42,y+44,15,WHITE)
    d.text((x+80,y+30),"书库",font=f(24),fill=WHITE)
    icon(d,"gear",x+w-44,y+44,14,WHITE)
    bar(d,x,y+80,x+w,72,RED_L)
    r=[x+18,y+96,x+w-250,y+136]; d.rounded_rectangle(r,radius=20,fill=(255,255,255))
    d.text((x+38,y+103),"搜索书名、作者",font=f(20),fill=GREY)
    for i,k in enumerate(["filter","sort","grid"]):
        icon(d,k,x+w-190+i*68,y+116,14,WHITE)
    base_content(d,x,y+152,w,h-248)
    bottom_tabs(d,x,y+h-92,w,92)


# 5. floating pill
def s5(d, x, y, w, h):
    phone(d,x,y,w,h)
    base_content(d,x,y+8,w,h-100)
    d.rounded_rectangle([x+14,y+22,x+w-14,y+104],radius=40,fill=RED)
    icon(d,"menu",x+50,y+62,16,WHITE)
    d.text((x+96,y+48),"书库  4",font=f(24),fill=WHITE)
    icon(d,"search",x+w-120,y+62,15,WHITE)
    icon(d,"grid",x+w-52,y+62,15,WHITE)
    bottom_tabs(d,x,y+h-92,w,92)


imgs = [
 card("方案 1","单行顶栏(推荐)","把两条顶栏合并成一行,与掌阅/QQ阅读一致。","[菜单] 标题+计数 | 搜索框 | 刷新 · 排序 · 视图 · 设置", s1),
 card("方案 2","去掉抽屉(微信读书式)","不设汉堡,设置收进齿轮。","标题+计数 | 搜索框 | 设置 · 视图", s2),
 card("方案 3","标签并入顶栏","底部不再有标签栏,每屏多一行内容。","[菜单] 最近 书库 文件夹 收藏 | 搜索 · 视图", s3),
 card("方案 4","两行分工","两行职责不同,用深浅区分。","深色行:菜单 标题 设置 / 浅色行:搜索 筛选 排序 视图", s4),
 card("方案 5","悬浮胶囊","没有整条顶栏,内容全屏。","胶囊:菜单 标题+计数 搜索 视图", s5),
]
for im, name in zip(imgs, ["1-single-row","2-no-drawer","3-tabs-in-bar","4-two-rows","5-floating-pill"]):
    im.save(f"{OUT}/{name}.png")

# combined
CW, CH = 470, 375
sheet = Image.new("RGB",(CW*5+40, CH+150),(250,250,248))
sd = ImageDraw.Draw(sheet)
sd.text((24,20),"OmniShelf 导航结构:5 个方案(去掉重复的汉堡与双顶栏)",font=f(30),fill=(25,25,25))
for i,im in enumerate(imgs):
    th = im.resize((CW-16, int(im.size[1]*(CW-16)/im.size[0])))
    sheet.paste(th,(16+i*CW, 70))
sheet.save(f"{OUT}/00-all.png")
print("written")
