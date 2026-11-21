#!/usr/bin/env bash
# Packs everything the next release needs into one archive:
#   delivery/OmniShelf-<version>-release-pack.zip
#
#   ./scripts/package-release.sh [version]
set -uo pipefail
cd "$(dirname "$0")/.."
VERSION="${1:-$(grep '^appVersionNumberBase' app/gradle.properties | cut -d= -f2).$(grep '^appVersionNumberIndex' app/gradle.properties | cut -d= -f2)}"
OUT="delivery/OmniShelf-${VERSION}-release-pack"
rm -rf "$OUT"; mkdir -p "$OUT/store" "$OUT/app" "$OUT/docs" "$OUT/share-cards"

cp delivery/OmniShelf-${VERSION}.aab "$OUT/app/" 2>/dev/null
cp delivery/OmniShelf-${VERSION}-arm64-signed.apk "$OUT/app/" 2>/dev/null
cp delivery/PLAY-RELEASE-NOTES-1.3.0.md "$OUT/docs/" 2>/dev/null
cp delivery/store-listing/STORE-LISTING-FINAL.md "$OUT/store/" 2>/dev/null
cp delivery/store-listing/ASO-12-MARKETS.md "$OUT/store/" 2>/dev/null
for lang in zh-CN en-US; do
  mkdir -p "$OUT/store/screenshots-$lang"
  cp delivery/store-listing/screenshots/$lang/*.png "$OUT/store/screenshots-$lang/" 2>/dev/null
done
cp store-assets/icon-512.png "$OUT/store/" 2>/dev/null
cp store-assets/feature-graphic-1024x500.png "$OUT/store/" 2>/dev/null
cp store-assets/share-card-promo.png "$OUT/store/" 2>/dev/null
cp docs/share-card/v3/card*.png docs/share-card/v3/all-six.png "$OUT/share-cards/" 2>/dev/null
cp docs/share-card/v3/DESIGN-SPEC-V3.md "$OUT/share-cards/" 2>/dev/null
mkdir -p "$OUT/docs/verification"
cp docs/verify-2026-11-21/* "$OUT/docs/verification/" 2>/dev/null
cp docs/PLAY-* "$OUT/docs/" 2>/dev/null
cp README.md "$OUT/docs/" 2>/dev/null
cat > "$OUT/READ-ME-FIRST.md" <<TXT
# OmniShelf ${VERSION} · 发布包

## 上架用
- app/OmniShelf-${VERSION}.aab —— 用 Play Console 上传(已用上传密钥签名,单一证书链)
- store/ —— 商店文案(中文、英文、12 市场 ASO)、截图(中英)、图标、特征图、卡片宣传图
- docs/PLAY-RELEASE-NOTES-1.3.0.md —— 版本说明(中文/英文,可直接粘贴)

## 测试用
- app/OmniShelf-${VERSION}-arm64-signed.apk —— 可直接安装的手机包(同密钥签名)

## 设计素材
- share-cards/ —— 6 套分享卡设计样张(中英)+ 设计规范

## 验证记录
- docs/verification/REPORT.md —— 设备验证 8/8 通过 + 本轮修复的缺陷

## 版本
versionName ${VERSION};versionCode 4(AAB)。
TXT
zip -qr "delivery/OmniShelf-${VERSION}-release-pack.zip" "$OUT"
echo "packed: delivery/OmniShelf-${VERSION}-release-pack.zip"
du -sh "delivery/OmniShelf-${VERSION}-release-pack.zip"
