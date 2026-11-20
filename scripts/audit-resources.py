#!/usr/bin/env python3
"""Static audit of resource references across app/src.

Uses the R.txt from the last successful build as the oracle. It answers one question:
does every @type/name and every R.type.name in the sources point at a symbol that exists?
A missing symbol is a compile error at best and a null view at runtime at worst.

False positives to avoid:
  - android.R.layout... is a framework resource, not ours
  - @android:... is a framework resource
  - @+id/... defines rather than references
"""
import re
import sys
import pathlib
import xml.etree.ElementTree as ET
from collections import defaultdict

SRC = pathlib.Path("app/src")
RTXT = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "app/build/intermediates/compile_symbol_list/proRelease/generateProReleaseRFile/R.txt")
# Symbols added after the last successful build; they will exist once Gradle runs again.
ALLOWED = {("string", "share_quote_card"), ("id", "onShareQuoteCard")}

if not RTXT.exists():
    print(f"没有找到资源符号表: {RTXT}")
    sys.exit(2)

symbols = defaultdict(set)
for line in RTXT.read_text(errors="ignore").splitlines():
    parts = line.split()
    if len(parts) >= 3:
        symbols[parts[1]].add(parts[2])

def java_and_xml():
    for p in sorted(SRC.rglob("*.xml")):
        yield p, "xml"
    for p in sorted(SRC.rglob("*.java")):
        yield p, "java"

# A reference is only ours when it is written as @type/name (not @android:type/name)
# or as R.type.name with no package prefix in front of the R.
XML_REF = re.compile(r'(?<![\w.])@([a-z]+)/([A-Za-z0-9_]+)')
XML_ID_DEF = re.compile(r'@\+id/([A-Za-z0-9_]+)')
JAVA_REF = re.compile(r'(?<![\w.])(?:com\.foobnix\.[A-Za-z0-9_.]*\.)?R\.([a-z]+)\.([A-Za-z0-9_]+)\b')

problems = []
warnings = []
parsed = 0
bad_xml = []
declared_ids = set()

for path, kind in java_and_xml():
    if kind == "xml":
        try:
            ET.parse(path)
            parsed += 1
        except Exception as exc:
            bad_xml.append(f"{path}: {str(exc)[:70]}")
    text = path.read_text(errors="ignore")
    if kind == "xml":
        declared_ids.update(XML_ID_DEF.findall(text))
        refs = [(m.group(1), m.group(2)) for m in XML_REF.finditer(text)]
    else:
        refs = [(m.group(1), m.group(2)) for m in JAVA_REF.finditer(text)]
    for typ, name in refs:
        if typ not in symbols:
            continue
        if name in symbols[typ] or (typ, name) in ALLOWED:
            continue
        problems.append(f"{path}: {kind} @{typ}/{name}")

for name in sorted(declared_ids):
    if name not in symbols["id"] and ("id", name) not in ALLOWED:
        warnings.append(f"@+id/{name} 不在 R.txt 中(可能是新增 id,待构建后生成)")

unique = sorted(set(problems))
print("=" * 60)
print(f"扫描: {parsed} 个 XML, {len(list(SRC.rglob('*.java')))} 个 Java 文件")
print(f"资源符号基准: {sum(len(v) for v in symbols.values())} 个")
print("=" * 60)
print(f"\n真实问题: {len(unique)} 条")
for line in unique[:80]:
    print("  " + line)
print(f"\n待构建生成的新 id: {len(warnings)} 条")
for line in warnings[:20]:
    print("  " + line)
print(f"\nXML 解析失败: {len(bad_xml)}")
for line in bad_xml[:10]:
    print("  " + line)
sys.exit(1 if unique else 0)
