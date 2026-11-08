# OmniShelf.cc 站点交付说明

> 技术栈:Astro 5.18(static output),零 JavaScript 也能读到全文。
> 构建:`cd web && pnpm build` → 产物在 `web/dist/`。
> 已构建验证:**17 个页面 + sitemap.xml + robots.txt**,无 404、无占位页。

---

## 1. 站点地图

| URL | 核心主题 | 实际字数 |
|---|---|---|
| `/` | 编辑导言:为什么"阅读不是格式问题" | 768 |
| `/why/` | 设计主张:与主流阅读器的根本分歧 | 1002 |
| `/notes/` | 文章索引 + 栏目说明 | 580 |
| `/notes/01-pdf-page-numbers/` | PDF 重排与引用页码的冲突 | 1062 |
| `/notes/02-two-kinds-of-position/` | 一个书架里两套"读到哪了" | 921 |
| `/notes/03-highlights-in-greyscale/` | 高亮在灰度屏上的存亡 | 992 |
| `/notes/04-tap-zones/` | 单手拿手机时的翻页热区几何 | 1015 |
| `/notes/05-hyphenation-default/` | 为什么自动断字默认关闭 | 934 |
| `/notes/06-tts-foreground-service/` | 息屏朗读是权限问题,不是语音问题 | 847 |
| `/notes/07-library-scan/` | "找到我的书"到底需要什么权限 | 849 |
| `/notes/08-format-list/` | 34 种格式不是卖点,是失败模式的承诺 | 1086 |
| `/notes/09-error-dialogs/` | 打不开文件时该说什么 | 968 |
| `/shelf/` | 应用现状与变更记录(轻量) | 421 |
| `/about/` | 谁在维护、为何建站、如何被资助、不是什么 | 526 |
| `/contact/` | 联系方式与有效 bug 报告的四要素 | 337 |
| `/privacy/` | Cookie、第三方广告、AdSense、退出机制 | 804 |
| `/terms/` | 服务条款、AGPLv3、无担保 | 556 |

合计约 **13,000 词**,其中可被 AdSense 视为"内容主体"的长文 **9 篇 / 8,600 词**。

---

## 2. 九篇文章:标题与核心论点

| # | 标题 | 一句话论点 |
|---|---|---|
| 01 | A PDF on a phone does not lose resolution — it loses the page number | 重排把引用赖以为生的页码变成了一个计算产物,所以原文页码必须作为唯一真相保留 |
| 02 | One shelf, two ideas of where I stopped | PDF 的位置是坐标、EPUB 的位置是文本指针,把两者塞进一个书库是一次不可见的工程妥协 |
| 03 | A highlight that disappears on an e-ink screen | 高亮颜色不能按色相选,必须按亮度差选,否则在墨水屏上等同于没标 |
| 04 | The left-edge tap zone that a one-handed reader cannot reach | 翻页热区要按手的几何设计,不能按布局的几何设计 |
| 05 | Why automatic hyphenation is off until you ask for it | 默认值应该选择"失败得可以理解"的那一侧:断字错误的破坏力远大于右缘不齐 |
| 06 | Listening with the screen off is a policy problem before it is a voice problem | 息屏朗读是前台服务权限问题,权限是功能的一部分,申报材料也是 |
| 07 | What "find my books" actually requires from Android | SAF 与 MediaStore 都无法索引跨目录的既有书库,这是广域文件权限唯一的正当理由 |
| 08 | Thirty-four formats is not a feature, it is a promise about failure | 格式清单不是能力分数,是"哪些解析器会在你的文件上运行、崩溃时怎样"的承诺 |
| 09 | What a reader should say when the file will not open | 错误路径是产品价值观最藏不住的地方——我们在这里发现了一个付费导流入口 |

每篇都包含至少一处具体的技术选择或可观察行为(重排后的页码换算、灰度亮度分离、360dp 下的 72dp 热区、前台服务类型申报、树形 URI 授权的三条限制、崩溃循环的预写标记等),不是形容词堆砌。

---

## 3. 首页文案

已在 `web/src/pages/index.astro`,**768 词**,结构:

- `h1` Reading is not a format problem.
- 小标题:书架即论点 / 我们删掉了什么,以及这不是道德立场 / 我们保留了什么,代价是什么 / 这个站点是干什么的

主 CTA **不是** "Download now",而是指向 `/why/` 与文章的内链。下载入口只出现在 `/shelf/`。

---

## 4. Why OmniShelf

`web/src/pages/why.astro`,**1002 词**,七个部分:格式是文件的属性而非阅读生活的属性 / 删掉的东西就是论点 / 离线是默认而非模式 / 字体排印不是装饰 / 应用不会做的事 / 这是给谁的。

与 librera.mobi 的差异:不做功能对比表,全文没有一个"功能矩阵",所有能力都以论据形式出现。

---

## 5. 信任页面

| 页面 | 关键内容 |
|---|---|
| `/about/` | 维护者身份(开源项目、无公司、无投资人)、真实邮箱、建站目的、资助方式、**明确声明与 Librera 及任何同名产品无关**、纠错机制 |
| `/contact/` | 邮箱、GitHub Issues、有效 bug 报告四要素、安全问题的私下渠道 |
| `/privacy/` | 应用零收集 / 站点 Cookie / **Google AdSense 与第三方广告** / **个性化广告** / **退出机制**(Ads Settings、aboutads.info、youronlinechoices、浏览器控制)/ 日志 / 儿童 / 权利 / 变更 |
| `/terms/` | 站点使用、应用以 AGPLv3 授权、无担保、禁止行为、第三方链接与广告、内容性质、变更、联系方式 |

四个页面均可从顶部导航与页脚到达。

---

## 6. 设计 token

| 类别 | 值 |
|---|---|
| 浅色底 | `#F7F5F0` |
| 浅色前景 | `#2B2B28` |
| 深色底 | `#1A1A18` |
| 深色前景 | `#E8E6E1` |
| 分隔线 | `#DDD8CE` / 深色 `#34322D` |
| 弱化文字 | `#6B6660` / 深色 `#9A958C` |
| 强调色(唯一) | `#4A5D4E`(墨绿);深色模式 `#8AA68F` |
| 正文字体 | `"Source Serif 4", "Literata", "Iowan Old Style", Georgia, "Songti SC", serif` |
| 界面字体 | `ui-sans-serif, system-ui, …`(仅用于 meta、导航、页脚) |
| 字号层级 | 0.875 / 1.0625 / 1.25 / 1.5 / 2 / 2.5 rem |
| 行高 | 正文 1.72;标题 1.15–1.35 |
| 单栏宽度 | `40rem`(约 68–72 字符) |
| 间距体系 | 0.5 / 0.875 / 1.5 / 2.5 / 4 rem |

**没有主题切换器**:深色模式跟随系统 `prefers-color-scheme`。禁止纯黑 `#000` 与纯白 `#FFF`。

---

## 7. 组件清单与约束

| 组件 | 纸感阅读下的约束 |
|---|---|
| `Base.astro` | 静态输出;每页唯一 title/description/canonical;文章页注入 `Article` 结构化数据;favicon 与 theme-color 跟随明暗 |
| `Header.astro` | 4 个一级导航;无搜索框、无 CTA 按钮;当前页用下划线而非色块 |
| `Footer.astro` | 固定五项:About / Contact / Privacy / Terms / Reading Notes;一句版权;不放大字标 |
| `ArticleCard.astro` | **整卡是 `<a>`,没有"阅读更多"按钮**;标题 + 真实摘要 + 日期与阅读时长;悬浮只加下划线,不做位移与阴影 |
| `Prose.astro` | 单栏容器;标题、段落、引用、代码、图注样式统一;不插入推荐模块 |
| `notes/[...slug].astro` | 文章页;结尾是"下一篇"内链而非下载广告;`Article` JSON-LD |
| `sitemap.xml.ts` | 构建期生成的静态 sitemap,含全部 17 页 |

---

## 8. AdSense 审核前自查清单

### 红线逐条核对

- [x] **主落地页不是功能列表**:首页 768 词编辑导言 + 文章流,无截图墙、无图标矩阵、无"Download on Google Play"主按钮
- [x] **无 AI 生成痕迹过重的内容**:9 篇文章均含具体技术选择与可观察行为(页码换算、灰度亮度、72dp 热区、前台服务类型、树形 URI 授权、崩溃预写标记),不是"支持多种格式、体验流畅"
- [x] **不复刻 librera.mobi 结构**:没有 Features / Formats / Settings 目录式栏目;功能只作为论据出现
- [x] **信任页面齐全**:Privacy / About / Contact / Terms 四个页面,顶部导航与页脚均可到达
- [x] **About 有可核验身份感**:维护者身份、真实邮箱、GitHub 仓库、资助方式、与同名产品无关联声明
- [x] **Privacy 披露完整**:Cookie / 第三方广告服务 / Google AdSense / 个性化广告 / 退出机制(四个渠道)
- [x] **全站 HTTPS + 移动端可读**:静态站点,`viewport` 已设,窄屏单栏
- [x] **无 404 / 无 under construction**:17 页全部有内容,构建产物中无占位页
- [x] **审核前未部署 AdSense 代码**:全站无任何广告脚本
- [x] **无需登录即可阅读全文**:没有任何登录墙

### 提交前的人工动作(代码之外)

- [ ] 域名解析到站点(Cloudflare → GitHub Pages,灰色云)
- [ ] 用手机实际打开首页与任意一篇文章,确认排版不溢出
- [ ] 提交 sitemap 到 Google Search Console
- [ ] 在 AdSense 后台提交 `https://omnishelf.cc/`,等待审核
- [ ] 审核通过后才在页面中部署广告位(建议只放在文章页正文之后,不进正文中间)

### 已知风险

1. **域名未解析前站点不可访问**——这是当前唯一阻塞项。
2. `github.io/omnishelf/privacy/` 是 Play 商店里填的隐私政策地址。**在域名切换完成之前,不要替换 GitHub Pages 上的旧静态页**,否则 Play 侧的链接会 404。
3. AdSense 对"新站 + 少量长文"的通过率不稳定,9 篇 800–1100 词的长文是下限而非保险;若首次被拒,补充 3–5 篇同规格文章比修改现有文章更有效。

---

## 部署(待 DNS 就绪后执行)

1. 在 `web/astro.config.mjs` 保持 `site: 'https://omnishelf.cc'`
2. 修改 `.github/workflows/pages.yml`:增加 pnpm 安装与 `pnpm build`,上传 `web/dist`
3. 在仓库 Settings → Pages 设置 Custom domain 为 `omnishelf.cc`
4. Play 商店的隐私政策地址改为 `https://omnishelf.cc/privacy/`
5. 确认新地址可访问后,删除旧的 `site/` 目录
