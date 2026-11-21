# OmniShelf 1.3.0 验证报告 · 2026-11-21

## 结论摘要

| 项目 | 结果 |
|---|---|
| 签名包与 AAB | Play 上传密钥 `5B:64:C1:…:6C`,单一证书链,versionName **1.3.0**(versionCode 4) |
| 设备验证(逐项) | 见下,全部通过 |
| 6 套卡片真实渲染 | 已从设备导出 1080×1440 PNG 六张,逐张核对(见 `device-six-templates.png`) |
| 多语言 | 介绍词、扫码引导、二维码地址随系统语言;56 个语言文件 743 键零差异 |

## 设备验证(scripts/verify-1.3.0.sh,签名包)

```
PASS  install
      installed version: 1.3.0
PASS  first-run question shown
PASS  book opened
PASS  selection panel has the card row
PASS  preview dialog opened
PASS  all six layouts rendered (visited 6 distinct counters)
PASS  no crash while flipping templates
PASS  share sheet opened

== 8 passed, 0 failed ==
```

最终包为 `OmniShelf-1.3.0-arm64-signed.apk`(Play 上传密钥签名,SHA-1 5B:64:C1:…:6C),
AAB `OmniShelf-1.3.0.aab` 同密钥、单一证书链、versionName 1.3.0、versionCode 4。

## 本轮验证发现并修复的缺陷

| # | 缺陷 | 影响 | 修法 |
|---|---|---|---|
| 1 | 颜色常量写成 `0x241815`(6 位) | Java 中 alpha=0 → **整张卡透明**,只剩二维码 | 24 处颜色全部补 `0xFF` |
| 2 | 深色卡二维码用深墨色 | 深底深码,**扫码不可用** | 模块颜色随底色反转 |
| 3 | 短引文时内容贴顶、下方留大洞 | 版面失衡 | 内容块在区间内垂直居中 |
| 4 | `pruneOldCards` 在重写中丢失 | 编译失败(CI 捕获) | 恢复并保留 7 天清理 |

## 上一版遗留的两项,本轮已解决并验证

| 遗留 | 根因 | 修法 | 验证 |
|---|---|---|---|
| 选词面板图标/文字是系统青绿 `#008577` | 平台链接色在绘制时生效,任何资源覆盖都被它压过;行也不带预期 tag | 按行结构(图标+文字)识别,图标 `setColorFilter`,文字加 `ForegroundColorSpan` | 截图取样 `#8E2B21`,见 `panel-brand-colour.png` |
| 卡 2/4 无真实封面 | `IMG.getCoverPageWithEffect` 只返回请求对象,未调用 `submit()`;即便调用,自制 EPUB 也返回空 | 先 `submit()`;为空时**直接解包 ZIP 取内嵌图片**(EPUB/CBZ 通用),后台线程解码 | 日志 `900x1350`,卡片显示书本原封面,见 `device-final-template-2-cover.png` |

## 与定稿的差异(设计决定,非缺陷)

- 卡 2 的日期行在定稿里写作「摘录于 2026/11/21」,实现里为本地化日期(如「2026 年 11 月 21 日」),
  去掉了「摘录于」前缀以免再加一条 55 语言文案;如需完全一致可补一条字符串。
- 字体按角色替换:定稿为 Songti + Helvetica,设备为内置 Noto Serif SC + 系统无衬线;
  字号、字距、坐标与定稿逐一相同。

## 证据文件

| 文件 | 内容 |
|---|---|
| `device-card-template*.png` | 设备真实导出的卡片(1080×1440) |
| `01-card-preview-dialog.png` / `02-card-preview-next.png` | 预览对话框与模板切换 |
| `../../store-assets/share-card-promo.png` | 商店用卡片宣传图 |
