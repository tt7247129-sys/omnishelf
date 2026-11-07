# 真机验证记录

> 目的:留下"到底测过什么、没测什么"的客观记录,而不是靠印象。
> 每次发布前更新本文件。

## 环境

| 项 | 值 |
|---|---|
| 设备 | Android 模拟器(arm64),非物理手机 |
| 系统 | Android 13 (API 33) |
| 分辨率 | 1080 × 1920 |
| 安装包 | `OmniShelf-arm64.apk`,由上传密钥签名,与 Play 要上传的 AAB 同一次构建 |
| 包名 | `cc.omnishelf.reader` |
| 版本 | 1.0.0 (versionCode 1) |

## 已验证(有截图/日志为证)

| # | 场景 | 结果 | 证据 |
|---|---|---|---|
| 1 | `adb install` | 成功 | 安装输出 Success |
| 2 | 冷启动 | 进入书架,无崩溃 | 截图 `store-assets/screenshots/01-library.png` |
| 3 | 书库扫描 | 识别 4 本(1 EPUB + 3 PDF) | UI 树中的 `Book …` 节点 |
| 4 | EPUB 打开与渲染 | 正常,260 页,可翻页 | 截图 `02-reading-epub.png` |
| 5 | PDF 打开与渲染 | 正常,页码正确 | 截图(内部) |
| 6 | MuPDF 原生库 | 正常加载并渲染 | logcat `MuPdfPage_open` |
| 7 | 阅读设置面板 | 打开正常 | UI 树含 Reading Settings |
| 8 | 高级设置面板 | 打开正常 | UI 树含 ADVANCED SETTINGS |
| 9 | 首次启动权限流程 | 说明弹窗 → 系统设置页 | 截图(内部) |
| 10 | 分享接收 (`ACTION_SEND`) | 进程处理完毕退出,无崩溃 | logcat 无 crash |
| 11 | 数据目录 | 自动创建 `/sdcard/OmniShelf` | `ls /sdcard` |

**11 项全部通过,崩溃日志全程为空。**

## 未验证(明确列出,不要当成已通过)

| 场景 | 为什么没测 |
|---|---|
| 朗读(TTS) | 模拟器无语音引擎,点了只会提示"未安装 TTS" |
| OPDS 在线书库 | 需要可用的书库地址与稳定网络 |
| 书签 / 笔记的持久化 | 需要跨重启验证,本轮未做 |
| 桌面小部件 | 需要启动器交互 |
| 夜间模式效果 | 测试用的 EPUB 自带样式表,覆盖了主题色,无法判定 |
| 分享输出目标 | 分享面板的第三方目标不在本应用内 |
| Android 14 / 15 行为差异 | 只跑了一个系统版本 |
| 真实物理设备 | 模拟器与真机在存储、权限、厂商定制上仍有差异 |

## 已知与上游一致的行为(不是缺陷)

- 阅读页工具栏会在数秒后自动隐藏,点屏幕中央唤出。
- 打开书籍后底部"Recent"面板可能自动展开,点 × 关闭。
- 大量书籍时首屏扫描需要时间。

## 复核方式

```bash
adb install -r OmniShelf-arm64.apk
adb shell am start -n cc.omnishelf.reader/com.foobnix.ui2.MainTabs2
adb logcat -b crash
```
