#!/usr/bin/env python3
"""Builds the one book on the site we wrote ourselves.

Everything else on the free shelf points at the publisher that made the edition; this guide
is original text owned by the project, so it is the first file the site actually hosts. Run
it after editing CHAPTERS, and commit both the script and the EPUB it produces.
"""
import pathlib
import zipfile
from datetime import date

OUT = pathlib.Path(__file__).resolve().parent.parent / "public" / "books" / "omnishelf-guide.epub"

TITLE = "OmniShelf: a reader's guide"
AUTHOR = "The OmniShelf project"
LANG = "en"
UUID = "urn:uuid:6f2f0d3c-1f0e-4a5e-9b7a-omnishelf-guide"
UPDATED = date.today().isoformat()

CHAPTERS = [
    ("What this app is", [
        "OmniShelf is an ebook and document reader for Android. It opens 34 formats — PDF, "
        "EPUB, MOBI, AZW3, FB2, DJVU, CBZ, CBR and others — from files you already have. It "
        "does not sell books, does not require an account, and does not contain advertising, "
        "analytics or tracking code.",
        "That shape decides most of the advice in this guide: OmniShelf is a reading surface, "
        "not a store. You bring the file; it makes the file pleasant to read.",
    ]),
    ("First run: point it at a folder", [
        "Open the app, choose the folder tab, and pick where your books live — the Download "
        "folder, an SD card, or a cloud folder your phone has already synchronised. OmniShelf "
        "scans that folder and builds the shelf from what it finds. Nothing is uploaded; the "
        "scan happens on the phone.",
        "If the shelf stays empty, the folder is usually the reason: a file manager can open a "
        "book from a location the reader was never given. Check the folder list under Settings "
        "→ Library folders.",
    ]),
    ("The three reading settings worth changing", [
        "Font size and font family. Settings → Text. The bundled serif is the one to try for "
        "long prose; it has the Chinese, Japanese and Korean glyphs a system font may lack.",
        "Page mode. Paged suits novels; scrolling suits PDFs, reference books and anything you "
        "will search within. The control sits in the bottom bar.",
        "Night reading. The moon button switches the whole interface and the page together, so "
        "the toolbar stops being the brightest thing in the room.",
    ]),
    ("Highlights, notes and the quote card", [
        "Long-press a word to start a selection, then drag the handles. The panel that appears "
        "can highlight, underline, annotate, and — from the same panel — make a quote card: a "
        "shareable image of the passage, the book and the author, in five layouts.",
        "Highlights are stored with the book, so deleting the app does not delete them unless "
        "you also clear its storage. Settings → Backup exports them.",
    ]),
    ("Listening offline", [
        "The audio button reads the current page with the device's own text-to-speech engine. "
        "It works with the screen off and without a network, because nothing is streamed. If "
        "the voice sounds wrong for the language, install a better engine for that language "
        "from the system settings — OmniShelf uses whatever the phone offers.",
    ]),
    ("Where the books come from", [
        "OmniShelf is a reader, so the library question is really: where do you get files? The "
        "site's own free shelf lists public-domain books in EPUB — free to keep, everywhere.",
        "For books still in copyright, buy them from a store that sells EPUB (or reads them "
        "without DRM) and open the file here. Public libraries with Libby or OverDrive also "
        "lend EPUBs for free.",
        "What OmniShelf will not do — and this site will not help with — is pirate books.",
    ]),
    ("Privacy, concretely", [
        "No account. No advertising ID. No analytics SDK. The app makes a network request only "
        "when you open an OPDS catalogue or configure a cloud folder. Your reading position, "
        "highlights and shelf stay on the phone; if you turn on cloud sync, the file goes to "
        "the provider you configured, not to us.",
    ]),
    ("If something breaks", [
        "Note the file format, the phone model and what you expected to happen. Send it to "
        "hello@omnishelf.cc. A reader that opens 34 formats is a reader that meets 34 different "
        "kinds of broken file, and reports are how the list gets shorter.",
    ]),
]


def chapter_xhtml(index: int, title: str, paragraphs: list[str]) -> str:
    body = "\n".join(f"      <p>{p}</p>" for p in paragraphs)
    return f"""<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops" lang="{LANG}">
  <head><title>{title}</title><link rel="stylesheet" href="style.css"/></head>
  <body>
    <section epub:type="chapter">
      <h1>{title}</h1>
{body}
    </section>
  </body>
</html>
"""


def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    chapters = [(f"ch{i + 1}", t, ps) for i, (t, ps) in enumerate(CHAPTERS)]

    opf_items = "\n".join(
        f'    <item id="{cid}" href="{cid}.xhtml" media-type="application/xhtml+xml"/>'
        for cid, _, _ in chapters
    )
    spine = "\n".join(f'    <itemref idref="{cid}"/>' for cid, _, _ in chapters)

    content_opf = f"""<?xml version="1.0" encoding="utf-8"?>
<package xmlns="http://www.idpf.org/2007/opf" version="3.0" unique-identifier="bookid">
  <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
    <dc:identifier id="bookid">{UUID}</dc:identifier>
    <dc:title>{TITLE}</dc:title>
    <dc:creator>{AUTHOR}</dc:creator>
    <dc:language>{LANG}</dc:language>
    <dc:publisher>omnishelf.cc</dc:publisher>
    <dc:date>{UPDATED}</dc:date>
    <dc:rights>© {AUTHOR}. Licensed CC BY 4.0.</dc:rights>
    <meta property="dcterms:modified">{UPDATED}T00:00:00Z</meta>
  </metadata>
  <manifest>
    <item id="nav" href="nav.xhtml" media-type="application/xhtml+xml" properties="nav"/>
    <item id="css" href="style.css" media-type="text/css"/>
{opf_items}
  </manifest>
  <spine>
{spine}
  </spine>
</package>
"""

    nav = f"""<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops" lang="{LANG}">
  <head><title>Contents</title></head>
  <body>
    <nav epub:type="toc"><h1>Contents</h1><ol>
{''.join(f'      <li><a href="{cid}.xhtml">{t}</a></li>' + chr(10) for cid, t, _ in chapters)}    </ol></nav>
  </body>
</html>
"""

    css = """body { font-family: serif; line-height: 1.65; margin: 6% 8%; }
h1 { font-size: 1.6em; margin: 0 0 1.2em; }
p { margin: 0 0 1em; }
"""

    with zipfile.ZipFile(OUT, "w") as z:
        z.writestr("mimetype", "application/epub+zip", compress_type=zipfile.ZIP_STORED)
        z.writestr("META-INF/container.xml", """<?xml version="1.0" encoding="utf-8"?>
<container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
  <rootfiles><rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/></rootfiles>
</container>
""", compress_type=zipfile.ZIP_DEFLATED)
        z.writestr("OEBPS/content.opf", content_opf, compress_type=zipfile.ZIP_DEFLATED)
        z.writestr("OEBPS/nav.xhtml", nav, compress_type=zipfile.ZIP_DEFLATED)
        z.writestr("OEBPS/style.css", css, compress_type=zipfile.ZIP_DEFLATED)
        for cid, title, paragraphs in chapters:
            z.writestr(f"OEBPS/{cid}.xhtml", chapter_xhtml(0, title, paragraphs),
                       compress_type=zipfile.ZIP_DEFLATED)

    print(f"wrote {OUT} ({OUT.stat().st_size} bytes, {len(chapters)} chapters)")


if __name__ == "__main__":
    main()
