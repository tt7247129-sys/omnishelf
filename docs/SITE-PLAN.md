# OmniShelf.cc — 交付说明

> 修订版。与上一版的差异:页面文案全部迁入内容集合、独立 `tokens.css`、指定字体栈、
> 部署工作流与 Cloudflare 切换顺序。
>
> 构建:`cd web && pnpm build` → `web/dist/`。已验证 **17 页 + sitemap + robots**,0 死链。

---

## 1. 仓库结构与部署

```
omnishelf/
├── .github/workflows/deploy.yml    构建 web/ 并发布 dist 到 Pages
├── web/
│   ├── astro.config.mjs            site: https://omnishelf.cc, output: static
│   ├── package.json                astro 5.18 + 两个自托管字体包
│   ├── public/                     robots.txt / icon.png / apple-touch-icon.png
│   └── src/
│       ├── content.config.ts       collections: articles, pages
│       ├── content/articles/*.md   9 篇长文
│       ├── content/pages/*.md      首页 / why / about / contact / privacy / terms / shelf
│       ├── layouts/Base.astro      <head>、Header、Footer
│       ├── layouts/Page.astro      渲染一个 pages 条目
│       ├── components/             Header / Footer / ArticleCard / Prose
│       ├── pages/                  路由 + sitemap.xml.ts
│       └── styles/tokens.css       全部视觉变量
└── site/                           旧的静态占位页,域名切完即删
```

`astro.config.mjs` 关键三项:`site: 'https://omnishelf.cc'`、`output: 'static'`、`build.format: 'directory'`。

`deploy.yml` 要点:

- 触发:`push` 到 **`site-live`** 分支 + `workflow_dispatch`。**刻意不挂 master** —— 域名迁移期间,一次普通推送就把线上的隐私政策页打掉是不可接受的。
- Node 22 / pnpm 9 / `--frozen-lockfile`
- 构建后先自检:`index.html`、`privacy/index.html`、`sitemap.xml`、`robots.txt` 必须存在,且页面数 ≥ 17,否则失败
- 产物 `web/dist` 经 `upload-pages-artifact` → `deploy-pages`

### Cloudflare 与 GitHub Pages 的切换顺序(顺序错了会进重定向循环)

目标是 **CNAME → `tt7247129-sys.github.io`,橙色云,SSL Full (strict)**。但这不是第一步:

1. GitHub → Settings → Pages → Custom domain 填 `omnishelf.cc`,保存
2. Cloudflare DNS 先加**灰色云**两条:`@` 与 `www` → `tt7247129-sys.github.io`
3. 等 GitHub 签发证书(几分钟到一小时),`https://omnishelf.cc` 直连可访问
4. Cloudflare SSL/TLS 改为 **Full (strict)**,开启 Always Use HTTPS
5. 两条记录再切**橙色云**
6. 验证:`curl -sI https://omnishelf.cc | head -1` 为 200/301,浏览器证书有效且无循环

> 橙色云先开会挡住证书验证;Full (strict) 先设会 526。

---

## 2. 站点地图(实测渲染字数)

| URL | 主题 | 字数 |
|---|---|---|
| `/` | 编辑导言:阅读不是格式问题 | 928(含 3 张卡片摘要,正文约 700) |
| `/why/` | 设计主张 | 1002 |
| `/notes/` | 文章索引 | 580 |
| `/notes/01-pdf-page-numbers/` | 重排与页码引用 | 1062 |
| `/notes/02-two-kinds-of-position/` | 两套"读到哪了" | 921 |
| `/notes/03-highlights-in-greyscale/` | 高亮在灰度屏 | 992 |
| `/notes/04-tap-zones/` | 单手翻页热区 | 1015 |
| `/notes/05-hyphenation-default/` | 断字默认关闭 | 934 |
| `/notes/06-tts-foreground-service/` | 息屏朗读与权限 | 847 |
| `/notes/07-library-scan/` | 找到我的书需要什么 | 849 |
| `/notes/08-format-list/` | 格式清单的真相 | 1086 |
| `/notes/09-error-dialogs/` | 打不开时该说什么 | 968 |
| `/shelf/` | 应用现状与变更记录 | 421 |
| `/about/` | 谁在运营、为何建站、不是什么 | 526 |
| `/contact/` | 联系方式与有效报告 | 341 |
| `/privacy/` | Cookie / AdSense / 个性化广告 / 退出机制 | 818 |
| `/terms/` | 条款、AGPLv3、无担保 | 554 |

合计约 **13,700 词**,其中 9 篇长文 **8,600 词**。

---

## 3. 九篇文章:标题与核心论点

| # | 标题 | 论点 |
|---|---|---|
| 01 | A PDF on a phone does not lose resolution — it loses the page number | 重排把页码变成计算产物,原文页码必须作为唯一真相 |
| 02 | One shelf, two ideas of where I stopped | PDF 位置是坐标、EPUB 是指针,同库共存是一次不可见的妥协 |
| 03 | A highlight that disappears on an e-ink screen | 高亮按亮度差选色,否则墨水屏上等于没标 |
| 04 | The left-edge tap zone that a one-handed reader cannot reach | 热区要按手的几何设计,不是按布局的几何设计 |
| 05 | Why automatic hyphenation is off until you ask for it | 默认应选"失败得可以理解"的一侧 |
| 06 | Listening with the screen off is a policy problem before it is a voice problem | 息屏朗读先是前台服务权限问题,申报材料也是功能的一部分 |
| 07 | What "find my books" actually requires from Android | SAF 与 MediaStore 都无法索引跨目录书库,这是广域文件权限唯一的正当理由 |
| 08 | Thirty-four formats is not a feature, it is a promise about failure | 格式清单是"哪些解析器会跑、崩了怎么办"的承诺 |
| 09 | What a reader should say when the file will not open | 错误路径最藏不住产品价值观——我们在这里删掉了一个付费导流入口 |

每篇结尾是**下一篇的真实标题内链**(不是 "Read more"),`ArticleCard` 整卡可点。

---

## 4. 首页文案

`web/src/content/pages/index.md`。结构:`# Reading is not a format problem.` →
导语 → `## The shelf is the argument` → `## What we removed, and why it is not a moral
position` → `## What we kept, and what it costs` → `## What this site is for`。

主 CTA 不是下载,是指向 `/why/` 与文章的内链。下载入口只在 `/shelf/`。

---

## 5. Why OmniShelf

`web/src/content/pages/why.md`,1002 词。七个部分,无功能对比表,所有能力以论据出现。

---

## 6. 信任页面

| 页面 | 关键内容 |
|---|---|
| `/about/` | 维护者身份、真实邮箱、建站目的、资助方式、**与 Librera 及任何同名产品无关**、纠错机制 |
| `/contact/` | 邮箱、GitHub Issues、有效 bug 报告四要素、安全问题私下渠道 |
| `/privacy/` | 应用零收集 / 站点 Cookie / **AdSense 与第三方广告** / **个性化广告** / **退出机制四渠道** / 日志 / 儿童 / 权利 / 变更 |
| `/terms/` | AGPLv3 授权、无担保、禁止行为、第三方链接与广告、变更 |

四页均在顶部导航或页脚可达。

---

## 7. 视觉 token 与组件

**`src/styles/tokens.css`** 是唯一变量文件,取值按 brief 固定:

| 变量 | 浅色 | 深色 |
|---|---|---|
| `--canvas` | `#F7F5F0` | `#1A1A18` |
| `--ink` | `#2B2B28` | `#E8E6E1` |
| `--rule` | `#D8D4CC` | `#3A3A36` |
| `--accent` | `#4A5D4E` | `#8AA68F` |
| 正文 | Literata 1.125rem / 1.7 | 同 |
| 度量 | `68ch` 单栏 | 同 |
| UI 字体 | IBM Plex Sans(仅导航与元数据) | 同 |

字体通过 `@fontsource/literata` 与 `@fontsource/ibm-plex-sans` **自托管**,不走 CDN。

| 组件 | 纸感约束 |
|---|---|
| `Base.astro` | 每页唯一 title/description/canonical;文章页注入 `Article` JSON-LD;字体自托管 |
| `Header.astro` | 4 个一级导航;无搜索框、无 CTA;当前页用下划线 |
| `Footer.astro` | 固定五项 + 一句版权;字号小于正文 |
| `ArticleCard.astro` | **整卡 `<a>`,没有"阅读更多"按钮**;摘要来自正文真实段落 |
| `Prose.astro` | 单栏 68ch;无侧边栏;`h1 + p` 由 CSS 承担导语样式(Markdown 无法带 class) |

---

## 8. 审核前自查清单

- [x] 主落地页非功能列表:首页正文约 700 词 + 3 张文章卡,无截图墙、无图标矩阵、无下载主按钮
- [x] 无 AI 痕迹内容:9 篇均含具体技术选择(页码换算 / 灰度亮度 / 72dp 热区 / 前台服务类型 / 树形 URI 授权 / 崩溃预写标记)
- [x] 功能只作论据:全站无 Features / Formats / Settings 栏目
- [x] 信任页面齐全且双向可达
- [x] About 有可核验身份、真实邮箱、目的声明、非同源声明
- [x] Privacy 显式披露 Cookie / AdSense / 第三方广告 / 个性化广告 / 退出机制
- [x] 全站 HTTPS(部署后)、移动端单栏、17 页无 404 无占位
- [x] **未部署任何 AdSense 代码**
- [x] 无需登录
- [x] 正文存在于 HTML(禁用 JS 可读),文案全部在 Markdown 内容集合

### 已完成的部署

- 域名 `omnishelf.cc` 已解析到 GitHub Pages(CNAME 扁平化,4 个 A 记录由 Cloudflare 生成)
- GitHub 证书已签发,HTTPS 已强制
- `https://omnishelf.cc/` 等 11 个 URL 全部 200
- 字体自托管并正常返回
- 旧地址 `github.io/omnishelf/privacy/` → 301 → `omnishelf.cc/privacy/`
- 旧的 `site/` 占位目录与 `pages.yml` 已删除

> 部署顺序与 brief 的差异:Cloudflare 代理目前仍是**灰色云**。橙色云 + Full (strict)
> 可作为后续优化,但需要先确认源站证书已就位,否则会 526。当前灰云 + GitHub 证书
> 已能提供有效 HTTPS,不影响审核。

### 提交前人工动作

- [ ] Play Console 把隐私政策地址改为 `https://omnishelf.cc/privacy/`
- [ ] 手机打开首页与任意文章,确认排版
- [ ] Search Console 提交 sitemap
- [ ] AdSense 提交 `https://omnishelf.cc/`
- [ ] **审核通过后**再加广告位(建议仅置于文章正文之后)

### 风险

1. 域名未切换前站点不可访问——当前唯一阻塞项。
2. `github.io/omnishelf/privacy/` 是 Play 商店当前填写的地址;域名切换并确认新地址可用之后,再改 Play 设置,不要提前替换 Pages 内容。
3. 新站 9 篇长文是下限。首次被拒时,补 3–5 篇同规格文章比修改现有文章有效。


## 站点复审(2026-11-14,对照 adsense-playbook)

- ✅ `/privacy/`:五项披露齐全(网站 Cookie、第三方广告、按访问记录投放的说明、退出渠道、
  其他广告网络条款)+ 应用零收集说明,与应用数据安全声明一致
- ✅ `/authors/`:一人实名化写作,说明为什么用笔名,可核验渠道(仓库账号 + 邮箱)
- ✅ 文章质量:第一人称、有具体场景与反转,非模板文
- ✅ 无任何广告代码(`adsbygoogle`/`ca-pub-` 零命中)
- ✅ `pnpm build`:27 页,3.4s(用系统 Node v22 构建)

**构建环境注意**:DSH 自带的 Node 无法加载 rollup 的原生绑定(macOS Team ID 校验),
用系统 `/usr/local/bin/node` + `pnpm build` 即可;重装 node_modules 后如再遇到
`ERR_DLOPEN_FAILED`,先用 `codesign --force --sign -` 重签 `.node` 或换系统 Node。
