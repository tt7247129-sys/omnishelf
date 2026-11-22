#!/usr/bin/env python3
"""Builds EPUBs for the Chinese public-domain shelf out of Wikisource text.

The texts themselves are public domain by age (all authors died long before the cut-off
everywhere these are published); the Wikisource transcription is used under CC BY-SA 4.0 and
attributed in a closing chapter. Nothing here touches a work that is still in copyright.

    python3 web/tools/make-wikisource-epub.py

Writes one EPUB per work into web/public/books/zh-<slug>.epub and prints a summary.
"""
import html as html_mod
import json
import pathlib
import re
import subprocess
import time
import urllib.parse
import zipfile

# Wikimedia asks every client to identify itself; without this the API can answer with a
# block page instead of JSON.
UA = "OmniShelfBookTool/1.0 (https://omnishelf.cc; hello@omnishelf.cc)"

OUT = pathlib.Path(__file__).resolve().parent.parent / "public" / "books"
API = "https://zh.wikisource.org/w/api.php"

BOOKS = [
    {"slug": "qianziwen", "title": "千字文", "author": "周興嗣", "page": "千字文",
     "summary": "A thousand characters, no two repeated: the primer that taught Chinese children to read for fifteen centuries."},
    {"slug": "baijiaxing", "title": "百家姓", "author": "佚名", "page": "百家姓",
     "summary": "The Song-dynasty list of surnames that turned family names into a poem every child could chant."},
    {"slug": "dizigui", "title": "弟子規", "author": "李毓秀", "page": "弟子規",
     "summary": "A Qing primer of household manners, in three-character lines, still quoted at Chinese kitchen tables."},
    {"slug": "sunzibingfa", "title": "孫子兵法", "author": "孫武", "page": "孫子兵法",
     "summary": "Thirteen chapters on winning a war before fighting it, written in the fifth century BC."},
    {"slug": "zengguangxianwen", "title": "增廣賢文", "author": "佚名", "page": "增廣賢文",
     "summary": "A Ming-Qing collection of proverbs on money, friendship and patience, arranged as rhyming couplets."},
    {"slug": "zhuzijiaxun", "title": "朱子家訓", "author": "朱用純", "page": "朱子家訓",
     "summary": "The Zhu family rules: five hundred characters on thrift, order and keeping a household in the seventeenth century."},
    {"slug": "lunyu", "title": "論語", "author": "孔子弟子", "page": "論語", "subpages": True,
     "summary": "The Analects in twenty books: what Confucius said, recorded by the students who followed him."},
]

CHAPTER_TAIL = "來源與授權"


def api(params: dict) -> dict:
    query = urllib.parse.urlencode({**params, "format": "json", "formatversion": 2})
    url = f"{API}?{query}"
    last = ""
    for attempt in range(5):
        raw = subprocess.run(["curl", "-sL", "-m", "45", "-A", UA, url],
                             capture_output=True, text=True).stdout
        try:
            return json.loads(raw)
        except json.JSONDecodeError:
            last = raw[:200]
            time.sleep(2 + attempt * 3)
    raise RuntimeError(f"API did not answer with JSON: {last!r}")


def clean(html: str) -> str:
    html = re.sub(r"(?is)<(script|style).*?</\1>", "", html)
    html = re.sub(r"(?is)<sup[^>]*class=\"[^\"]*reference[^\"]*\".*?</sup>", "", html)
    html = re.sub(r"(?is)<table.*?</table>", "", html)
    html = re.sub(r"(?is)<div[^>]*id=\"toc\".*?</div>", "", html)
    html = re.sub(
        r"(?is)<div[^>]*class=\"[^\"]*(navbox|sistersitebox|ws-noexport|mw-editsection|"
        r"noprint|metadata|ws-header|header_notes)[^\"]*\".*?</div>", "", html)
    html = re.sub(r"(?i)</(p|div|h[1-6]|li|tr|blockquote|dd|dt)>", "\n", html)
    html = re.sub(r"(?i)<br\s*/?>", "\n", html)
    text = re.sub(r"<[^>]+>", "", html)
    text = html_mod.unescape(text)
    boilerplate = ("姊妹计划", "参阅维基百科", "參閱維基百科", "维基百科條目", "維基百科條目",
                   "版本信息", "版本資訊", "目錄", "目次", "編輯", "编辑", "数据项", "數據項",
                   "公有領域", "此作品", "本作品", "本页面", "本頁面", "姊妹项目", "姊妹計劃")
    lines = []
    for line in text.splitlines():
        line = re.sub(r"[ \t\u3000]+", " ", line).strip()
        if not line:
            continue
        if any(marker in line for marker in boilerplate):
            continue
        if len(line) <= 2 and not re.search(r"[\u4e00-\u9fff]", line):
            continue
        lines.append(line)
    return "\n".join(lines)


def fetch_page(title: str, section: int = None) -> str:
    params = {"action": "parse", "page": title, "prop": "text"}
    if section is not None:
        params = {"action": "parse", "page": title, "prop": "text", "section": section}
    data = api({**params, "redirects": "1"})
    return data.get("parse", {}).get("text", "")


def chapters_for(book: dict):
    """Returns [(chapter title, plain text)] for one work."""
    sections = api({"action": "parse", "page": book["page"], "prop": "sections"})
    section_list = sections.get("parse", {}).get("sections") or []
    if book.get("subpages"):
        index_html = fetch_page(book["page"])
        prefix = "/wiki/" + urllib.parse.quote(book["page"].replace(" ", "_")) + "/"
        seen, titles = set(), []
        for href in re.findall(r'href="(/wiki/[^"#?]+)"', index_html):
            if not href.startswith(prefix):
                continue
            raw = urllib.parse.unquote(href[len("/wiki/"):]).replace("_", " ")
            if raw in seen:
                continue
            seen.add(raw)
            titles.append(raw)
        out = []
        for sub in titles:
            body = clean(fetch_page(sub))
            if len(body) > 200:
                out.append((sub.split("/")[-1], body))
        return out
    if len(section_list) > 4:
        out = []
        for section in section_list:
            index = section.get("index")
            # headings can carry inline markup ("汎<span>一作泛</span>"); keep the words only
            line = html_mod.unescape(re.sub(r"<[^>]+>", "", section.get("line") or str(index))).strip()
            body = clean(fetch_page(book["page"], section=index))
            if len(body) > 200:
                out.append((line, body))
        return out
    return [(book["title"], clean(fetch_page(book["page"])))]


def build_epub(book: dict, chapters) -> pathlib.Path:
    OUT.mkdir(parents=True, exist_ok=True)
    target = OUT / f"zh-{book['slug']}.epub"
    total = sum(len(text) for _, text in chapters)
    source_url = "https://zh.wikisource.org/wiki/" + urllib.parse.quote(book["page"].replace(" ", "_"))

    def chapter_xhtml(index: int, title: str, body: str) -> str:
        paragraphs = "\n".join(
            "      <p>" + html_mod.escape(part) + "</p>" for part in body.split("\n"))
        return ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                "<!DOCTYPE html>\n"
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\" lang=\"zh\">\n"
                f"  <head><title>{html_mod.escape(title)}</title><link rel=\"stylesheet\" href=\"style.css\"/></head>\n"
                "  <body><section epub:type=\"chapter\">\n"
                f"    <h1>{html_mod.escape(title)}</h1>\n{paragraphs}\n"
                "  </section></body>\n</html>\n")

    source_text = (
        f"原文為公有領域作品。電子文本轉錄自維基文庫（{source_url}），"
        "轉錄內容依 CC BY-SA 4.0 授權使用。"
        f"本 EPUB 由 OmniShelf（https://omnishelf.cc）製作並提供下載，亦依 CC BY-SA 4.0 提供。"
        f"全書約 {total} 字。")
    all_chapters = list(chapters) + [(CHAPTER_TAIL, source_text)]

    cid = lambda i: f"ch{i + 1}"
    items = "\n".join(
        f'    <item id="{cid(i)}" href="{cid(i)}.xhtml" media-type="application/xhtml+xml"/>'
        for i in range(len(all_chapters)))
    spine = "\n".join(f'    <itemref idref="{cid(i)}"/>' for i in range(len(all_chapters)))
    nav_items = "\n".join(
        f'      <li><a href="{cid(i)}.xhtml">{html_mod.escape(title)}</a></li>'
        for i, (title, _) in enumerate(all_chapters))
    modified = "2026-11-22"

    content_opf = f"""<?xml version="1.0" encoding="utf-8"?>
<package xmlns="http://www.idpf.org/2007/opf" version="3.0" unique-identifier="bookid">
  <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
    <dc:identifier id="bookid">urn:uuid:omnishelf-zh-{book['slug']}</dc:identifier>
    <dc:title>{html_mod.escape(book['title'])}</dc:title>
    <dc:creator>{html_mod.escape(book['author'])}</dc:creator>
    <dc:language>zh</dc:language>
    <dc:publisher>omnishelf.cc</dc:publisher>
    <dc:date>{modified}</dc:date>
    <dc:rights>原文公有領域;電子文本 CC BY-SA 4.0,轉錄自維基文庫</dc:rights>
    <dc:source>{source_url}</dc:source>
    <meta property="dcterms:modified">{modified}T00:00:00Z</meta>
  </metadata>
  <manifest>
    <item id="nav" href="nav.xhtml" media-type="application/xhtml+xml" properties="nav"/>
    <item id="css" href="style.css" media-type="text/css"/>
{items}
  </manifest>
  <spine>
{spine}
  </spine>
</package>
"""
    nav = f"""<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops" lang="zh">
  <head><title>目次</title></head>
  <body><nav epub:type="toc"><h1>目次</h1><ol>
{nav_items}
    </ol></nav></body>
</html>
"""
    css = ("body { font-family: serif; line-height: 1.9; margin: 6% 8%; }\n"
           "h1 { font-size: 1.4em; margin: 0 0 1.2em; }\n"
           "p { margin: 0 0 0.6em; text-indent: 2em; }\n")

    with zipfile.ZipFile(target, "w") as z:
        z.writestr("mimetype", "application/epub+zip", compress_type=zipfile.ZIP_STORED)
        z.writestr("META-INF/container.xml", """<?xml version="1.0" encoding="utf-8"?>
<container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
  <rootfiles><rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/></rootfiles>
</container>
""", compress_type=zipfile.ZIP_DEFLATED)
        z.writestr("OEBPS/content.opf", content_opf, compress_type=zipfile.ZIP_DEFLATED)
        z.writestr("OEBPS/nav.xhtml", nav, compress_type=zipfile.ZIP_DEFLATED)
        z.writestr("OEBPS/style.css", css, compress_type=zipfile.ZIP_DEFLATED)
        for i, (title, body) in enumerate(all_chapters):
            z.writestr(f"OEBPS/{cid(i)}.xhtml", chapter_xhtml(i, title, body),
                       compress_type=zipfile.ZIP_DEFLATED)
    return target


def main() -> None:
    report = []
    for book in BOOKS:
        try:
            chapters = chapters_for(book)
            if not chapters:
                report.append((book["slug"], "no text", 0, 0))
                continue
            target = build_epub(book, chapters)
            chars = sum(len(body) for _, body in chapters)
            report.append((book["slug"], target.name, len(chapters), chars))
            print(f"ok  {book['slug']:16s} chapters={len(chapters):3d} chars={chars:6d} "
                  f"-> {target.name} ({target.stat().st_size} bytes)")
        except Exception as error:  # noqa: BLE001 - a failed source must not stop the rest
            report.append((book["slug"], f"FAILED {error}", 0, 0))
            print(f"ERR {book['slug']}: {error}")
    pathlib.Path("/tmp/zh-books-report.json").write_text(
        json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")


if __name__ == "__main__":
    main()
