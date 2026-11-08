---
title: "One shelf, two ideas of where I stopped"
summary: "A PDF knows its pages. An EPUB does not know its pages until the screen decides. Storing both kinds of position in one library is the least visible decision in the app, and the one users notice first when it is wrong."
date: "2026-11-08"
order: 2
readingMinutes: 8
---

Put a novel and a scanned paper on the same shelf and you inherit a problem that has
nothing to do with either file: the two documents do not agree on what a position is.

## Two kinds of position

A PDF position is a coordinate. Page 214, and within it a rectangle — typically the top
of the page, sometimes a point on it. The coordinate is stable because the page is a
stored object with fixed dimensions. Reopen the file on a different device and page 214
is the same page.

An EPUB position is a pointer into a text stream. The format's answer is a CFI — an
expression that identifies a location in the content document tree, something like a
path down through the spine, into an XHTML file, and then into a text node at a
character offset. It is precise and completely unreadable, and it does not mention pages
at all, because an EPUB has no pages: the reading system manufactures them from the
current screen, font and margin.

So the library stores two different things in the same column. For PDFs, a page number
and a small offset. For EPUBs, a CFI string. Neither is meaningful to the other.

## The percentage is the lie

The obvious solution is to normalise both into a percentage and show that. It is what
most readers do, and it is wrong in a way that is hard to argue with until you watch
someone use it.

Sixty-two percent of a novel is a real place: you read to the end of the chapter and
stop. Sixty-two percent of a PDF is arithmetic. It tells you nothing about what is
there, because the relationship between a PDF's pages and its content is arbitrary —
page 300 of a 480-page anthology might be the middle of a short story, or blank, or a
plate. "62%" is a progress bar, not a position; it answers "how much is left" and
nothing else.

We show the percentage in the library grid, because it is the only thing that can be
compared across formats at a glance. We do not use it to resume. Resuming uses the
format's own model.

## What resume actually does

For a PDF: restore the page, restore the zoom and the scroll offset within the page,
restore the reading mode. If the document is being displayed reflowed, translate the
original page into the reflowed position rather than the reverse — the original page is
the thing that survives a font change.

For an EPUB: restore the CFI, then verify it. A CFI is fragile in a specific way: it
points into a document tree, and if the book was re-generated, or if a converter
normalised the XHTML between sessions, the same CFI can resolve to a different node or
fail to resolve at all. So the restore is a two-step: resolve, and if resolution fails,
fall back to the nearest chapter start and say so quietly rather than opening at page
one.

The quiet part matters. A reader that silently reopens a book at the beginning because
a pointer went stale teaches the user that their progress is not reliable. A reader that
reopens at the top of the current chapter has lost a few pages, not a week of reading.

## Where the two models collide

Annotations are the collision point.

A highlight in a PDF is a set of rectangles over a fixed page. It survives anything: the
page never changes, so the rectangles never move. A highlight in an EPUB is a text range
inside a document that may be re-laid out; if the reader changes the font size, the
rectangles that represented the highlight on screen are meaningless, and the range has
to be re-measured against the new layout.

Both are stored as a text range plus, for the PDF case, a cached geometry. The cached
geometry is treated as a rendering hint, never as the truth. When the hint disagrees with
the current layout — which happens whenever the screen changes — the hint is discarded
and the range is re-measured. That is more expensive and it is the only way a highlight
survives rotation, font size and reflow.

The alternative, storing geometry as the truth, produces the classic bug: rotate the
phone and every highlight in the book sits three lines above the text it marks.

## The test that matters

The check we run before calling a build finished is not "does it remember the page".
It is: open a PDF, read to page 40, change the font size, close the app, reopen it,
rotate the device, and land on the same paragraph.

That sequence is boring and it exercises every decision above at once. When it passes,
the shelf feels like one thing. When it fails, the two formats feel like two apps that
happen to share a grid — which is exactly what a reader notices within a minute.

The next note is about colour: what happens to a highlight when the screen has none.
