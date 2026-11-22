# Google Search Console / Bing 提交清单

站点已具备收录所需的一切:静态页、`sitemap.xml`(156 条)、`robots.txt`、
`canonical`、`Book` / `Article` / `WebSite` / `BreadcrumbList` 结构化数据、`SearchAction` 站内搜索。

## 一、Google Search Console

1. 打开 https://search.google.com/search-console ,添加「网址前缀」资源:`https://omnishelf.cc`
2. 验证方式选 **HTML 标记**:
   - 复制形如 `<meta name="google-site-verification" content="XXXX">` 里的 **content 值**;
   - 发给我,我在部署时通过仓库 Secret `PUBLIC_GOOGLE_SITE_VERIFICATION` 注入 → 重新部署 → 你点「验证」。
   - (也可以选 DNS 验证,由你在域名解析里加 TXT,效果相同。)
3. 验证通过后:**站点地图 → 添加站点地图** → 填入 `sitemap.xml` → 提交。
4. 观察项:7 天内「已编入索引」页数;「网页体验」;「增强功能 → 面包屑 / 图书」是否识别。

## 二、Bing Webmaster Tools

1. https://www.bing.com/webmasters 添加站点 `https://omnishelf.cc`;
2. 验证方式选「Meta 标记」,把 content 值发我 → 通过 Secret `PUBLIC_BING_SITE_VERIFICATION` 注入;
3. 提交 `https://omnishelf.cc/sitemap.xml`;
4. 可选:在 Bing 里生成 **IndexNow 密钥**,发我,我运行
   `./scripts/submit-indexnow.sh <key>` 主动推送全部 URL(脚本会同时在站点根发布该密钥文件)。

## 三、提交后我会盯的指标

| 指标 | 目标 |
|---|---|
| 已编入索引页数 | 7 天 > 50%,30 天 > 80% |
| 站点地图错误 | 0 |
| 图书增强功能识别 | 书页被识别为 Book |
| 面包屑增强功能 | 文章/书页识别 Breadcrumb |
| 搜索查询出现 | 30 天内出现「免费 epub」「公版书」「epub 阅读器」等词 |

## 四、技术侧已就位(不需要操作)

- `sitemap.xml` 每次构建自动生成(156 条,含 100 本书、21 篇文章、16 篇工程笔记)
- `robots.txt` 放行全站、屏蔽 `/go/`(跳转页已 `noindex`)
- 每页 `canonical` + `og:` + `twitter:card`
- 结构化数据:`WebSite`+`SearchAction`(首页)、`Book`+`BreadcrumbList`(书页)、
  `Article`+`BreadcrumbList`(文章)、`SoftwareApplication`(首页)、`FAQPage`(下载页)
- 站内搜索 `/search/?q=` 可被 `SearchAction` 直接调用
