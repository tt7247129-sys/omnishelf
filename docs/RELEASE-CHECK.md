# 上架前的包体预检

> 用 Google 官方工具 bundletool 对**即将上传到 Play 的那个 AAB** 做的检查。
> 检查日期:2026-11-08

## 结论

**通过。这个包可以被 Google Play 接收。**

## 用到的工具

`bundletool-all-1.17.2.jar`(Google 官方发布),JDK 21。

## 清单字段

| 字段 | 值 | 判断 |
|---|---|---|
| package | `cc.omnishelf.reader` | 与仓库一致 |
| versionCode | `1` | 首次上传,合法 |
| versionName | `1.0.0` | — |
| minSdkVersion | `24` | Android 7.0 起 |
| targetSdkVersion | `36` | 满足 Play 当年要求 |
| compileSdkVersion | `37` | — |

## 签名

```
jar 已验证
签名者:CN=OmniShelf, OU=Release, O=OmniShelf Project, L=Unknown, ST=Unknown, C=CN
```

签名者是本项目生成的上传密钥,与 `keys/omnishelf-upload.jks` 一致。

## 包体指纹

```
SHA-256  9af4f870e364007698e27b6764227741c96380b09b5f1bafca4903ac02124474
大小     71 MB
```

> 这个哈希对应提交 `1fd96c01` 构建出的产物。若将来重新构建,哈希会变,
> 但 versionCode 必须递增,否则 Play 会拒绝。

## 复现方式

```bash
curl -L -o OmniShelf.aab \
  https://github.com/tt7247129-sys/omnishelf/releases/download/latest/OmniShelf.aab

java -jar bundletool.jar validate --bundle OmniShelf.aab
java -jar bundletool.jar dump manifest --bundle OmniShelf.aab
jarsigner -verify OmniShelf.aab
```

## 下次发版必须记住

`versionCode` 在当前实现里来自 `app/gradle.properties` 的 `appCodeNumber`。
**每次发布前把它加 4**(上游约定:每个 ABI 占一个号),否则 Play 会以
"版本号已存在"拒绝上传。
