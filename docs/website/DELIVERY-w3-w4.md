# W3/W4 交付清单(2026-11-22)

## 站点(已上线)

| 交付 | 地址 | 验收 |
|---|---|---|
| 站内搜索 | https://omnishelf.cc/search/ | 67 条索引;`?q=melville` 可分享;前端过滤不发请求 |
| 知识库 +9 篇(共 21) | /reading-craft/ 6 · /where-to-read/ 10 · /reading-life/ 5 | 全部 200,含 Article 结构化数据 |
| 首本自托管书 | /books/omnishelf-guide/ · /books/omnishelf-guide.epub | EPUB 合法(13 条目),CC BY 4.0,项目自有版权 |
| 书库 | /books/、/books/free/(30 本)、/books/paid/ | 全部 200 |
| 法务 | /dmca/、/privacy/、/terms/ | 200 |
| sitemap | /sitemap.xml | 86 条 URL |

## App(1.3.1)

- 安装归因:`InstallAttribution` 读取 Play Install Referrer,`utm_*` 存本地(`AppState.installReferrer`),
  **不联网、不采集广告 ID**;非 Play 安装静默跳过
- 版本 1.3.1(versionCode 5),CI 编译通过
- 签名包:`delivery/OmniShelf-1.3.1.aab`(33MB)、`delivery/OmniShelf-1.3.1-arm64-signed.apk`(50MB)
- 证书:SHA-1 `5B:64:C1:A4:30:C2:B2:57:16:8F:98:30:C3:C9:FB:67:96:BE:BA:6C`(Play 上传密钥)

## 版权与合规(本轮自查)

- 免费书 30 本:29 本公版(文件由 Standard Ebooks 托管)+ 1 本自有(我们撰写并生成)
- 付费区:仅店铺链接,无文件,明确声明暂无联盟合作
- 不抓取、不镜像、不转存任何他人版权文件
- 中文公版书**未批量上线**:Wikisource 导出有反爬,逐本校对成本高,留待下一轮

## 仍需你操作(3 项)

1. GSC / Bing 验证(给我验证文件,我部署,你确认)
2. 分析 ID(Umami / Cloudflare,可选)
3. Play 上架日(我把 `play.published` 切 true,启用商店直链与 referrer)
