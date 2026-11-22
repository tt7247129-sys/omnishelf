# 网站 W5 状态(2026-11-23)

## 结论:书库到 100 本(含 7 本中文公版),SEO/GSC 就绪

线上:**https://omnishelf.cc**

## 1. 中文公版书流水线(新)

`web/tools/make-wikisource-epub.py`:从 Wikisource 抓原文 → 清洗排版 → 生成我们自己的 EPUB。

| 书 | 作者 | 章节 | 字数 |
|---|---|---|---|
| 論語 | 孔子弟子 | 22 | 56,203 |
| 孫子兵法 | 孫武 | 14 | 10,311 |
| 增廣賢文 | 佚名 | 1 | 11,023 |
| 弟子規 | 李毓秀 | 5 | 1,304 |
| 千字文 | 周興嗣 | 1 | 1,717 |
| 百家姓 | 佚名 | 1 | 776 |
| 朱子家訓 | 朱用純 | 1 | 767 |

- 文件托管在站点自身:`/books/zh-<slug>.epub`(EPUB 3,含目次与「來源與授權」章)
- **合规口径**:原文为公有领域(作者均为古代);维基文库转录以 **CC BY-SA 4.0** 使用,
  在书页与 EPUB 末尾均署名;EPUB 由我们排版,同样以 CC BY-SA 4.0 提供
- 脚本带合规 UA 与重试退避,可重复运行

## 2. 书库:30 → 100 本

- 新增 63 本英文公版(Standard Ebooks,逐本核验页面可达,下载指向出版方)
- 新增 7 本中文公版(自托管 EPUB)
- 现状:**100 本**(English 93 / 中文 7),免费区按题材分组,
  新增题材:Chinese classics、Primer、Proverbs、Strategy、Mystery、Children、Poetry、
  Philosophy、Adventure、Gothic、Romance、History、Essays 等

## 3. SEO / GSC(新增)

- 首页:`WebSite` + `SearchAction`(站内搜索可直接被搜索引擎调用)+ `SoftwareApplication`
- 书页:`Book` + `BreadcrumbList`
- 文章页:`Article` + `BreadcrumbList`
- `robots.txt`:放行全站、屏蔽 `/go/`
- sitemap:**156 条 URL**(100 书页 + 21 文章 + 16 工程笔记 + 其余静态页)
- 验证位:构建期注入 `PUBLIC_GOOGLE_SITE_VERIFICATION` / `PUBLIC_BING_SITE_VERIFICATION`
  (GitHub Secret),未配置时不输出任何标签
- IndexNow:`scripts/submit-indexnow.sh <key>` 一键向 Bing 推送全部 URL 并发布密钥文件
- 提交手册:`docs/website/GSC-SUBMISSION.md`

## 4. 线上验收(待部署后执行)

```
/books/free/          100 张卡片
/books/zh-lunyu/      中文书页
/books/zh-lunyu.epub  EPUB 下载(81KB)
/sitemap.xml          156 条
/robots.txt           Disallow: /go/
```

## 5. 仍需你(两项)

1. **GSC / Bing 验证**:建好资源后把验证 content 值发我,我配置 Secret 并部署,你点验证。
2. **IndexNow 密钥(可选)**:Bing 里生成后发我,我跑脚本推送。
