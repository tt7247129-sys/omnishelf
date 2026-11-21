# Play 上架:照着点就行

> 应用已经建好了(app id 结尾 `…00287`)。
> 下面每一步都给了**可直接复制的文字**,不需要你判断任何技术问题。
> 每完成一步,可以截图发我确认。

---

## 第 1 步:应用内容(9 项)

左侧菜单 → **政策 → 应用内容**。逐项点进去,按下表填。

### 1.1 隐私政策 ← 你现在这一页

点输入框,粘贴:

```
https://tt7247129-sys.github.io/omnishelf/privacy/
```

点**保存**。

### 1.2 应用访问权限

选:**所有功能均无需特殊访问权限**(All functionality is available without special access)

### 1.3 广告

选:**否,我的应用不含广告**

### 1.4 内容分级

开始问卷,所有问题一律选「**否**」(无暴力、无性内容、无脏话、无赌博、无毒品、无恐怖、无用户生成内容、无社交、无位置共享)。

提交后结果应为 **Everyone / 3+**。

### 1.5 目标受众

选:**13 岁及以上**。**不要**勾选 13 岁以下。

### 1.6 数据安全 ← 最容易填错的一项

- 「您的应用是否会收集或共享任何必需的用户数据类型?」→ **否**
- 后面所有数据类型(位置、个人信息、财务、健康、消息、照片、文件、通讯录、应用活动、网络浏览、应用信息与性能、设备 ID)→ **全部不勾**

> 应用会读写手机上的电子书文件,但那是**本地访问**,数据没有离开设备,不构成"收集"。

提交。

### 1.7 政府应用 → **否**

### 1.8 金融功能 → **否**

### 1.9 健康应用 → **否**

---

## 第 2 步:上传安装包

1. 下载(会存到「下载」文件夹):
```
https://github.com/tt7247129-sys/omnishelf/releases/download/latest/OmniShelf.aab
```

2. 左侧 **发布 → 测试 → 封闭测试** → **创建轨道** → **创建新版本**
3. 找到 **App Bundle** 上传区域,**从访达把 `OmniShelf.aab` 拖进浏览器窗口**
4. 上传成功会显示:版本号 `1`、包名 `cc.omnishelf.reader`
5. 版本说明粘贴:

```
<en-US>
First release.
• Free and open source: no ads, no in-app purchases, no tracking
• 30+ formats including PDF, EPUB, MOBI, CBZ and DOCX
• Offline text-to-speech, highlights, notes and bookmarks
</en-US>
```

6. **保存 → 审核发布**

---

## 第 3 步:商店信息

左侧 **扩大用户群 → 展示 → 主要商店商品详情**

### 3.1 简短说明(复制)

```
30+ formats: PDF, EPUB, MOBI, CBZ, DOCX. TTS, offline. No ads, no tracking.
```

### 3.2 完整说明

从 `reader-rebrand/store/listing.md` 里复制英文「完整说明」那一段(很长,直接全选复制)。

### 3.3 图片

| 位置 | 文件 |
|---|---|
| 应用图标 512×512 | `omnishelf/store-assets/icon-512.png` |
| 特征图片 1024×500 | `omnishelf/store-assets/feature-graphic-1024x500.png` |
| 手机截图 | `omnishelf/store-assets/screenshots/01-library.png` |
| 手机截图 | `omnishelf/store-assets/screenshots/02-reading-epub.png` |

---

## 第 4 步:拉 12 个人

封闭测试轨道 → **测试者** 标签 → 新建 **电子邮件列表** → 填 12 个 **Gmail 地址**(必须是他们手机 Play 商店登录的账号)。

把 **opt-in 链接**发给他们,附这段话:

> 我在发布一个免费的开源电子书阅读器,Google 要求先找 12 个人试用两周。
> 点开链接 → 点「成为测试者」→ 去 Play 商店安装 → 放手机上两周别卸载。
> 不用写评测、不用付费,装上就行,谢谢你!

**三个必须提醒的**:必须点 opt-in 链接(直接搜是搜不到的)、必须用手机上那个 Google 账号、14 天内不能退出或卸载。

---

## 第 5 步:等 14 天,然后申请正式发布

满 14 天且 12 人都在,Play 首页会出现 **Apply for production**。

点它,问卷问「这 14 天收到什么反馈、改了什么」——写两三条真实反馈即可(比如"测试者反馈书架扫描较慢")。审核约 7 天。

---

## 卡住了怎么办

把那一屏**截图**发我。我看图告诉你点哪里、填什么。

---

## 三件绝对不要做

1. 不要改包名 `cc.omnishelf.reader`
2. 不要上传 APK(只能用 AAB)
3. 不要日后加广告或内购
