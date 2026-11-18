# 短视频系统(按 Max 框架:批量录屏 + 多平台分发)

## 一、已经产出的素材(本轮完成)

`delivery/marketing/video/out/` 共 **18 条**,来自 6 个真实操作流程:

| 片段 | 内容 | 竖屏 EN | 竖屏 中文 | 横屏 EN |
|---|---|---|---|---|
| clip-open-book | 书架点开一本书 | ✓ | ✓ | ✓ |
| clip-turn-pages | 翻页 | ✓ | ✓ | ✓ |
| clip-selection | 划词选中 | ✓ | ✓ | ✓ |
| clip-night | 切夜间模式 | ✓ | ✓ | ✓ |
| clip-tts | 打开听书面板 | ✓ | ✓ | ✓ |
| clip-cbz | 打开 CBZ 漫画 | ✓ | ✓ | ✓ |

规格:1080×1920(9:16)与 1920×1080(16:9),12 秒,带朱砂标题带与品牌水印。

## 二、批量生产方法(每次 2 小时产出 15-30 条)

1. 手机开「录屏」(或 adb screenrecord),按脚本演一遍:每个动作只录 8-12 秒
2. 一次录 10-15 个动作(打开书/翻页/划词/夜间/听书/搜索/排序/置顶/文件夹/漫画)
3. 用同一个标题带模板套文案(参考 `video/captions/` 下的 PNG)
4. 同一段素材换标题 = 一条新视频(钩子不同,算法视为新内容)

**不要**:露脸、配音、花哨转场。真实录屏 + 一句钩子,就够。

## 三、钩子库(从竞品爆款结构拆解)

来自对 Reddit 高赞帖、TikTok 教育类爆款、小红书 75 个标题公式的拆解
([Reddit 应用推广](https://www.mediafa.st/reddit-marketing-for/app-downloads)、
[3 秒钩子研究](https://www.airbridge.io/en/resources/handbook/ugc/04-hooks-and-soft-cta))。

**核心结论:前 3 秒决定一切;卖得最好的视频常常不说 App 名字。**

### 结果先行型(outcome-first)
- "This is what 34 formats look like on one screen."
- "I read 3 books a week. This is the only app on my phone."
- "No ads. No account. No analytics. Just books."

### 反常识型(contrarian)
- "Your reader app is collecting more data than your browser."
- "The best ebook reader is the one that asks you nothing."
- "Free apps are not the problem. Tracking is."

### 反向演示型(demo-in-reverse)
- "Watch what happens when I tap a 400-page PDF on a 3-year-old phone."
- "Open a comic file that most apps refuse."

### 手感型(tactile reveal)
- "Long-press a word. This is what comes up."
- "Tap once. Night mode."

### 中文(小红书 / B站 竖屏)
- 「用了 3 年付费阅读器,直到我发现这个开源的」
- 「别再被广告逼着跳转了——本地书用这个读」
- 「34 种格式一个 App:PDF、EPUB、漫画、扫描书」
- 「离线朗读:通勤路上闭眼也能读」
- 「无广告、无账号、无追踪:我为什么自己做了一个阅读器」

## 四、平台矩阵(同一素材,不同剪法)

| 平台 | 规格 | 时长 | 标题风格 | 备注 |
|---|---|---|---|---|
| TikTok / Shorts / Reels | 9:16 | 8-15s | 结果先行 + 话题标签 | 前 3 秒必须有画面动作 |
| Instagram Reels | 9:16 | 10-20s | 视觉优先 | 播放往往低于 FB,别以它评估成败 |
| Facebook | 9:16 或 1:1 | 10-20s | 文字钩子 + 链接 | **同一条视频在 FB 常比 IG 高 10 倍** |
| X / Twitter | 16:9 | 10-20s | 一句话 + 无广告主张 | 技术人群转化高 |
| YouTube Shorts | 9:16 | 15-30s | 教程式 | 可复用为长视频切片 |
| B站 | 16:9 或竖屏 | 30-60s | 对比 + 实测 | 中文技术人群 |
| 小红书 | 3:4 图文 或 9:16 | 图文/视频 | 数字+实测、身份+踩坑 | 标题公式见下 |
| 微信视频号 | 9:16 | 15-30s | 情感 + 实用 | 与公众号文章互链 |

**同一素材分发 6-8 个平台,是零预算下最大的杠杆。**

## 五、小红书标题公式(直接套用)

| 类型 | 模板 | 我们的用法 |
|---|---|---|
| 数字+实测 | 「我实测了 27 个 XX,免费能打的只有 4 个」 | 「我试了 12 个阅读器,只剩这一个没广告」 |
| 身份+踩坑 | 「做独立开发 2 年,我终于搞懂了 XX」 | 「业余时间做了个阅读器,踩了 5 个坑」 |
| 对比+反差 | 「用了 3 年 XX,直到我发现 Y 才真香」 | 「用了 3 年付费阅读器,开源版反而更干净」 |
| 疑问+拆解 | 「为什么 80% 的人选错了 XX?」 | 「为什么免费阅读器都要看广告?」 |
| 清单+速成 | 「5 步搞定 XX」 | 「3 步把手机变成阅读器(附下载)」 |

## 六、发布节奏(每周 2-3 条,不刷屏)

| 周 | 内容 | 平台 |
|---|---|---|
| W1 | clip-open-book + clip-night | TikTok / Reels / 小红书 |
| W2 | clip-tts + clip-cbz | Shorts / B站 / FB |
| W3 | clip-selection + clip-turn-pages | X / TikTok / 小红书 |
| W4 | 汇总:一条 30 秒合集 | 全平台 |

> 数据纪律:同一素材在不同平台的表现差异可达 10 倍以上,**不要因为一个平台没量就否定素材**。

## 七、回复与转化

- 视频描述区放 `omnishelf.cc/?ref=<平台>`,统计能看出哪个平台真的带来访问
- 评论里有人问「什么 App」→ 再回答,不主动刷名字(算法更喜欢自然互动)
- 每条视频固定回复:感谢 + 引导到 GitHub/Play(一句,不重复)
