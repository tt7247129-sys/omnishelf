# OmniShelf 1.1.0 · Play Console 文案(直接复制粘贴)

> 以下内容分别对应 Google Play Console 的两个输入框:
> **「发布版本名称」** 和 **「版本说明」(Release notes)** —— 复制即可,无需修改。

---

## 一、发布版本名称(Release name)

```
1.1.0 界面重构与新配色
```

如果 Play Console 只允许纯版本号,就用:

```
1.1.0
```

---

## 二、版本说明 · 简体中文(zh-CN)

```
1.1.0 更新内容

【全新界面结构】
· 每个页面只保留一条顶栏:标题、搜索、刷新、排序、视图切换一目了然
· 设置统一收进右上角齿轮,不再有两套导航重复
· 底部标签顺序调整为:最近 / 书库 / 文件夹 / 收藏 / 书签,打开应用直接回到最近在读

【配色与可读性】
· 全新朱砂配色,顶栏图标对比度从几乎看不清提升到 8.3:1
· 支持白天浅色、夜间深色两套配色,夜间自动切换深红顶栏
· 中文界面全面适配:书架空态、继续阅读、书签提示等文案均已本地化
· 新增 12 种语言支持,目前共支持 54 种语言,自动跟随系统语言

【修复】
· 修复点击书籍封面无法打开的问题(书名、封面、卡片任意位置都可以点开)
· 修复升级后界面退回旧样式的问题
· 修复若干图标不可见、按钮点不动的问题

【关于我】
App 是我利用业余时间独立开发的,难免会有 bug,请大家多多包涵、多多反馈,
我会尽快优化迭代。如果觉得还行,也请大家多多好评——这是我继续开发的唯一动力。

反馈邮箱:hello@omnishelf.cc
```

---

## 三、版本说明 · English(en-US)

```
What's new in 1.1.0

New layout
- One top bar per screen: title, search, refresh, sort and view in a single row
- Settings live behind a gear icon; the duplicate navigation is gone
- Tab order is now Recent / Library / Folder / Favorites / Bookmarks

Colour and readability
- New cinnabar palette; header icons went from nearly invisible to 8.3:1 contrast
- Light theme for the day, dark red header for the night theme
- Chinese UI fully localised, including empty states and hints
- 12 more languages added - 54 in total, following the system language

Fixes
- Tapping a book cover now opens the book (cover, title or card: any tap works)
- The redesign no longer reverts to the old look after an update
- Several invisible icons and dead buttons fixed

About this app
This app is built by one person in his spare time. Bugs will happen - please be patient
and tell me about them, and I will fix them as quickly as I can. If you find it useful,
a good rating means a lot: it is the only thing that keeps this project going.

Feedback: hello@omnishelf.cc
```

---

## 四、Play Console 里这两项在哪

| 项 | 位置 |
|---|---|
| 发布版本名称 | 发布 → 正式版(或封闭测试)→ 创建新版本 → 右上「发布版本名称」 |
| 版本说明 | 同一页面下方「此版本中的新内容」文本框(可点「添加翻译」分别填各语言) |

> 提示:版本说明有 500 字符上限/语言。上面中文约 420 字、英文约 700 字符,
> 英文若超限,可删掉「About this app」段的第二句。

---

## 五、本次发布的技术说明(给你自己看)

- **versionCode = 2**,**versionName = 1.1.0**
- AAB 现在只有 **1 个证书链**:证书 `CN=OmniShelf, OU=Reader, O=OmniShelf, L=Shenzhen, C=CN`,
  SHA-256 `382a4750a8ce27f4223f0e784022c2ffd810c6458479477a7e95de4dcf98aef1`
- 之前的报错根因:Gradle 先用调试证书签了 AAB,我又用 jarsigner 追加了一次正式签名,
  于是包里存在两条证书链。现在改由 Gradle 直接用正式 keystore 签名,只签一次。
