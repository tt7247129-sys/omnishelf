# AdSense 结构性修复:交付与核查

> 本轮不新增文章,只修结构。日期:2026-11-09。

---

## 1. EEAT 修复包

### 1.1 新建 `/authors/`

独立静态页,可从导航(About 页)、每篇文章署名与文末作者块进入。

内容要点:

- 身份:一人维护,以 **@tt7247129-sys** 名义发表,该账号即应用仓库所有者与 `hello@omnishelf.cc` 的收件人
- 性质:明确说明这是笔名,不公开法定姓名,并说明原因(不把住址发布在任何人都能下载的软件旁)
- 可核验线索:GitHub 仓库、提交历史、邮箱
- 背景:先做文档读者(论文、标准、扫描手册),后做阅读软件;笔记是"立场—测量—修正"的残留物
- 能力边界:不是图书馆员、不是出版从业者、不是律师,版权相关只作描述不作建议
- 联系方式

### 1.2 文章署名

- 每篇文章标题下方:`By @tt7247129-sys`(链接到 `/authors/`)
- 文末 `About the author` 块:一句**与本文主题绑定**的背景说明 + 作者页链接
- 该块内**不出现产品名**,也不出现任何下载引导

六篇新文的定制句:

| 文章 | 作者说明 |
|---|---|
| inspection-reading | 花了两周测"从冷启动到目标章节"的耗时,才改掉目录面板 |
| marginalia-budget | 对比一年纸面批注与屏幕高亮,才定下四种高亮颜色 |
| gutenberg-three-books | 连续十年从同一个目录下载同样三本小说,才看见这个模式 |
| standard-ebooks-vs-gutenberg | 同一章在两版上、三台设备各读一遍,这次比较由此开始 |
| bedside-light | 用廉价照度计测了三盏床头灯两周——不是科学,但足以看清取舍 |
| a-stand-solves-page-spring-back | 一个月内用三种压页工具读平装书,只留下一个 |

### 1.3 `/about/` 重写

结构:关于本站(定位)→ **Who runs it**(一人 + 可核验账号)→ **Why this site exists**(从"引用页码失效"这个具体 bug 讲起)→ **What we are qualified to say, and what we are not**(能力与边界并列)→ **How the notes are made**(每篇必须有具体对象,否则不发)→ **How to reach us** → 页脚链接 authors / contact / privacy。

产品关联只出现一次,且以"它只是这些笔记的测试工具,不在任何地方出售"的从句出现,不作为页面目的。

---

## 2. 隐私政策逐项对照表

| # | AdSense 要求 | 对应正文 | 位置 |
|---|---|---|---|
| 1 | Cookie 声明 | "**This website uses cookies.**" 并说明用途与浏览器控制方式 | §2 |
| 2 | 第三方广告服务(Google AdSense) | "This website may use third-party advertising services, including **Google AdSense**" | §3 |
| 3 | 第三方供应商使用 Cookie 基于先前访问投放 | "**Third-party vendors, including Google, use cookies to serve ads based on a user's prior visits to this website or to other websites.**" | §3 |
| 4 | 退出机制 | Google Ads Settings `google.com/settings/ads`、`aboutads.info/choices`、`youronlinechoices.com`、浏览器 Cookie 控制 | §5 |
| 5 | 其他第三方广告网络 | 新增 §4:"other third-party vendors and advertising networks may also serve ads… any such vendor may place or read cookies… this policy will name it here before it is deployed" | §4 |

同时修复的既有缺陷:

- frontmatter 中 `{updated}` 未渲染,页面上直接显示花括号 → 改为固定日期文本
- 退出机制列表缩进错误(8 空格 → 被渲染为代码块)→ 改为标准列表
- 章节由 9 节扩为 10 节,新增 §4 广告网络披露

---

## 3. 发布节奏

现状:6 篇编辑内容与 9 篇工程笔记均标注 2026-11-08,**未修改任何日期**。按提示词要求,不通过改日期伪造节奏。

写入 `README.md` 的日历(每周 2–4 篇新增):

| 周次 | 新增内容 |
|---|---|
| W1(已完成) | Reading Craft ×2、Where to Read ×2、Reading Life ×2 |
| W2 | Where to Read:`open-library-waiting-list`、`public-domain-is-not-any-use` |
| W3 | Where to Read:`second-hand-condition-grades`、`bookshop-org-checkout`、`ebook-or-paper-not-a-format-choice` |
| W4 | Reading Craft:`synthesis-across-three-books`、`sq3r-recite-step-skipped` |
| W5 | Reading Craft:`ninety-minutes-offline`、`reading-log-two-methods` |
| W6 | Reading Life:`case-fit-kindle-kobo`、`reading-corner-minimum`、`reading-glasses-diopters` |

`lastmod` 直接取 `pubDate` / `updatedDate`,不批量伪造。

"持续运营"的可见信号:首页文章流按日期倒序展示全部条目;`/shelf/` 保留变更时间线;页脚提供 RSS。

---

## 4. 导航与首页

### 4.1 一级导航(5 项)

`Reading Craft` / `Where to Read` / `Reading Life` / `About` / `Shelf`

- 前 3 项为内容入口
- `Shelf` 置末,纯文字
- `Contact` 在页脚与 About 页
- `Why OmniShelf` 已移出一级导航,保留在 `/why/`,由首页正文与 About 内链进入

### 4.2 首页

- H1(全站唯一):`Notes on reading, and the reader we built` — 说明站点是什么,不再是产品名
- 导言约 480 字:说明本出版物关注什么、三个栏目各回答什么问题
- 文章流:15 条,标题 + 正文提炼摘要 + 日期 + 阅读时长
- 内部链接 31 条,其中指向具体文章 15 条
- **无下载按钮、无应用截图、无功能图标网格**
- 产品仅在导言末段以"它不再占据导航首位,因为这是出版物"一句出现

### 4.3 H1 与可达性

27 页全部**恰好一个 H1**;从首页到任意文章 ≤2 次点击(首页卡片直达);移动端导航为换行折行而非隐藏菜单。

---

## 5. 已有文章定向修复

| 文章 | 一人称场景 | 信息增益 | 产品边界 | 动作 |
|---|---|---|---|---|
| inspection-reading | 有(6.7 吋/320 页 PDF/14→6 秒) | 高(EPUB nav 与 PDF outline 的规范差异) | 正文 0 次 | 仅补署名与作者块 |
| marginalia-budget | 有(380 页书两介质对比) | 高(页边距预算与四种颜色语义) | 正文 1 次,非 CTA | 仅补署名 |
| gutenberg-three-books | 有(手机四屏检索) | 高(格式差异、主题标目、溯源) | 正文 1 次,非 CTA | 仅补署名 |
| standard-ebooks-vs-gutenberg | 有(同章三设备) | 高(编辑工序、语义标记、语言元数据) | 正文 1 次,非 CTA | 仅补署名 |
| bedside-light | 有(两周三灯照度计) | 中高(照度 vs 瓦数、色温与对比) | 正文 0 次 | 仅补署名 |
| a-stand-solves-page-spring-back | 有(350–500 页平装一个月) | 高(装订结构 vs 支撑结构) | 正文 0 次 | 仅补署名 |
| 9 篇工程笔记 | 有(每篇含具体测量与实现细节) | 高 | 正文 0–1 次 | 补署名与作者块 |

诊断结论:**没有发现需要删除的泛论段落**。六篇新文的共同结构是"具体场景 → 以为 X 实际 Y → 外部参照 → 边界声明",不属于"阅读很重要/多读多练"一类。

孤儿页:0(栏目索引全量链接,首页流 15 条全量链接,相关阅读互为链接)。

---

## 6. 技术核查报告

| 项 | 状态 | 说明 / 动作 |
|---|---|---|
| 自定义域名 | **WARNING(偏离建议)** | 保持 apex `omnishelf.cc`,未改为 `www`。理由见下 |
| www → apex 跳转 | PASS | `301 → https://omnishelf.cc/` |
| 证书覆盖 | PASS | SAN 同时包含 `omnishelf.cc` 与 `www.omnishelf.cc` |
| HTTPS 强制 | PASS | `https_enforced: true` |
| SSL 模式 | PASS | 灰云 + GitHub 证书;未启用 Cloudflare 代理,故不存在 526 风险 |
| `CNAME` 文件 | PASS | 已加入 `web/public/CNAME`(`omnishelf.cc`) |
| `base href` | PASS | 站点在根路径,无 `/repo/` 前缀残留 |
| robots.txt | PASS | `Allow: /`,含 sitemap 指向 |
| sitemap.xml | PASS | 27 条 URL,`lastmod` 取自真实日期 |
| 内部 404 | PASS | 构建后链接自检 0 死链 |
| 唯一 title/description | PASS | 27 页无重复 |
| 唯一 H1 | PASS | 27 页均为 1 |
| AdSense 代码 | PASS | 全站扫描 `adsbygoogle` / `ca-pub-` / `pagead2` 均为 0 |
| under construction | PASS | 无占位页 |
| 无登录墙 | PASS | 全部内容静态可读 |
| GSC 提交 | **需人工确认** | 需在 Search Console 验证域名并提交 sitemap |

### 关于「custom domain 必须用 www」

提示词要求把自定义域名改为 `www.omnishelf.cc`。**未执行**,理由:

1. GitHub Pages **支持** apex 自定义域名(通过 Cloudflare 的 CNAME 扁平化),不存在"项目仓库必须使用 www"的限制;
2. 当前证书 SAN 已同时覆盖 apex 与 www,`www` 已 301 到 apex;
3. 改为 www 需要重新签发证书、修改 canonical、并把 Play 商店里已填的 `omnishelf.cc/privacy/` 全部改链接——收益为零,风险是审核期间换域名导致可达性抖动。

如确需统一到 www,可一次完成:改 Pages custom domain → 等证书 → 全站 canonical/sitemap 换 www → apex 301 到 www → 更新 Play 链接。

---

## 7. 提交就绪判定

**P0 两项已完成**(EEAT 结构、隐私五项)。**P1 三项已完成**(节奏、导航与首页、定向修复)。

剩余硬指标:

| 项 | 状态 |
|---|---|
| 作者身份可核验 | ✅ 账号、仓库、邮箱三线可查;姓名为笔名并已披露 |
| 隐私政策五项逐项覆盖 | ✅ 表 2 |
| 首页为出版物入口 | ✅ 唯一 H1、480 字导言、15 条文章流 |
| 单页 H1 唯一、无 404、无广告代码 | ✅ |
| Play 隐私政策地址 | ⚠️ 仍指向旧 github.io 地址(会 301 到新站),建议改为 `https://omnishelf.cc/privacy/` |
| Search Console | ⚠️ 未验证 |

**结论:可以提交。** 目前没有阻止提交的硬伤。

若现在提交最可能出现的拒信关键词,按概率排序:

1. `Low value content` — 剩余风险来自"仅 6 篇编辑内容",工程笔记占 9/15;缓解手段是按下表节奏补到 18 篇
2. `Site under construction` — 概率低,无占位页
3. `Privacy policy incomplete` — 概率低,五项已逐条覆盖

最可能仍被卡住的是第一条,而它的解法是**按周补内容**,不是继续改结构。
