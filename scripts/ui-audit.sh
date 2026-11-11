#!/usr/bin/env bash
# ui-audit.sh — checks that can be made from the source alone.
#
# Everything here answers a question a screenshot cannot answer reliably: is there a
# stray colour left, does every control have a name for a screen reader, is a control
# hidden in XML while the code still expects to show it.

cd "$(dirname "$0")/.." || exit 2
RES="app/src/main/res"
JAVA="app/src/main/java"
fails=0

check() { # name, then the command whose output is the problem list
  local name="$1"; shift
  local out; out=$("$@" 2>/dev/null)
  if [ -n "$out" ]; then
    echo "  ✗ $name"
    printf '%s\n' "$out" | head -8 | sed 's/^/      /'
    fails=$((fails + 1))
  else
    echo "  ✓ $name"
  fi
}

echo "== colours =="
check "no indigo left anywhere" bash -c \
  "grep -rniE '#(3949ab|3f51b5|5c6bc0)' $RES $JAVA | grep -v '#4A5D4E'"
check "no material teal left" bash -c \
  "grep -rniE '#(009688|00bfa5|26a69a)' $RES $JAVA"

echo "== accessibility =="
check "every ImageView carries a description or is marked decorative" bash -c \
  "python3 - <<'PY'
import pathlib, re
bad = []
for p in pathlib.Path('$RES/layout').glob('*.xml'):
    text = p.read_text()
    for m in re.finditer(r'<ImageView\b(.*?)/>', text, re.S):
        block = m.group(1)
        if 'contentDescription' not in block and 'importantForAccessibility' not in block:
            if 'android:id=' in block:
                line = text[:m.start()].count(chr(10)) + 1
                bad.append(f'{p.name}:{line}')
print(chr(10).join(bad[:12]))
PY"

echo "== hidden but wired =="
check "controls hidden in XML with no visibility call in code" bash -c \
  "python3 - <<'PY'
import pathlib, re
res = pathlib.Path('$RES/layout'); java = pathlib.Path('$JAVA')
code = ''
for p in java.rglob('*.java'):
    code += p.read_text(errors='ignore')
bad = []
for p in res.glob('*.xml'):
    t = p.read_text()
    for m in re.finditer(r'android:id=\"@\\+id/(\\w+)\"(.*?)visibility=\"gone\"', t, re.S):
        name = m.group(1)
        if f'R.id.{name}' not in code:
            bad.append(f'{p.name}: {name}')
print(chr(10).join(bad[:10]))
PY"

echo
if [ "$fails" -eq 0 ]; then echo "audit: clean"; else echo "audit: $fails finding(s)"; fi
exit 0
