# 云端构建(无需本地 Android 环境)

> 现行流水线沿用 master 上验证过的那一份(它会**从源码编译 MuPDF 的 4 个 ABI**,
> 首次约 40 分钟,之后走缓存)。之前的失败原因是用了已下线的
> `android-actions/setup-android`——它去装 `tools` 包时会直接报错,所以构建在 40 秒内就挂了。

本机没有 JDK 17-21、没有 Gradle 缓存、也连不上 Maven 仓库,所以构建搬到了 GitHub Actions。
公开仓库的 Actions 不计费。

## 一、每次推送自动编译

`.github/workflows/build.yml`:

1. 装 JDK 21 与 Android SDK
2. 跑 `:app:assembleProDebug`
3. 把 APK 作为构建产物上传

**怎么看结果**:仓库页面 → **Actions** 标签 → 最新一次 `build`
- 绿色 ✓:Java 全部编译通过
- 红色 ✗:点进去看 `Build debug APK` 那一步的报错

**怎么下载 APK**:该次运行页面底部 → **Artifacts** → `omnishelf-debug-apk`

> **原生库已随仓库提供**:`app/src/main/jniLibs/arm64-v8a/`(23MB,含 `libmupdf.so`)已提交,
> 所以云端打出的 arm64 APK **可以正常打开书**。CI 里有一道检查,APK 里少了
> `libmupdf.so` / `libdjvu.so` / `libmobi.so` 就直接报错,不会悄悄出一个只能看界面的包。
>
> 其他 ABI(arm、x86、x86_64)的原生库不在仓库里,由发布机用
> `Builder/link_arm64.sh` 构建;因此云端产物只有 arm64。

## 二、签名上架包(手动触发)

`.github/workflows/release.yml`,需要先加 4 个仓库 Secret:

| Secret | 内容 |
|---|---|
| `KEYSTORE_BASE64` | 上传密钥库的 base64:`base64 -i omnishelf-upload.jks \| pbcopy` |
| `KEYSTORE_PASSWORD` | 库口令(`KwsbVANaaeMsa5n9wNj37YFB`) |
| `KEY_ALIAS` | `omnishelf` |
| `KEY_PASSWORD` | 密钥口令(同上) |

加完后:Actions → **release** → Run workflow → 填版本号 → 运行。
产物里会有签好名的 `.aab` 与 `.apk`,并自动校验"只有一个证书链"。

**密钥必须与 Play 已注册的一致**(SHA-1 `5B:64:C1:...`),否则上传会被拒。

## 三、常见失败与处理

| 现象 | 原因 | 处理 |
|---|---|---|
| `SDK location not found` | 没装 SDK | setup-android 步骤失败,重跑即可 |
| `compileSdk 37 not found` | 平台未预装 | AGP 会自动下载;若失败,在工作流里加 `sdkmanager "platforms;android-37"` |
| 编译报错指向某个 Java 文件 | 代码问题 | 把报错发我,我改 |
| 签名校验提示多个证书 | 用了调试签名 | 检查 4 个 Secret 是否正确 |

## 四、本地构建(有 Android Studio 时)

```bash
./scripts/restore-build-env.sh
```

脚本会逐项检查 JDK / SDK / 网络 / 缓存,缺什么就说什么,而不是甩栈。
