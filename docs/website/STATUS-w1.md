# 网站 W1 状态(2026-11-22)

## 结论:W1 已上线

线上地址:**https://omnishelf.cc**

| 页面 | 状态 | 作用 |
|---|---|---|
| `/` | ✅ 已改造 | 首屏一句话主张 + 唯一主按钮 + 二维码 + 信任条(34 格式/55 语言/0 广告/0 账号/0 追踪) |
| `/download/` | ✅ 新增 | 安装说明、三步上手、能做什么/不做什么、FAQ(含 FAQPage 结构化数据) |
| `/beta/` | ✅ 新增 | 内测加入流程(邮件 → 加名单 → Play 测试页),当前 CTA 的正确落点 |
| `/go/play/` | ✅ 新增 | 唯一跳转页:`?src=` 记录来源;未上架→`/beta/`,上架后→Play 并带 `referrer`(utm_content=来源) |
| `/dmca/` | ✅ 新增 | 版权与下架通道(hello@omnishelf.cc) |
| `/404` | ✅ 新增 | 兜底页,带下载按钮 |

- 站点品牌色改为 App 同款朱砂 `#8E2B21`(暗色 `#D2503C`)
- 顶部导航新增 **Download**;页脚新增 Download / Closed test / Copyright
- sitemap 收录新页面;`/go/play/` 设 `noindex`
- 二维码:`web/public/qr/play.svg`,内容 `https://omnishelf.cc/go/play/?src=qr`(可归因);重新生成:`./scripts/make-site-qr.sh`
- 埋点:所有 CTA 带 `data-track="download_click"`;Base 有一个无 Cookie 的点击监听,
  配置 `PUBLIC_UMAMI_SRC` / `PUBLIC_UMAMI_WEBSITE_ID` 后即可计数(不配置则零脚本)

## 线上验收(2026-11-22 14:50)

```
/            200     /beta/       200
/download/   200     /dmca/       200
/go/play/    200 → url=/beta/      /qr/play.svg 200
```

- 首页 CTA:`/go/play/?src=home-hero`、`/go/play/?src=home-sticky`
- 下载页 CTA:`/go/play/?src=download-primary`、`/go/play/?src=download-sticky`
- 移动端(视口 500,Chrome 无头最小宽度):scrollWidth = viewport,无横向溢出

## 交付物

- 截图:`docs/website/screens/`(home-desktop / download-desktop / home-mobile / download-mobile / beta-desktop)
- 方案:`docs/website/PLAN-v2.md`、一页图 `PLAN-v2-overview.png`
- 提交:`8bfa005`(首版) → `d50bd88`(版心与导航)→ `a65df60`(内测漏斗修正)

## 待办(下一步)

| 事项 | 谁做 | 说明 |
|---|---|---|
| GSC / Bing 验证 | 你:建资源 → 我放验证文件 → 你点确认 | 建好后告诉我,我把文件写进 `web/public/` 并重新部署 |
| 分析开关 | 你:给我 Umami(或 Cloudflare)站点 ID | 我加环境变量并部署;不配也能跑,只是无计数 |
| W2 书库 | 我 | books 集合 + 书页模板 + 首批 100 本公版书(EPUB) |
| App 端 Install Referrer | 我(W4) | 让"站内来源安装数"可测;上架前完成即可 |
| Play 上架 | 你(封闭测试走完) | 上架当天我把 `site.ts` 的 `play.published` 改 true,全站自动切商店直链 |

## 现在可以做什么

1. 用手机打开 https://omnishelf.cc ,点「Join the closed test」,看是否走到 `/beta/` 页面。
2. 扫首页二维码,应打开同一个内测说明页。
3. 把内测测试员账号发我(或按 `/beta/` 的说明收邮件),我确认 CTA 文案是否要改成"留下邮箱"。
