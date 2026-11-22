# 上线检查清单(W3/W4)

## 站点侧(已全部完成)

- [x] 首页单一主张 + 单一主按钮
- [x] `/download/` 说明安装、步骤、能力与边界
- [x] `/beta/` 内测加入流程(未上架时 CTA 正确落点)
- [x] `/go/play/` 唯一跳转 + `noindex` + 参数透传
- [x] 书库 `/books/`、`/books/free/`(30 本)、`/books/paid/`
- [x] 单本书页 + `Book` 结构化数据 + 出版方下载入口
- [x] 品牌色与 App 一致(朱砂 #8E2B21)
- [x] 移动端无横向溢出(500px 视口实测 scrollWidth=viewport)
- [x] 站内搜索 `/search/`(67 条,前端过滤)
- [x] sitemap 86 条、robots 指向 sitemap
- [x] `/dmca/`、`/privacy/`、`/terms/`、`/404`
- [x] 无广告、无 Cookie 追踪脚本
- [x] App 端 Install Referrer(1.3.1,可测站内来源安装)

## 需要你操作(3 项)

1. **GSC / Bing 验证**:在 Search Console 与 Bing Webmaster 添加 `omnishelf.cc`,
   把 HTML 验证文件/字符串给我 → 我放进 `web/public/` 并部署 → 你在浏览器点确认。
2. **分析 ID(可选)**:Umami 或 Cloudflare Web Analytics 的站点 ID 给我 →
   我配置 `PUBLIC_UMAMI_SRC` / `PUBLIC_UMAMI_WEBSITE_ID` 并部署;不配也能跑,只是没有计数。
3. **Play 上架日**:告诉我上架时间 → 我把 `web/src/site.ts` 的 `play.published` 改为 true,
   全站按钮从"内测"切到商店直链,并把 referrer 打开。

## 上线后 7 天观察项

- GSC 收录页数(目标:7 天内 > 50% 页面)
- 首页 → `/go/play/` 点击率
- 书页下载按钮点击(等分析接入后)
- Play Console → 安装来源(等上架后,referrer 生效)
- 服务器:GitHub Pages 构建时长与失败情况
