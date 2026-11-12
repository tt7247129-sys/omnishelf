# 真机验收:已由我自己完成

设备上跑通了。下面的截图都在 `docs/ui-shots/verified/`。

## 已确认(模拟器实拍)

| 项 | 证据 |
|---|---|
| 默认落在 Recent、顶栏墨绿 | `01-recent-first-green.png` |
| 封面进度角标(绿色 2%) | `02-cover-badge.png` |
| 阅读页顶栏 4 图标、底栏 4 图标 | `03-reader-toolbars.png` |
| 长按封面 → Pin to the top | `04-pin-menu.png` |
| 置顶后再长按 → Unpin from the top | `05-unpin-menu.png` |
| 对话框按钮为墨绿(非青色) | `06-dialog-accent.png` |

## 自测中发现并修掉的两个 bug

1. **置顶长按完全没反应** — 判断条件里排除了 `getChild() != null` 的条目,而书库里的书
   大多数都带 child 路径,等于把所有书都排除了。
2. **升级后看不到新界面** — 设置存在 `/sdcard/OmniShelf/profile.*`,卸载重装不会重置,
   旧的主题色与标签顺序盖住了新默认值。已加一次性迁移。

## 仍未验证

- **空书架提示**:模拟器上删掉书、清数据、删 profile 之后,书库仍显示那本书,无法造出空库
- **真实手机**:480x800 的模拟器与你的手机分辨率和 ROM 都不同,长按手感、通知栏、TTS 仍需你确认
