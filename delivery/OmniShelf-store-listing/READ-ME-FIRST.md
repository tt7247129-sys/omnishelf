# OmniShelf 商店资料包(1.1.0)

## 目录说明

| 文件 | 内容 |
|---|---|
| `text/01-STORE-LISTING-FINAL.md` | **商店文案最终定稿**:应用名称、简短说明、完整说明(中英双语)、类别、标签、联系邮箱、隐私政策网址、数据安全表单答案、图形素材清单 |
| `text/02-RELEASE-NAME-AND-NOTES.md` | Play Console 的「发布版本名称」和「版本说明」,中英双语 |
| `text/03-PLAY-FORM-ANSWERS.md` | Play 各表单(数据安全、内容分级等)的逐项答案 |
| `assets/icon-512.png` | 应用图标 512×512 |
| `assets/feature-graphic-1024x500.png` | 特征图片 1024×500 |
| `assets/screenshot-01..08.png` | 8 张手机截图 1080×1920 |

## 按这个顺序填

1. **商店设置 → 主要商店商品详情**
   - 应用名称 ← 文案第 1 节
   - 简短说明 ← 文案第 2 节
   - 完整说明 ← 文案第 3 节
   - 图形:icon + feature graphic + 8 张截图
2. **商店设置 → 隐私权政策** → `https://omnishelf.cc/privacy/`
3. **商店设置 → 应用类别** → 图书与工具书
4. **内容分级问卷** → 全部选「否」
5. **数据安全表单** → 按 `03-PLAY-FORM-ANSWERS.md`
6. **发布 → 正式版** → 上传 `OmniShelf-1.1.0-release-signed.aab`
7. **发布版本名称 / 版本说明** → 复制 `02-RELEASE-NAME-AND-NOTES.md`

## 已核实的事实(文案里所有数字都有出处)

- 34 种文件格式(`org/ebookdroid/BookType.java`)
- 54 种语言,自动跟随系统(`values-*` 共 54 个语言目录)
- DOCX 需 Android 8+(`AppsConfig.isDOCXSupported = SDK_INT >= 26`)
- **无云同步**——该功能在本构建中关闭,文案里不得宣传同步
- 无广告、无内购、无追踪(无 ads/analytics 依赖)

## 备注

截图目前为模拟器英文界面,后续可换真机截图;不影响本次提交。
