# 上架操作手册(个人开发者账号版)

> 你用的是**个人**开发者账号。Google 的规则是:必须先跑一次封闭测试,
> **至少 12 名测试者连续 14 天保持加入状态**,才会解锁「申请正式发布」按钮。
>
> 这 14 天是日历时间,绕不过去。但除了这 14 天,其余每一步都已经准备好了。

---

## 现在已经完成的事(不需要你管)

| 项目 | 状态 |
|---|---|
| 代码仓库 | https://github.com/tt7247129-sys/omnishelf |
| 构建流水线 | 绿色通过,能自动出包 |
| 已签名安装包 | `build-output/OmniShelf-1.0.0.aab` |
| 测试用 APK | `build-output/OmniShelf-1.0.0-mupdf-arm64.apk` |
| 隐私政策页 | 已部署,见第 2 步 |
| 签名密钥 | `keys/`,**已备份到 U 盘或密码管理器了吗?** |

---

## 第 1 步:让域名生效(Cloudflare,约 5 分钟)

`omnishelf.cc` 现在解析到 Cloudflare,但还没指向站点。

1. 登录 Cloudflare → 选中 `omnishelf.cc` → **DNS → Records**
2. 删除已有的 `A` / `CNAME` 记录(指向别处的那些,如果有)
3. 添加两条:

| 类型 | 名称 | 内容 | 代理状态 |
|---|---|---|---|
| CNAME | `@` | `tt7247129-sys.github.io` | **DNS only(灰色云)** |
| CNAME | `www` | `tt7247129-sys.github.io` | **DNS only(灰色云)** |

> ⚠️ 必须是**灰色云**。开着橙色云代理,GitHub 签不出 HTTPS 证书,页面会一直报错。

4. 等 5–10 分钟,浏览器打开 **https://omnishelf.cc/privacy/** 应该能看到中英文隐私政策。

> 打不开就等一会儿再试。这一步没完成,**Play 提交会卡在隐私政策校验**。

---

## 第 2 步:把应用装到手机,截图(约 10 分钟)

1. 把 `OmniShelf-1.0.0-mupdf-arm64.apk` 发到你的安卓手机(微信文件传输/数据线都行)
2. 点击安装,允许「安装未知来源应用」
3. 打开应用,依次做这几件事,**顺便就是功能验收**:

| 检查项 | 怎么做 | 正常表现 |
|---|---|---|
| 能启动 | 点图标 | 进入书架页,不闪退 |
| 能导入书 | 左上角菜单 → 扫描本地书籍 | 列出手机里的电子书 |
| 能打开 PDF | 随便点一本 PDF | 正常显示页面 |
| 能打开 EPUB | 点一本 EPUB | 正常显示,可翻页 |
| 能朗读 | 阅读页菜单 → 朗读 | 有声音 |
| 能加书签 | 阅读页 → 书签 | 保存成功 |
| 无广告 | 全程观察 | 没有任何广告条/弹窗 |
| 无内购 | 设置里翻一遍 | 没有任何购买入口 |

4. 在**书架页**和**阅读页**各截一张图,传到电脑

> 截图要求:最短边 ≥ 320px、长宽比不超过 2:1、PNG 或 JPEG。
> 手机直接截的图都满足。

**任何一项不正常,截图发我,我来改。**

---

## 第 3 步:在 Play Console 创建应用

1. 打开 https://play.google.com/console
2. **Create app**,按下表填:

| 字段 | 填什么 |
|---|---|
| App name | `OmniShelf: PDF EPUB Reader` |
| Default language | English (United States) |
| App or game | App |
| Free or paid | **Free** |
| Declarations | 两个勾都打上 |

---

## 第 4 步:填「App content」全部问卷

左侧 **Policy → App content**,一共 9 项。**逐项答案在 `store/play-console-answers.md`**,照抄即可。

最关键的三项:

| 项目 | 答案 |
|---|---|
| **Ads** | **No**(本应用不含广告) |
| **Data safety** | **不收集任何数据**(全部不勾) |
| **Target audience** | **13 岁及以上** |

> `MANAGE_EXTERNAL_STORAGE` 的声明草稿在第 7.1 节,直接粘贴英文那段。
> **这是最可能被打回的一项**,材料要一次写足。

---

## 第 5 步:填商店信息并上传安装包

左侧 **Grow → Store presence → Main store listing**:

- **Short description**:从 `store/listing.md` 复制英文那行(72 字符)
- **Full description**:复制完整英文说明
- **App icon**:上传 `store-assets/icon-512.png`
- **Feature graphic**:上传 `store-assets/feature-graphic-1024x500.png`
- **Phone screenshots**:上传第 2 步截的两张图

然后 **Release → Testing → Closed testing**:

1. **Create track**
2. **Create new release**
3. 上传 `build-output/OmniShelf-1.0.0.aab`
4. Release notes 从 `store/listing.md` 复制
5. **Start rollout**

---

## 第 6 步:凑 12 个测试者,开始 14 天

### 添加测试者

1. 封闭测试轨道 → **Testers** 标签
2. 建一个 **Email list**,把这 12 个人的 **Gmail 地址**填进去
   > 必须是他们手机上登录 Play 商店用的那个 Google 账号,不是随便什么邮箱
3. 保存,然后把 **opt-in 链接**发给这 12 个人

### 发给测试者的话(直接复制)

> 我在发布一个免费的开源电子书阅读器,Google 要求 12 个人先试用两周。
> 具体操作:点开下面这个链接 → 点「成为测试者」→ 去 Play 商店安装 → 放在手机上两周别卸载。
> 不用写评测,不用付费,装上就行。谢谢你!
>
> (opt-in 链接)

### 三个必须提醒他们的点

1. **必须点开 opt-in 链接**,只在商店里搜是搜不到的
2. 用**手机上登录 Play 商店的那个 Google 账号**,换成别的账号会看不到
3. **14 天内不要退出测试、不要卸载**,中途退出会重新计时

### 时间线

| 阶段 | 时长 |
|---|---|
| 封闭测试轨道审核通过 | 3–7 天 |
| 12 人满 14 天 | 14 天 |
| 申请正式发布 + 审核 | 7–10 天 |

**从今天算,最快约 4 周后上线。** 期间应用可以正常被这 12 个人使用,你可以继续收集问题我来修。

---

## 第 7 步:申请正式发布

14 天满、12 人都在,Play Console 首页会出现 **Apply for production** 按钮:

1. 点它,填问卷(问你这 14 天收到什么反馈、改了什么)
2. 提交后等 7 天左右
3. 通过后 → **Production → Create new release** → 用同一个 AAB → 开始发布

---

## 出问题时的对照表

| 现象 | 原因 | 处理 |
|---|---|---|
| 隐私政策链接打不开 | DNS 没生效 / 开了橙色云 | 改灰色云,等 10 分钟 |
| 上传 AAB 报错 | 版本号重复 | 告诉我,我改版本号重新构建 |
| 被拒:MANAGE_EXTERNAL_STORAGE | 声明不够具体 | 用 `play-console-answers.md` 7.1 的全文重新申诉 |
| 被拒:数据安全声明不符 | 勾选与实现不一致 | 把拒信原文发我 |
| 测试者说搜不到应用 | 没点 opt-in 链接 / 账号不对 | 让他确认账号并重新点链接 |
| 应用闪退 | 功能缺陷 | 把机型和操作步骤发我,我修 |

---

## 你只需要记住三件事

1. **`keys/` 里的密钥要备份。** 丢了就永远无法更新这个应用。
2. **代码仓库必须保持公开。** 这是 AGPL 的硬性要求。
3. **包名 `cc.omnishelf.reader` 永久不可改。**
