# OmniShelf 1.2.0 · 发布文案草稿(待编译验证后正式发布)

## 一、发布版本名称

```
1.2.0 书摘卡片与来源调查
```

## 二、版本说明 · 简体中文(直接复制)

```
1.2.0 更新内容

【新增:书摘分享卡】
· 阅读时长按划词,在选词面板里点「分享书摘卡」
· 自动生成 1080×1440 精美卡片:引文、书名、作者、品牌行
· 夜间模式下自动用深色卡片,白天用朱砂卡片
· 可直接分享到微信、微博、小红书或任意应用,无需账号

【新增:首次启动的来源调查】
· 第一次打开会问一句「你从哪里知道 OmniShelf」
· 答案只保存在你手机里,不会上传
· 如果你愿意,可以一键把答案发给我——这只是为了让我知道哪个渠道有用

【改进】
· 修复书库作者名与设置勾选框颜色不统一的问题

【关于我】
App 是我利用业余时间独立开发的,难免会有 bug,请大家多多包涵、多多反馈,
我会尽快优化迭代。如果觉得还行,也请大家多多好评——这是我继续开发唯一的动力。

反馈邮箱:hello@omnishelf.cc
```

## 三、版本说明 · English

```
What's new in 1.2.0

Quote cards
- Long-press a word, then choose "Share quote card" in the selection panel
- Renders a 1080x1440 card with the quote, book, author and a small brand line
- Night theme produces a dark card; the day theme uses the cinnabar card
- Shares to any app, no account needed

Where did you find us
- Asked once at first launch, eight channels
- The answer stays on your device; sending it to the developer is optional

Fixes
- Author names and checkboxes in settings now follow the accent colour

About this app
Built by one person in his spare time. Bugs will happen - please report them and
leave a rating if you find it useful. hello@omnishelf.cc
```

## 四、发布前必须完成

- [ ] 恢复编译环境(见 `scripts/restore-build-env.sh`),构建通过
- [ ] 真机/模拟器验证:首次启动弹出来源调查且只弹一次
- [ ] 验证:选词面板出现「分享书摘卡」,点击后系统分享面板出现图片
- [ ] 验证:折叠状态下该行随其它行一起隐藏
- [ ] 出包签名(用 Play 已注册的上传密钥)
