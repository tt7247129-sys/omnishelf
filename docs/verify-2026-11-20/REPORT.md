# 真机(模拟器)验证记录 · 2026-11-20

## 环境
- 设备:`sdk_gphone64_arm64`(Android 37 镜像,arm64)
- 模拟器重启为 4GB RAM / 4 核(原 2GB / 1 核,启动即 System UI ANR)

## 执行

```bash
./scripts/verify-1.2.0.sh delivery/OmniShelf-1.1.0-arm64-release-signed.apk
```

## 结果

```
PASS  install
      installed version: 1.1.0
SKIP  first-run source question (build 1.1.0 predates 1.2.0)
PASS  book opened (com.foobnix.pdf.search.activity.HorizontalViewActivity)
SKIP  share-quote-card row (build 1.1.0 predates 1.2.0)

== 2 passed, 2 skipped, 0 failed ==
```

## 说明

- **安装、启动、打开书**在设备上真实通过,活动名为 `HorizontalViewActivity`,
  即 EPUB 已进入阅读界面而不是停在书库。
- 两个 SKIP 是脚本的版本感知逻辑:被测包是 1.1.0,本来就没有 1.2.0 的
  来源问卷与书摘卡,不算缺陷。拿到 1.2.0 的包后同一条命令会执行这两项断言。
- 证据文件:本目录 `reader-on-emulator.png`(阅读界面)、`final.png`(脚本收尾截图)。

## 复跑

```bash
# 模拟器已就绪时
./scripts/verify-1.2.0.sh <1.2.0 的 apk 路径>

# 或者指定设备
ADB=/path/to/adb ./scripts/verify-1.2.0.sh app.apk
```
