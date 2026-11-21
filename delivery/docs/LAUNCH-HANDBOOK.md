# OmniShelf 发布手册

> 一份给"要上架的人"的总览。工程侧全部完成并在模拟器验证;标记 ⏸ 的步骤需要真人/真机/账号。

## 一、工程现状(全部在 ui/redesign 分支,已推送)

> 最终验收(2026-11-14):全新安装 release → 冒烟 11/11 → 无崩溃。
> 交付物:`out/OmniShelf-ui-check.apk`(35MB 验证包)、`out/OmniShelf-app-bundle.aab`(31MB 上架包)。

| 项 | 状态 |
|---|---|
| 改名 OmniShelf、包名 cc.omnishelf.reader、图标、品牌色 | ✅ |
| 品牌残留(文件夹名/启动屏/评分链接/推广卡) | ✅ 清理 |
| 12 个新语言包(共 54 语言),CI 行数检查通过 | ✅ |
| 空态三件套(Recent/Library/Bookmarks)+ 搜索区分文案 | ✅ |
| 继续阅读卡片(进度条+角标)、置顶+图钉、按天分组 | ✅ |
| 划词面板折叠化、字号步进器、无障碍 109→0 | ✅ |
| APK 体积 49→35MB(R8 + 资源收缩 + -Os 原生库) | ✅ |
| 冒烟 11 项 × monkey 1800 事件,零崩溃 | ✅ |
| Play 声明答案 | ✅ `docs/PLAY-COMPLIANCE.md` |
| 商店截图草稿 | ✅ `store-assets/`(真机重拍) |
| 内容站 | ✅ 27 页,AdSense 手册复审通过 |

## 二、本地构建(随时可复现)

```bash
JAVA_HOME=/tmp/jdk21b/jdk-21.0.13+9/Contents/Home \
GRADLE_USER_HOME=/tmp/gradle-home ANDROID_USER_HOME=/tmp/android-home \
./gradlew :app:assembleProRelease -x lint --no-configuration-cache
# 产物:app/build/outputs/apk/pro/release/OmniShelf Pro-1.0.0-mupdf-arm64.apk (35MB)

# Play 上架用 AAB(按设备分发,31MB):
#   ./gradlew :app:bundleProRelease   -> app/build/outputs/bundle/proRelease/app-pro-release.aab
# AAB 已验证:package cc.omnishelf.reader, versionCode 1 (1.0.0), minSdk 24, targetSdk 36
```

原生库在 `app/src/main/jniLibs/arm64-v8a/`(gitignored,本机已有);丢失时用
`Builder/link_arm64.sh` 重建(-Os,约 50 分钟)。**新克隆的仓库不含原生库,需要此步骤**。

## 三、上架前你(或我协助)要做的

1. **正式 keystore**:生成并妥善保存;当前本地 release 用 debug 密钥,**不可上架**。
   重签:`apksigner sign --ks <你的.keystore> --out signed.apk unsigned.apk`
2. **真机验收**(装 `out/OmniShelf-ui-check.apk`):
   - TTS 出声与息屏续播
   - 中文 PDF 是否豆腐块
   - 夜间模式空态可读性
   - 重拍 8 张商店截图(英文界面)
   - 录 2 分钟"所有文件访问"演示视频(模拟器编码器不可用)
3. **封闭测试**:12 名测试员 × 连续 14 天;发链接,提醒"不要卸载"(卸载重置个人计时)
4. **域名/证书**:omnishelf.cc 的 DNS(Cloudflare 先 DNS-only)→ 证书 → 再开代理
5. **Play Console 填写**:按 `docs/PLAY-COMPLIANCE.md` 的答案逐项照抄
6. **GitHub 仓库**:撤销用过的临时 token(`ghp_hXmq…` 已在会话中用于推送),换成
   fine-grained token 或 SSH

## 四、发布后

- 内容站每周 2–4 篇(勿同日批量、勿倒填日期)
- Search Console 提交 sitemap;AdSense 通过后再挂广告代码
- 版本迭代:versionCode 每次 +1,同一 keystore 签名

## 五、已知限制(有意为之)

- 无跨设备同步、无账号(零收集定位)
- 阅读面板打开时正文最后一行可能被遮挡(动重排风险高,未做)
- 480dp 极窄屏底栏标签截断
