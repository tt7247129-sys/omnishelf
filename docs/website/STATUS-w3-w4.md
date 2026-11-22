# 网站 W3 / W4 状态(2026-11-22)

## 结论:W3、W4 计划内任务已完成并上线

线上:**https://omnishelf.cc**

## W3 交付

### 1. 站内搜索 `/search/`
- 构建期生成 **67 条索引**(29+1 本书 / 21 篇文章 / 16 篇工程笔记)
- 纯前端过滤,**不发送任何输入**;支持 `?q=melville` 分享链接,地址栏随输入更新
- 已验收:扫码/打开带 q 的链接可直接筛出结果

### 2. 知识库补齐:新增 9 篇,总数 21 篇
| 栏目 | 新增 |
|---|---|
| Reading Craft(6) | 四遍读法、SQ3R 实操、能活下来的批注、手机读长篇的六项设置 |
| Where to Read(10) | 公版是什么意思(合规科普)、图书馆借 EPUB(Libby/OverDrive) |
| Reading Life(5) | 会结束的书架、听书不是读书、同时读两本 |

每篇都有:结构化数据 `Article`、相关阅读内链、作者注、文末产品软转化。

### 3. 第一本「我们能托管」的书(零版权风险)
- 书:`OmniShelf: a reader's guide`(项目自有版权,CC BY 4.0)
- 文件:站点自己生成并托管 **`/books/omnishelf-guide.epub`**(6.3KB,合法 EPUB,13 个条目)
- 生成脚本:`web/tools/make-guide-epub.py`(可重复生成,不依赖第三方)
- 意义:验证了"自建 + 托管"的完整链路,同时**不碰任何他人版权文件**

**关于中文公版书的判断**:Wikisource 的导出服务有反爬(Anubis),Standard Ebooks 的
直链也有防护;从 Wikisource 抓文本自制 EPub 在技术上可行,但**质量与合规需要逐本校对**。
为避免上线半成品,本轮没有批量生成中文公版书——这一步排在下一轮,按"每本人工校对"推进。

## W4 交付

### 1. App 端安装归因(Install Referrer)
- 新增 `InstallAttribution`(`app/src/main/java/com/foobnix/model/InstallAttribution.java`)
- 首次启动读取 Play 的 `referrer`,把 `utm_*` 字符串存进 `AppState`(installReferrer / installReferrerChecked)
- **不联网、不上报、不采集广告 ID**;非 Play 安装(侧载/模拟器)直接跳过,不崩溃
- 依赖:`com.android.installreferrer:installreferrer:2.2` + keep 规则
- 版本:**1.3.1**(versionCode 5),CI 编译通过

### 2. 上线清单
见 `LAUNCH-CHECKLIST.md`:站点侧已全绿;剩余项需要你操作 GSC/Bing 与分析 ID。

## 线上验收(2026-11-22 16:26)

```
/search/                                200  24 KB (67 条索引)
/books/omnishelf-guide/                 200  EPUB 自托管页
/books/omnishelf-guide.epub             200  6,328 bytes (EPUB, mimetype 正确)
/reading-craft/four-passes-through-a-book/  200
/where-to-read/public-domain-in-plain-words/ 200
/reading-life/two-books-at-once/        200
sitemap: 86 条 URL
```

## 版权与合规自查(每轮必做)

- [x] 免费书只收公版,文件由出版方托管;本站不复制他人文件
- [x] 自有书(guide)为我们撰写,标注 CC BY 4.0
- [x] 付费区只有店铺链接,无文件;明确声明暂无联盟合作
- [x] `/dmca/` 下架通道 + 24 小时响应承诺
- [x] 站点无第三方广告、无 Cookie 追踪脚本(分析为空配置)

## 下一轮建议

1. 中文公版书流水线(逐本校对,先做 4 本:论语、道德经、孙子兵法、唐诗选)
2. 书库从 30 本扩到 100 本(Gutenberg 恢复后批量核验)
3. GSC 收录优化:提交 sitemap、观察书页收录
4. Play 上架后:`site.ts` 的 `play.published` 改 true,全站切商店直链并启用 referrer
