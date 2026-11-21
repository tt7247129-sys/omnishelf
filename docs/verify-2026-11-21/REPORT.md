# OmniShelf 1.3.0 验证报告 · 2026-11-21

## 结论摘要

| 项目 | 结果 |
|---|---|
| 签名包与 AAB | Play 上传密钥 `5B:64:C1:…:6C`,单一证书链,versionName **1.3.0**(versionCode 4) |
| 设备验证(逐项) | 见下,全部通过 |
| 6 套卡片真实渲染 | 已从设备导出 1080×1440 PNG,逐张核对 |
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

## 本轮验证发现并修复的缺陷

| # | 缺陷 | 影响 | 修法 |
|---|---|---|---|
| 1 | 颜色常量写成 `0x241815`(6 位) | Java 中 alpha=0 → **整张卡透明**,只剩二维码 | 24 处颜色全部补 `0xFF` |
| 2 | 深色卡二维码用深墨色 | 深底深码,**扫码不可用** | 模块颜色随底色反转 |
| 3 | 短引文时内容贴顶、下方留大洞 | 版面失衡 | 内容块在区间内垂直居中 |
| 4 | `pruneOldCards` 在重写中丢失 | 编译失败(CI 捕获) | 恢复并保留 7 天清理 |

## 已知遗留(不影响发布,记入下一版)

- 选词面板部分图标/文字仍呈系统青绿色(`#008577`):上游图标素材本身带色,
  需逐张替换为可着色矢量图;文字色的判断还需按实际主题覆盖再验一次。
- 卡 2/4 的封面目前用朱砂渐变 + 书名(无封面时的设计预期),真实封面接入排在 1.3.1。

## 证据文件

| 文件 | 内容 |
|---|---|
| `device-card-template*.png` | 设备真实导出的卡片(1080×1440) |
| `01-card-preview-dialog.png` / `02-card-preview-next.png` | 预览对话框与模板切换 |
| `../../store-assets/share-card-promo.png` | 商店用卡片宣传图 |
