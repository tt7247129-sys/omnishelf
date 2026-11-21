# OmniShelf 1.3.0 验证报告 · 2026-11-21

## 结论摘要

| 项目 | 结果 |
|---|---|
| 签名包与 AAB | Play 上传密钥 `5B:64:C1:…:6C`,单一证书链,versionName **1.3.0**(versionCode 4) |
| 设备验证(逐项) | 见下,全部通过 |
| 5 套卡片真实渲染 | 已从设备导出 1080×1440 PNG 五张,逐张核对(见 `device-five-sheet.png`) |
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

## 本版改动(2026-11-22)

- **删除封面票根卡**:按用户要求整卡移除,模板由 6 套变为 **5 套**;
  选词面板→预览对话框的计数器同步为 `1/5 … 5/5`。
- **日期全部动态**:日、月名、年、星期、完整日期、书脊竖排日期均由
  `ShareQuoteCard.fillDates()` 从设备时钟与 Locale 生成(设备当时显示 22 / Sunday)。
- **二维码标签组按样张实现**:同字号(16,超宽自动降到 11)、同字距(0.10 / 0.06)、
  同一轴线居中、上下间距 18 / 10;逐字绘制,与样张同一套算法。
- **左下介绍按样张三段式实现**:起首线 34×4、品牌 19px 字距 0.14、主张 23px、
  规格 14px 字距 0.05;整块从二维码上沿 -34 起排,超长自动改为底对齐,不溢出。

### 与参考样张的实测对比(票根区)

| 元素 | 参考(px) | 设备(px) | 偏差 |
|---|---|---|---|
| 起首线 | y 1210–1214,x 84–118 | y 1210–1213,x 84–117 | ≤1 |
| 品牌行 | y 1235–1247,x 85–191 | y 1232–1245,x 85–192 | ≤3 |
| 主张行 | y 1262–1281,x 85–619 | y 1261–1279,x 85–700 | y ≤1,x +81 |
| 规格行 | y 1298–1311,x 85–187 | y 1299–1311,x 86–177 | ≤2 |
| 二维码 | 112px 码区填满 | 112px 码区填满(本次修复) | 0 |

> 二维码在上一轮实测只有 **75px 码区**:ZXing 默认留 4 模块静区,样张没有静区。
> 已改为「找到码区→整数倍放大→填满 112px」,卡片本身的纯色地面充当静区。

y 与 x 起点的偏差都在 3px 内;唯一明显差异是**主张行的行宽**(英文 +81px),
原因是字体按角色替换:样张用 Songti 的拉丁字形,设备用内置 Noto Serif SC 的拉丁字形,
后者更宽。中文排版时两者等宽(CJK 均为 1em),不会出现这个差异。

## 与定稿的差异(设计决定,非缺陷)

- 卡 4(朱砂框)顶部日期行:中文环境为「摘录 · 二〇二六年十一月」,英文环境只显示「November 2026」
  (没有对应英文的「摘录」文案,避免出现中英混排)。
- 字体按角色替换:定稿为 Songti + Helvetica,设备为内置 Noto Serif SC + 系统无衬线;
  字号、字距、坐标与定稿逐一相同。

## 证据文件

| 文件 | 内容 |
|---|---|
| `device-card-template*.png` | 设备真实导出的卡片(1080×1440) |
| `01-card-preview-dialog.png` / `02-card-preview-next.png` | 预览对话框与模板切换 |
| `../../store-assets/share-card-promo.png` | 商店用卡片宣传图 |
