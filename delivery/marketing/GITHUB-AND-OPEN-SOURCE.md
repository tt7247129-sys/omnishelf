# 开源渠道执行清单(GitHub / IzzyOnDroid / F-Droid / Awesome lists)

## 一、GitHub 仓库设置(逐项勾)

| 项 | 填什么 | 状态 |
|---|---|---|
| Repository description | `Free, no-ads EPUB/PDF reader for Android — 34 formats, offline TTS, 54 languages. AGPLv3.` | 待填 |
| Website | `https://omnishelf.cc` | 待填 |
| Topics(见下) | 20 个以内 | 待填 |
| README 首屏 | 已重写:徽章 + 三张截图 + 下载链接 | ✅ 已完成 |
| Releases | 每个版本附 `OmniShelf-<版本>-arm64-release-signed.apk` + SHA256 | 待上传 |
| Social preview | 用 `store-assets/feature-graphic-1024x500.png` | 待设 |

### Topics(复制这些,逗号分隔)

```
android, epub, pdf, ebook, ebook-reader, pdf-reader, epub-reader, offline,
no-ads, no-tracking, privacy, open-source, tts, text-to-speech, opds, cbz,
djvu, mupdf, material-design, agpl
```

> 设置地址:`https://github.com/tt7247129-sys/omnishelf/settings` → Topics
> 也可用 API:
> ```
> curl -X PUT -H "Authorization: token $GITHUB_TOKEN" \
>   -H "Accept: application/vnd.github+json" \
>   https://api.github.com/repos/tt7247129-sys/omnishelf/topics \
>   -d '{"names":["android","epub","pdf","ebook","ebook-reader","offline","no-ads","no-tracking","privacy","open-source","tts","opds","cbz","djvu","mupdf","agpl"]}'
> ```

## 二、IzzyOnDroid(最快,1-3 天)

1. 打开 `https://gitlab.com/IzzyOnDroid/repo/-/issues` → New issue
2. 模板选 "App inclusion"
3. 填:RW = `https://github.com/tt7247129-sys/omnishelf`,
   APK 支持 "Fastlane" 或直接指定 release APK,**不需要自己构建**
4. 需要提供:应用名、包名 `cc.omnishelf.reader`、许可证 AGPL-3.0、无广告/无追踪确认

> 好处:隐私用户群 + 被 F-Droid 用户发现 + 一条高权重外链。

## 三、F-Droid(慢,但长期)

门槛(见 [收录政策](https://f-droid.org/zh_Hans/docs/Inclusion_Policy/)):
- 完全 FLOSS,**包括所有依赖与构建工具**
- 必须能从源码构建(RfD = Reproducible for Debian 检查)

我们的情况:
- 代码 AGPLv3 ✅,MuPDF 也是 AGPL ✅
- 但 MuPDF 需要用 NDK 从源码编译(体积大、耗时长)
- 仓库里已有 `fdroid` flavor,需要提供 `metadata/cc.omnishelf.reader.yml` 与构建脚本

**执行顺序**:先走 IzzyOnDroid;F-Droid 作为第二阶段,只做"提交 metadata + 构建配方",让别人也能复现。

## 四、Awesome lists(PR 模板)

目标仓库与位置:
| 仓库 | 加到哪一节 |
|---|---|
| `awesome-android` | Reading / Ebook readers |
| `awesome-privacy` | Android → Media/Reading |
| `Awesome-Ebook-Readers` | Android readers |
| `awesome-opds` | Clients |

PR 描述模板:
```
Add OmniShelf — a free, no-ads, no-tracking ebook reader for Android.

- 34 formats: PDF, EPUB, MOBI, AZW3, FB2, DJVU, CBZ, CBR, DOCX...
- Offline text-to-speech with the system voice
- 54 interface languages
- AGPLv3, no advertising/analytics/telemetry code
- https://github.com/tt7247129-sys/omnishelf
```

## 五、AlternativeTo

1. `https://alternativeto.net/manage/` → Add application
2. 名称 `OmniShelf`,平台 Android,许可证 Open Source
3. "Listed as alternative to": Moon+ Reader, Librera Reader, KOReader, ReadEra
4. 描述用 ASO 文档的英文简短说明

## 六、Obtainium / 直装用户

README 里已写 Releases 链接,并保留"每次发版附 APK + SHA256"的规则 ——
Obtainium 用户会通过 GitHub Releases 自动更新。

## 七、顺序建议

1. **今天**:GitHub description + topics + social preview(10 分钟)
2. **本周**:IzzyOnDroid 提交 + AlternativeTo 建条目
3. **发布后**:Awesome lists PR(4 个仓库)+ F-Droid metadata
