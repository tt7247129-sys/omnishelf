# OmniShelf 社区发帖文案(首发日用)

> 规则先说:每个社区只发一次、不刷评、不诱导好评、不群发同一链接。
> 发帖人必须是真人账号,回复要真回。以下是可直接复制的草稿。

---

## 一、Hacker News(Show HN)

**标题**
```
Show HN: OmniShelf – a no-ads, no-tracking EPUB/PDF reader for Android
```

**正文**
```
I forked Librera Reader (AGPLv3) and rebuilt its interface. The pitch is boring but
true: it opens 34 file types, reads them offline, speaks text with the device's own
TTS voice, and contains no advertising, analytics or crash-reporting code at all.

What's different from the upstream app:
- One top bar per screen instead of a drawer plus a header (that took three tries)
- A single accent colour, measured for contrast (headers were 1.3:1, now 8.3:1)
- 54 interface languages, following the system language
- Continue-reading card, pin-to-top, day-grouped recents

No account, no sign-in, no servers. Reading progress and highlights stay on the device.

It's on Google Play (closed testing) and the APK is on GitHub:
https://github.com/tt7247129-sys/omnishelf

I'd genuinely like feedback on two things: whether the reading panel still feels
crowded, and which format support is missing for your use case.
```

---

## 二、Reddit

### r/androidapps(Show)

```
[Dev] I rebuilt an open-source EPUB/PDF reader — 34 formats, offline TTS, zero ads or trackers

APK: https://github.com/tt7247129-sys/omnishelf/releases
Play (closed testing): https://play.google.com/store/apps/details?id=cc.omnishelf.reader

34 formats (EPUB, PDF, MOBI, AZW3, FB2, DJVU, CBZ/CBR, DOCX, ODT, RTF, TXT...),
offline text-to-speech with the system voice, highlights/notes/bookmarks, OPDS
catalogues, 54 interface languages. No account, no analytics, no ad SDK.

Happy to answer anything about the fork or the UI work.
```

### r/ebooks

```
Free, open-source reader with no ads: 34 formats including DJVU and CBZ

I maintain OmniShelf, an AGPLv3 fork of Librera. It reads EPUB/PDF/MOBI/AZW3/FB2/
DJVU/CBZ/CBR/DOCX and runs entirely offline. No ads, no tracking, nothing locked.
Looking for testers who read scanned PDFs and comics — those are the formats
I most want feedback on.
```

### r/degoogle / r/fossdroid

```
OmniShelf: AGPLv3 ebook reader — no ads, no analytics, no ad ID, no account
Source: https://github.com/tt7247129-sys/omnishelf
APK: releases page (also aiming for IzzyOnDroid/F-Droid)
```

---

## 三、V2EX(分享创造)

**标题**
```
做了一个无广告的开源阅读器,支持 34 种格式,想听听反馈
```

**正文**
```
fork 了开源的 Librera Reader,把界面整个重做了一遍:

- 每屏只有一条顶栏(原来是抽屉 + 标题栏两层,重复又占地方)
- 一个强调色,顶栏图标对比度从 1.3:1 提到 8.3:1
- 54 种语言,跟随系统
- 继续阅读卡片、置顶、按天分组
- 无广告、无内购、无统计、无账号,数据只在本地

支持 EPUB / PDF / MOBI / AZW3 / FB2 / DJVU / CBZ / CBR / DOCX 等 34 种。

APK: https://github.com/tt7247129-sys/omnishelf/releases
源码: https://github.com/tt7247129-sys/omnishelf

想听两个方向的反馈:阅读面板是不是还太挤,以及你常用但没被支持的格式。
```

---

## 四、酷安

**标题**
```
无广告本地阅读器，34 种格式，求真实反馈
```

**正文**
```
自己业余时间做的开源阅读器，界面重做过一遍：
每屏一条顶栏、无广告无追踪、支持 EPUB/PDF/MOBI/CBZ/DJVU/DOCX 等 34 种格式，
离线朗读用的系统语音，54 种语言跟随系统。

APK 在 GitHub Releases，装完能给点真实意见就好——尤其是扫描版 PDF 和漫画的体验。
```

---

## 五、少数派(投稿/Matrix 帖子)

**标题**
```
把开源阅读器的界面重做一遍,我学到的三件事
```

**正文要点**(写成 1500 字左右的经验文,不是广告)
1. 双顶栏 + 汉堡是历史包袱:去掉之后每屏多出约 15% 内容区
2. 对比度是能量出来的:1.3:1 的图标等于没有,8.3:1 才算能看见
3. 长按 vs 点击:只挂长按监听会让点击被吞掉,这是最容易被忽略的 bug
文末附应用与源码链接。

---

## 六、知乎(回答类,选已有问题去回答)

适合回答的问题类型:
- 「有哪些无广告的电子书阅读器?」
- 「安卓上有什么好用的本地 EPUB 阅读器?」
- 「如何把 Kindle 电子书导入安卓手机阅读?」

回答结构:先给方法/对比表,再提自己也做了一个开源版本(附链接),最后提醒隐私点。

---

## 七、小红书 / 即刻(图文)

**首图文案**:被广告逼疯后,我换了这个阅读器
**正文**:
```
痛点:免费阅读器全是广告,翻两页弹一次
换成自己做的开源版:无广告、无追踪、34 种格式、离线朗读
界面重做过,顶栏只有一条,夜间模式不刺眼
安卓搜 OmniShelf，或者 GitHub 搜同名项目
```

---

## 八、首发日节奏(72 小时)

| 时间 | 动作 |
|---|---|
| D1 上午(美东) | Hacker News Show HN |
| D1 下午 | r/androidapps(Show) + r/ebooks |
| D1 晚上 | V2EX + 酷安 |
| D2 | r/degoogle、r/fossdroid、知乎回答 2 条 |
| D3 | 少数派投稿 + 小红书/即刻 |
| 之后 | 每周 1 条进展帖(功能更新、bug 修复),不复读广告 |

## 九、回复纪律

- 有人报 bug:先复现,48 小时内给结论,公开回复处理结果
- 有人问"和 Moon+/KOReader 比怎么样":如实说差异,不贬低竞品
- 有人要求开源证明:直接给仓库链接和许可证文件
