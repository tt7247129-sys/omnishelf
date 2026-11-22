# 分享书摘卡:入口调整 + 完整性验证(1.3.2)

日期:2026-11-22 · 版本:1.3.2(versionCode 6)· 设备:emulator-5554(1080×1920)

## 一、需求与实现

### 1) 「分享书摘卡」入口放到「+」之后
- `dialog_selected_text.xml`:在 `onAddCustom`(+)**之后、`moreToggle`(⋮)之前**新增
  `onShareCardQuick`(分享图标,contentDescription = 分享书摘卡)。
- 原来藏在折叠区里的同名整行已删除,避免出现两个入口。
- `DragingDialogs.java`:两处入口共用一个 `openQuoteCard` 监听器,点击后直接打开卡片预览。

**设备 UI 树实测(同一行内,从左到右)**:

```
x=120  Enable text selection
x=243  (underline)
x=366  (strike)
x=489  (+)                       ← 加号
x=579  Share quote card          ← 分享书摘卡,紧跟在加号之后
x=687  More (⋮)
```

### 2) 卡片信息完整性:不许遮挡、不许截断
渲染器改为「先给装饰物留位,再让文字自适应」:

| 位置 | 约束 |
|---|---|
| 印章 / 二维码 / 边框 / 书脊色块 | 先登记为禁入矩形 |
| 引文 | 按剩余宽高自动降字号与行距,直到全部放下 |
| 书名 | 可用宽度避开印章;拉丁书名用英文引号(按词换行),CJK 用《》;放不下时改横排 |
| 作者 / 章节 / 日期 | 宽度硬约束 + 自动换行 |
| 左下介绍 | 三段式按高度收缩,永不进入二维码 |
| 选词长度 | **取消 220 字截断**,全文渲染 |

新增运行时自检 `ShareQuoteCardAudit`:
每块文字与每个装饰都记录矩形,越界或与装饰相交时打印 `OUTSIDE` / `OVERLAP`,否则打印块数。

## 二、设备实测

### A. 压力输入(68 字标题、41 字作者、260 字连续长词引文)

自检日志(5 个模板,全部无告警):

```
template=0 textBlocks=4 decorations=2
template=1 textBlocks=4 decorations=2
template=2 textBlocks=5 decorations=2
template=3 textBlocks=5 decorations=2
template=4 textBlocks=4 decorations=2
```

即:没有 OUTSIDE,没有 OVERLAP。压力测试暴露并修掉的三处真实问题:
1. 竖排书脊卡标题超长时溢出卡底 → 改为「放得下才竖排,放不下转横排三行」;
2. 画廊卡印章压在第一行引文上 → 印章移到引文块下方;
3. 拉丁书名被从中断字(`Lon|g`) → 拉丁按词换行,书名按语言选引号。

### B. 中文正常书籍回归(绿色封面 / 读书不是把眼睛放在纸上)

自检日志同样只有 `textBlocks/... decorations=2`,无告警;5 张卡人工核对:
引文、书名、作者、日期、扫码文案、网址、左下三段介绍全部完整,无遮挡。

### C. 分享功能实测

点击卡片预览里的 SHARE 后:

- 系统分享面板正常打开(`com.android.intentresolver/.ChooserActivity`);
- 面板标题 **Sharing image**;
- 分享文本 = 选中全文(260 字符,无省略号);
- 可用目标:Quick Share、Messages、Gmail、Photos、Drive。

## 三、证据文件

| 文件 | 内容 |
|---|---|
| `panel-order.txt` | 顶部一行控件的 UI 树顺序(证明分享卡紧跟「+」) |
| `cards-stress-text.png` | 压力输入下的 5 张卡 |
| `cards-chinese.png` | 中文书籍的 5 张卡 |
| `dialog-preview.png` | 预览对话框实拍 |

## 四、版本与签名

- 版本:1.3.2(versionCode 6)
- 产物:`delivery/OmniShelf-1.3.2.aab`(33MB)、`delivery/OmniShelf-1.3.2-arm64-signed.apk`(50MB)
- 证书:SHA-1 `5B:64:C1:A4:30:C2:B2:57:16:8F:98:30:C3:C9:FB:67:96:BE:BA:6C`(Play 上传密钥)
- CI:build 成功;设备安装后 versionName=1.3.2

## 五、已知边界(如实说明)

- 自检覆盖的是「块级文字 + 印章 + 二维码」;逐字竖排标题、逐字字距标签属于固定位置绘制,
  由适配逻辑保证,不在矩形自检范围内(压力测试已用肉眼与日志双重确认)。
- 极端输入(例如整本书一次全选,上万字)会把字号压到最小可读尺寸;此时仍不截断,
  但排版已接近「密排」,建议在分享前把选择范围控制在自然段落。
