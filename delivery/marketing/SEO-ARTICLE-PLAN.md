# SEO 文章排期(每周 2-3 篇,6 个月长尾发酵)

> Max 框架第 1 步的核心:**每周 2-3 篇、瞄准长尾词、给 3-6 个月发酵期**。
> 不抄竞品、不批量生成 AI 垃圾页——Google 会惩罚。

## 一、已完成(本轮 + 之前)

| 文章 | 目标关键词 | 状态 |
|---|---|---|
| Choosing an Android ebook reader: four apps, one honest table | `best android ebook reader`, `librera alternative`, `moon reader alternative` | ✅ 已上线 |
| CBZ and CBR on Android | `cbz reader android`, `cbr reader`, `comic reader android` | ✅ |
| DJVU on Android | `djvu reader android`, `djvu to pdf` | ✅ |
| 之前 16 篇工程笔记 + 2 篇阅读专栏 | 品牌词、AGPL、开源阅读器 | ✅ |

## 二、未来 12 篇(每篇对应一个真实搜索意图)

| # | 标题 | 目标长尾词 | 为什么能排 |
|---|---|---|---|
| 1 | How to read Kindle books on Android without the Kindle app | `read kindle books on android` | 高搜索量、教程型 |
| 2 | EPUB vs MOBI vs AZW3: which one should you keep | `epub vs mobi vs azw3` | 对比型、长期有效 |
| 3 | How to set up an OPDS library on your own server | `opds server`, `calibre opds` | 自建书库人群 |
| 4 | Why your PDF reader is slow (and what to do about it) | `pdf reader slow android` | 痛点型 |
| 5 | Reading scanned books on a phone: OCR, DJVU, and PDF | `scanned book reader android` | 长尾精准 |
| 6 | 10 free public-domain libraries that are not Project Gutenberg | `free ebook libraries` | 清单型、易传播 |
| 7 | Text-to-speech for books: what actually works offline | `offline tts reader` | 差异化卖点 |
| 8 | The anatomy of a comic archive (CBZ, CBR, and page order) | `cbz page order` | 技术深度 |
| 9 | Android reader privacy: what the permissions actually mean | `android reader permissions` | 隐私人群 |
| 10 | How to move your library between phones without an account | `move ebooks to new phone` | 实用教程 |
| 11 | 阅读器对比:微信读书、掌阅、番茄、OmniShelf(中文) | `阅读器 对比`、`无广告 阅读器` | 中文流量池 |
| 12 | 在手机上读扫描版 PDF 的正确姿势(中文) | `手机 读 扫描 pdf` | 中文长尾 |

## 三、每篇的固定结构(可复用的模板)

1. **一句话结论**(前 3 行给出答案,搜索引擎喜欢,用户也喜欢)
2. 为什么这个问题存在(2-3 段,讲机制)
3. 具体做法(带步骤或表格)
4. 边界与例外(不要过度承诺)
5. 结尾一句 CTA:免费开源版本在这里(链接带 `?ref=blog`)

## 四、技术与内容的检查清单

- [x] `SoftwareApplication` 结构化数据(首页已加,含 featureList / offers price 0)
- [x] sitemap.xml 自动包含新页面(38 页)
- [x] RSS 输出
- [ ] 每篇文章内链到 2 篇相关文章 + 1 次商店链接
- [ ] 每篇文章配 1 张真实截图(用 `delivery/store-listing/raw/`)
- [ ] 文章发布后,把同一内容改写成社区帖(一稿多投,不同措辞)
- [ ] 每月检查 Search Console:哪些长尾词有曝光但点击低 → 改标题

## 五、节奏与止损

- 频率:每周 2-3 篇,连续 6 个月(约 60-70 篇)
- 观察指标:Search Console 曝光量、点击率、`?ref=blog` 的访问
- **止损线**:3 个月后若某类文章(如工程笔记)完全没有曝光,停掉该类,把精力放到有曝光的方向
- **不要**为了数量生成 AI 垃圾页:宁可每周 2 篇有用的,也不要每天 10 篇模板文
