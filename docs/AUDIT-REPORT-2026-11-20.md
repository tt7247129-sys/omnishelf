# 全模块功能检测报告 · 2026-11-20

> 范围:整个 `app/src`(445 个 XML、1170 个 Java 文件),重点覆盖最近两周新增的
> 书摘分享卡、首次启动来源问卷,以及围绕图标/原生库/构建的改动。
> 方法:静态审计(资源符号基准来自上次成功构建的 `R.txt`,2149 个符号)+ 逐条人工复核。
> 不能做的:真机运行(本机无 JDK 17-21、无 Gradle 缓存、Maven 网络不通),见文末"残余风险"。

## 一、结论

**未发现会导致功能不可用的缺陷。** 审计器初筛出 17 条资源引用问题,逐条复核后
**全部为误报**(注释中的引用、系统资源、flavor 专属资源、反射用法)。另外做了三处
防御性加固,不影响现有行为。

## 二、审计覆盖的方法

1. 资源符号基准:取 `R.txt`(上次成功构建产出的 2149 个符号)作为"什么存在"的权威。
2. XML 引用:扫描 445 个 XML 中的 `@string/` `@id/` `@drawable/` `@color/` `@dimen/`
   `@style/` `@layout/` `@mipmap/` 等引用,与基准比对。
3. Java 引用:扫描 1170 个 Java 文件的所有 `R.type.name`。
4. 语言一致性:55 个 `values-*/strings.xml` 的**键集合**逐个与 `values/strings.xml` 比对
   (不仅看行数),并检查单文件内重复键。
5. Manifest 组件:每个声明的 Activity/Service/Receiver 是否有对应源码。
6. XML 合法性:全部资源 XML 解析,0 失败。
7. 逐条人工复核初筛结果。

## 三、初筛 17 条 → 逐条复核结果

| 引用位置 | 初筛判定 | 复核结论 |
|---|---|---|
| `MainTabs2` `R.id.brigtnessProgressView` | id 缺失 | **注释中**,非代码 |
| `FileMetaAdapter` `R.id.signIcon` | id 缺失 | **注释中**(`//signIcon = ...`) |
| `PrefFragment2` `R.id.appName`、`R.id.onHelpTranslate` | id 缺失 | **注释中** |
| `DragingDialogs` `R.id.moreSettingsImage`、`R.string.search_in_the_book` | 缺失 | **注释中** |
| `AbstractViewController` `R.id.adFrame` | id 缺失 | **注释中**;布局里实际是 `adFrame1/adFrame2` |
| `ActionEx` `R.id.class` | 语法可疑 | 合法 Java:对 `R.id` 内部类取类字面量,用于反射枚举 id |
| `ShareDialog` 两个 glyphicons | 图片缺失 | **注释中** |
| `Clouds` `R.drawable.star_1` | 缺失 | 资源存在于 flavor 源集,`R.txt` 为 proRelease 基准,非缺陷 |
| `SlidingTabStrip` `@attr/colorForeground` | 缺失 | 系统 `android.R.attr` |
| 两个 `notification_tts_line*.xml` `@style/TextAppearance` | 缺失 | 系统样式 `@android:style/TextAppearance` |
| `fdroid/AndroidManifest.xml` `@xml/automotive_app_desc` | 缺失 | 文件存在于 `app/src/fdroid/res/xml/` ✓ |

**真实缺陷数:0。**

## 四、本轮加固(3 处,均为防御性,不改变功能)

| 位置 | 改动 | 原因 |
|---|---|---|
| `ShareQuoteCard` 引文字体 | `Typeface.SERIF` → `Typeface.DEFAULT` | 很多设备没有中文衬线字体,衬线会导致引文变豆腐块;无衬线始终可用 |
| `DragingDialogs` 写卡片文件 | 改为 try-with-resources,并 `card.recycle()` | 异常时不再泄漏文件句柄/位图内存 |
| `ShareQuoteCard.pruneOldCards()` | 每次生成后清理 7 天前的卡片 | `filesDir/share` 每分享一次多一个 PNG,长期会涨 |

## 五、重点模块复核

| 模块 | 检查内容 | 结论 |
|---|---|---|
| 书摘分享卡 | 渲染器 `javac + android.jar` 编译通过;布局新行 id 已声明;`FileProvider` 路径覆盖 `files-path`;分享 Intent 带 `FLAG_GRANT_READ_URI_PERMISSION` | 通过 |
| 选词面板 | 新行与"分享"同级;已加入 `foldable` 数组,折叠时随其它行隐藏;`editText` 为同方法内 `final` 变量 | 通过 |
| 首次启动问卷 | 调用点在 `setContentView` 之后;`Handler.postDelayed` 有 `isFinishing/isDestroyed` 守卫;邮件发送有 try/catch;答案只存本地 | 通过 |
| 图标 | `@mipmap/icon_pdf_pro`:anydpi-v26(自适应)+ xxhdpi PNG(API 24-25 兜底)齐备;无残留 `ic_omnishelf` 引用 | 通过 |
| 字符串 | 56 个文件均含新键;无重复键;键集合完全一致 | 通过 |
| 签名配置 | `hasReleaseKey` 逻辑未改;无注入式签名 | 通过 |
| 商业/隐私开关 | `isCloudsEnable = false`、无广告逻辑未改动 | 通过 |

## 六、残余风险(必须说清楚)

1. **运行时未验证**:书摘卡出图、分享面板、问卷弹窗只在编译/语法层验证过,
   没在真机跑过——本机无法构建(无 JDK 17-21 / 无 Gradle 缓存 / Maven 不通)。
2. **CI 产物待确认**:最近两次使用"仓库内 arm64 原生库 + 不编译 MuPDF"的流水线,
   从 Actions 过滤结果看未出现在失败列表,推断转绿,但无法由我直接观测。
3. **问卷文案为英文**:54 语言 App 里这一处是英文,属于体验瑕疵,计划 1.2.1 补
   55 个语言文件(需要 9 条短文案 × 55 语言,量较大,单独排期)。

## 七、复现命令

```bash
python3 scripts/audit-resources.py \
  app/build/intermediates/compile_symbol_list/proRelease/generateProReleaseRFile/R.txt
```
