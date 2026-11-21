# OmniShelf 1.3.0 · 发布资料

## 版本号

| 项 | 值 |
|---|---|
| versionName | **1.3.0** |
| versionCode | **4**(AAB 基线;arm64 APK 因 ABI 偏移显示为 5) |
| 数据库版本 | 8(未改) |
| 上架类型 | 正式版(替换 1.1.0) |

## 版本说明 · 简体中文(直接粘贴)

```
1.3.0 更新内容

【新增:书摘分享卡】
· 阅读时长按选中文字,在选词面板点「分享书摘卡」
· 6 套卡片版式,可左右滑动预览切换:日期卡、封面票根、竖排书脊、影像封面、朱砂画框、画廊极简
· 每张卡都有朱砂印章、纸张肌理、票根式页脚,分享出去像印刷品
· 卡片自动带上书名、作者、日期,以及「一个不给你看广告的阅读器」的介绍
· 二维码指向官网 omnishelf.cc,App 上架后自动切换为下载链接
· 记住你上次选的版式

【新增:首次启动的来源调查】
· 第一次打开会问一句「你从哪里知道 OmniShelf」
· 答案只存在你的手机里,不会上传;如果你愿意,可以一键发给我

【视觉统一】
· 修正部分面板、勾选框仍是系统青色的问题,界面颜色现在统一为朱砂

【关于我】
App 是我利用业余时间独立开发的,难免会有 bug,请大家多多包涵、多多反馈,
我会尽快优化迭代。如果觉得还行,也请大家多多好评——这是我继续开发唯一的动力。

反馈邮箱:hello@omnishelf.cc
```

## 版本说明 · English

```
What's new in 1.3.0

Share quote cards
- Long-press a word, then tap "Share quote card" in the selection panel
- Six layouts, swipe left and right to preview: date card, cover ticket, vertical spine,
  cover band, cinnabar frame, gallery
- Every card carries a cinnabar seal, paper grain and a perforated ticket stub
- The card adds the book, the author, the date and a one-line introduction
- The QR code points at omnishelf.cc and will point at the store once the app is public
- The layout you picked last is remembered

Where did you find us
- Asked once at first launch, stored on your device, sent to the developer only if you
  choose to

Visual fixes
- Panels and checkboxes no longer fall back to the system teal; the interface is cinnabar

About this app
Built by one person in his spare time. Bugs will happen - please report them, and leave a
rating if you find it useful. hello@omnishelf.cc
```

## 发布前检查(我在 CI 与设备上验证)

- [x] 6 套模板渲染器编译通过(android.jar + ZXing)
- [x] 55 语言卡片文案:介绍词、扫码引导、二维码地址(743 键 × 56 文件零差异)
- [x] 宋体子集 2.8MB 内置(OFL 授权,含许可证文件)
- [x] 二维码运行时生成(ZXing core 3.5.3),地址取自字符串资源
- [x] 选词面板入口、预览切换、分享链路
- [ ] CI 签名构建产出 AAB/APK(待本轮)
- [ ] 真机逐模板验证(待本轮)
