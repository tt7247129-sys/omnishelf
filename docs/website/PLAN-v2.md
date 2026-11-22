# omnishelf.cc 执行方案 v2(基于现状盘点)

> 主导:我。你配合账号/浏览器/审稿。
> 定位不变:**一个能立刻下到书、也教你怎么读的官方站**。下载是唯一主 CTA,书库是流量入口,阅读知识是信任来源。

---

## 1. 现状盘点(我读了 `web/` 与线上 `omnishelf.cc`)

| 项 | 现状 | 判断 |
|---|---|---|
| 技术栈 | Astro 5,静态输出,GitHub Pages 部署(`.github/workflows/deploy.yml`) | ✅ 保留,与我的推荐一致 |
| 路由 | 18 个:`/`、`/notes/`、`/reading-craft/`、`/where-to-read/`、`/reading-life/`、`/shelf/`、`/why/`、`/about/`、`/contact/`、`/privacy/`、`/terms/`、`/share/`、`/authors/`、RSS、sitemap | ⚠️ 内容多,但**没有下载路径** |
| 内容 | 28 篇:工程笔记 16、找书 8、阅读方法/生活各 2 | ✅ 是信任资产,保留 |
| 下载入口 | **无 Play 链接、无二维码、无 `/download`、无 `/go/play`** | ❌ P0 缺口(第一作用完全没做) |
| 书库 | **无书籍数据模型、无书页** | ❌ P0 缺口(第二作用没做) |
| 法务 | 有 `/privacy`、`/terms`,无 `/dmca` | ⚠️ 做书库前必须补 |
| 归因 | App 端**没有** Play Install Referrer 实现 | ❌ 无法回答"网站带来多少安装" |
| 搜索/结构化数据 | 无站内搜索;无 JSON-LD | ⚠️ 影响 SEO 与承接 |

**结论:不做重构,做"增量改造"**——保留现有内容与设计气质,补上转化层与书库层。

---

## 2. 研究:10 个承接/转化标杆

| # | 站点 | 月访问(公开估算) | 承接打法 | 可取之处 |
|---|---|---|---|---|
| 1 | [Project Gutenberg](https://www.gutenberg.org) | ~10.5M | 6 万+ 书,每书一页+直链 | **程序化书页=SEO 发动机** |
| 2 | [Open Library](https://openlibrary.org) | ~8.3M | 借阅 + 书架 | 想读/在读/读完状态 |
| 3 | [Goodreads](https://www.goodreads.com) | ~110M | 书评 + 榜单 | 榜单页是稳定入口 |
| 4 | [Wattpad](https://www.wattpad.com) | ~90M | 网页先读一章 → 催装 App | 内容前置漏斗 |
| 5 | [BookBub](https://www.bookbub.com) | 大 | 邮件推送免费/低价书 | 回访与分发 |
| 6 | [Standard Ebooks](https://standardebooks.org) | ~350K | 只做 900+ 本,排版极精 | 书页即作品页 |
| 7 | [ManyBooks](https://manybooks.net) | ~0.8–1.3M | 免费/付费分层 + 细分筛选 | **最像我们的书库模板** |
| 8 | [Readwise](https://readwise.io) | ~680K | 博客教"怎么读"→ 转化订阅 | **内容养高意向用户** |
| 9 | [F-Droid](https://f-droid.org) | 中量 | 开源分发,极简可信 | 用"开源/无广告"做信任状 |
| 10 | [Blinkist](https://www.blinkist.com) | 大 | 首屏一句话价值主张 | 100 像素内说清价值 |

来源:[Similarweb 对比](https://www.similarweb.com/website/standardebooks.org/vs/manybooks.net/)、[Semrush gutenberg.org](https://www-semrush.waimaoclub.com/website/gutenberg.org/overview/)、[MrrTrack readwise.io](https://mrrtrack.com/saas/readwise.io)、[Exploding Topics 图书馆类](https://analytics.explodingtopics.com/website/global/libraries)、[Play Install Referrer 官方文档](https://developer.android.com/google/play/installreferrer)。

## 3. 推荐借鉴的 3 个(抄什么/不抄什么)

**① ManyBooks —— 抄「免费/付费分层 + 细筛选」**:`/books/free/` 与 `/books/paid/` 两条路径,付费只放封面/简介/购买链接,不托管文件。不抄它密集的广告位。

**② Standard Ebooks —— 抄「书页质量」**:每本书页 = 封面 + 一句简介 + 下载按钮 + 版权状态 + 相关 3 本。不抄它"不做产品"的纯公益姿态,我们书页底部放软转化(用 OmniShelf 读它)。

**③ Readwise —— 抄「知识内容 → 产品转化」**:把「怎么读/怎么记/怎么选」做成体系,文章内嵌产品能力演示,结尾一个 CTA。不抄它的长英文 SaaS 文案。

备选:**Project Gutenberg 的程序化书页 SEO**——是我们书库的流量底盘。

---

## 4. 信息架构(在现有基础上增量)

```
/                    改造:首屏改成承接(主 CTA) + 三个入口(下载/书库/笔记)
/download            新增:Play 直链 + 二维码 + 三步上手 + 权限说明
/go/play             新增:静态跳转页(带 utm 参数 → Play Install Referrer)
/books/              新增:书库首页(搜索 + 免费/付费 + 分类 + 榜单)
/books/free/         新增:公版与免费原创
/books/paid/         新增:正版付费导流
/books/[slug]        新增:单本书页(下载 EPUB / 用 App 打开)
/books/collections/  新增:书单(P1)
/reading-craft/      保留:阅读方法(补足到 12 篇)
/where-to-read/      保留:去哪找书(8 篇,天然导流书库)
/reading-life/       保留
/notes/              保留:工程笔记(信任资产)
/shelf/ /why/ /about/ /contact/ /authors/ /share/  保留
/dmca                新增:版权下架通道
```

## 5. 具体改动清单(按文件)

### 新增组件
- `src/components/DownloadCta.astro` —— 主按钮(Play 链接 + `/go/play?src=` 埋点参数 + 悬浮条逻辑)
- `src/components/QrCode.astro` —— 桌面端二维码(扫码直达 Play)
- `src/components/BookCard.astro` / `BookGrid.astro` —— 封面/标题/作者/语言/格式/免费或付费徽标
- `src/components/JsonLd.astro` —— SoftwareApplication / Book / Article / FAQPage

### 新增数据与页面
- `src/content.config.ts` 增加 `books` 集合:
  `slug, title, author, translator?, language, genre[], status(free-public-domain | free-original | paid-affiliate), pages?, cover, summary(≤120字), formats[], download(format→url), buyUrl?, related[], source?`
- `src/pages/download.astro`、`src/pages/go/play.astro`(meta refresh + JS 跳转,参数透传)
- `src/pages/books/index.astro`、`free.astro`、`paid.astro`、`[...slug].astro`
- `src/pages/dmca.astro`、`src/pages/404.astro`
- 首页改造:`web/src/pages/index.astro` 顶部加承接区块,下方保留最新笔记

### 站点级
- `astro.config.mjs`:加 `sitemap`(已有 `sitemap.xml.ts`,补书页)、可选 Pagefind 构建步骤
- `deploy.yml`:构建后加 Pagefind 索引
- JSON-LD:首页 `SoftwareApplication`(含 Play URL),书页 `Book`+`offers`,文章 `Article`
- 分析:Umami 或 Cloudflare Web Analytics(无 Cookie),事件:`download_click`、`book_open`、`article_cta`
- 法务:`/privacy` 补一行"网站不设 Cookie 追踪"、`/terms`、新增 `/dmca`

### App 端(配合,1 个改动)
- 集成 **Play Install Referrer**:首次启动读取 `utm_source/medium/campaign`,本地记录并(可选)上报到隐私友好端点;不采集广告 ID。
- 这是"北极星指标"唯一可信来源。

## 6. 书库方案(第二作用)

- **版权红线**:公版(中国:作者卒年 ≤1975;英文:以 Gutenberg/Standard Ebooks 标注为准)+ 自有原创 + 授权免费;**付费只做联盟导流**;禁止任何盗版源。
- 首批 300 本(第 2 周 100 本):中文公版 60(四大名著/儒林外史/聊斋/唐诗宋词/四书五经/鲁迅/朱自清/老舍)、英文公版 200、原创 20、付费导流 20。
- 每本书页固定显示:封面、120 字简介、版权状态、格式与下载按钮、「用 OmniShelf 打开」深链、相关 3 本。
- 筛选维度:语言 / 题材 / 格式 / 免费或付费 / 长度。
- 导入流水线:Astro content collection + 脚本校验(必填/版权/坏链)+ 构建期生成 sitemap 与 JSON-LD。

## 7. 阅读知识(第三作用,已有底子)

现有 reading-craft(2)/where-to-read(8)/reading-life(2) → 补到 **12 篇支柱 + 36 篇支撑**:
如何阅读一本书实操 / 精读 vs 泛读 / SQ3R / 主题阅读 / 笔记三法 / 读完就忘 / 阅读器怎么选 / 手机读长篇设置 / 书摘分享 / 听书 / 选书 / 年度阅读系统。
每篇:问题 → 方法 → 示例 → 常见错误 → 「用 OmniShelf 怎么做」CTA → 相关书 3 本。

## 8. 四周里程碑(每周可验收)

| 周 | 交付 | 验收 |
|---|---|---|
| W1 | 首页承接改造 + `/download` + `/go/play` + 埋点 + JSON-LD(SoftwareApplication)+ `/dmca` | 手机/桌面可打开;下载点击有事件;Lighthouse ≥95 |
| W2 | 书库首页 + 书页模板 + **100 本** + 免费/付费分流 | 100 书页可访问可下载;JSON-LD 校验通过 |
| W3 | 书单页 + 8 篇支柱文章 + 内链 | 每篇内链 ≥3;文章→下载可测 |
| W4 | App 端 Install Referrer + Pagefind 搜索 + 剩余文章 + 上线清单 | 站内来源安装可见;GSC 收录 >50%;检查表全绿 |

## 9. 指标

- 北极星:站内来源的 **Play 安装数**(Install Referrer)。
- 一级:下载点击率、书下载数、文章→下载转化率。
- 二级:自然流量、收录页数、Top 落地页、跳出率。
- 反指标:下载 404、LCP、单页 JS 体积。

## 10. 风险与红线

| 风险 | 应对 |
|---|---|
| 版权(最高) | 只做公版/原创;付费只导流;`/dmca` 24h 响应;来源标注 |
| Google 对"下载站"信任评级 | 无弹窗/无诱导/无捆绑;HTTPS;清晰所有者;Play 官方链接 |
| .cc 域名信任 | About/开源/隐私政策/Play 官方页互证 |
| 与 App 品牌不一致 | 设计令牌与文案直接取 App(朱砂/纸/墨/宋体) |

## 11. 需要你配合(按顺序)

1. 确认 **Play 商店正式链接**是否已有(包名 `cc.omnishelf.reader`);没有的话先上架,我这边用 `market://details?id=cc.omnishelf.reader` 兜底。
2. **GitHub Pages 部署权限**:确认 `deploy.yml` 用的是当前仓库 Actions(我需要能触发部署),或给我 Cloudflare 账号我另挂。
3. **GSC / Bing 验证**:我把验证文件放进 `web/public/`,你在浏览器点确认。
4. **语言顺序**:我建议先中文、英文同批骨架(可否决)。
5. **版权口径确认**:公版 + 原创 + 正版导流(不托管付费文件)。
6. **是否接受联盟链接**(Amazon/Kobo 等);不接受就改成"去商店搜索"按钮。
7. DMCA 邮箱确认:`hello@omnishelf.cc` 是否可用。

## 12. 我 24 小时内会做(不用等你)

1. 首页承接区块 + `/download` + `/go/play`(含参数透传)+ 二维码组件。
2. `books` 集合 schema + 书页模板 + 20 本书元数据验证流水线。
3. `/dmca` 与隐私政策补句。
4. 一篇支柱文章初稿(如何阅读一本书·实操版)。
5. 本地预览,交你审。
