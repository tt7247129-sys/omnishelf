#!/usr/bin/env bash
# Tells Bing (and the other IndexNow engines) that the sitemap's URLs changed.
#
#   ./scripts/submit-indexnow.sh <indexnow-key>
#
# The key is the one Bing Webmaster gives you. The script publishes it as
# web/public/<key>.txt (IndexNow requires that file to be reachable), so deploy once after
# the first run, then run again to submit the URLs.
set -euo pipefail
cd "$(dirname "$0")/.."

KEY="${1:-}"
if [ -z "$KEY" ]; then
  echo "usage: $0 <indexnow-key>   (from Bing Webmaster Tools)" >&2
  exit 1
fi

HOST="omnishelf.cc"
mkdir -p web/public
printf '%s' "$KEY" > "web/public/${KEY}.txt"

# Every URL the sitemap lists, read from the built output when present.
if [ -f web/dist/sitemap.xml ]; then
  URLS=$(grep -oE '<loc>[^<]+</loc>' web/dist/sitemap.xml | sed 's/<[^>]*>//g')
else
  URLS=$(curl -sL "https://${HOST}/sitemap.xml" | grep -oE '<loc>[^<]+</loc>' | sed 's/<[^>]*>//g')
fi
COUNT=$(printf '%s\n' "$URLS" | wc -l | tr -d ' ')
echo "submitting ${COUNT} URLs for ${HOST}"

# IndexNow accepts at most 10,000 URLs per request; this site is far below that.
JSON=$(printf '%s\n' "$URLS" | /Users/huangpeng/.dsh/dsh-runtimes/dsh-primary-runtime/dependencies/python/bin/python3 -c '
import json, sys
urls = [line.strip() for line in sys.stdin if line.strip()]
print(json.dumps({"host": "omnishelf.cc", "key": sys.argv[1],
                  "keyLocation": f"https://omnishelf.cc/{sys.argv[1]}.txt",
                  "urlList": urls}))
' "$KEY")

curl -sS -X POST "https://api.indexnow.org/indexnow" \
  -H "Content-Type: application/json; charset=utf-8" \
  --data "$JSON" -w "\nHTTP %{http_code}\n"
echo "note: web/public/${KEY}.txt must be deployed before the engines can read it"
