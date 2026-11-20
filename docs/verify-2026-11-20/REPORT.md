# 1.2.0 真机验证报告 · 2026-11-20

## 结论

**7 项检查全部通过,0 失败。** 被测包为 CI 构建的 `versionName 1.2.0`(versionCode 4),
来源:`releases/tag/latest`,构建批次 #33。

## 命令

```bash
./scripts/verify-1.2.0.sh delivery/OmniShelf-1.2.0-arm64-debug.apk
```

## 结果

```
PASS  install
      installed version: 1.2.0
      question: text="Where did you hear about OmniShelf?"
PASS  first-run source question shown
PASS  source question shown only once
PASS  book opened (com.foobnix.pdf.search.activity.HorizontalViewActivity)
PASS  share-quote-card row reachable behind the fold
PASS  quote card written (43463 bytes, 1080 1440 )
PASS  share sheet opened

== 7 passed, 0 skipped, 0 failed ==
```

## 每项断言的是什么

| 检查 | 断言方式 |
|---|---|
| install | `adb install` 成功,并从 `dumpsys package` 读到 versionName |
| 来源问卷 | uiautomator 转储中出现 "Where did you hear about OmniShelf?" |
| 只弹一次 | 回答后重启应用,转储中不再出现该标题 |
| 打开书 | `topResumedActivity` 为 `HorizontalViewActivity`(真正进入阅读页) |
| 分享卡入口 | 转储中出现 `cc.omnishelf.reader:id/onShareQuoteCard`(折叠时自动展开) |
| 生成卡片 | 点击前后对比 `files/share` 目录,新文件 ≥10KB 且为 1080×1440 |
| 分享面板 | 点击后出现系统分享界面 |

## 证据文件

| 文件 | 内容 |
|---|---|
| `01-source-question.png` | 首次启动的来源问卷(8 个渠道,品牌红按钮) |
| `generated-share-card.png` | 应用真实生成的分享卡(1080×1440) |
| `reader-on-emulator.png` | 阅读界面 + 选词面板(1.1.0 时期,用于对照) |
| `final.png` | 脚本收尾截图 |

## 本轮验证发现并修复的问题

1. **面板青色 #008577**:`textLink` 等样式用了 `?android:attr/textColorLink`,基础主题
   未设 `colorAccent` → 改为品牌色 `@color/omni_accent`(已包含在 1.2.0 构建中,
   截图里的对话框按钮已是朱砂色)。
2. **升级用户调色板残留**:`enforcePalette` 接受上游调色板 → 收紧为本 fork 三种颜色。
3. **验证脚本自身两个 bug**:版本号门控导致误跳过;`has_text` 前缀匹配导致漏检
   "Thanks. Send this answer…"。均已修复。

## 复跑

```bash
# 模拟器已启动时
./scripts/verify-1.2.0.sh <apk>
# 网络通过 git 的本地代理可用(见 docs/HOW-TO-WATCH-WORK.md)
```
