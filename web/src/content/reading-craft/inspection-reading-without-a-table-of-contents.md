---
title: "Inspection reading without a table of contents"
description: "Adler's second reading level assumes you can flip to the contents page. On a phone the contents page is a scrolling menu, and the chapter you want is often not in it at all. What survives, and what we changed."
category: "Reading Craft"
pubDate: "2026-11-08"
authorNote: "Spent two weeks timing how long it takes to reach a chapter on a phone before changing the outline panel described here."
readingMinutes: 8
related: ["marginalia-budget"]
---

Inspection reading is the level nobody teaches and everybody uses. You have a book you are
not sure you want to read. You spend ten minutes on it: title page, preface, the table of
contents, the index, a paragraph from the middle of two chapters. Then you decide. Adler
called it the art of getting the gist before committing, and the whole method rests on one
physical fact — that the apparatus of a book is arranged so your eye can move across it
quickly.

That apparatus is exactly what a phone destroys, and the destruction is not uniform.

## What the contents page was for

A printed contents page is a single rectangle. Twelve chapters fit on one page, and the
shape of the list — how long the chapter titles are, where the parts break, which chapters
have subsections — is information you absorb without reading it. You can see that the book
is front-loaded, or that one chapter is three times the length of the others, before you
have read a word of either.

On a 6.7-inch phone, an EPUB's navigation document renders as a scrolling list. At a
comfortable text size, one screen holds about eleven lines. A fifteen-chapter book with
subsections is thirty-five lines. So inspection reading becomes a scroll: you see the first
eleven, you swipe, you see the next eleven, you have lost the shape.

We measured the cost in the least scientific way available: from a cold open of a 320-page
PDF to having chapter five's opening sentence on screen, using the built-in outline. The
first version of our outline view took us fourteen seconds. After flattening it to a single
column with the depth of the outline preserved visually instead of structurally, the same
task took six.

Six seconds is not a triumph. It is the difference between a method you use and a method
you skip.

## The chapter that is not in the list

Here is where the classical method and the file format disagree more seriously.

Adler's inspection reading tells you to look at the index and pick entries to sample. Many
EPUBs have no index at all — the print edition had one, the digital edition dropped it
because it could not be made to work as a link list. Many PDFs have no outline: scanned
books have none by construction, and a great deal of the born-digital literature has an
outline that names "Chapter 1" through "Chapter 12" and nothing else.

An academic PDF from the last twenty years usually does have a proper outline, because the
typesetting system that produced it emits one automatically. A scan of a 1962 paperback
does not, and never will.

So inspection reading on a device splits into two cases with almost nothing in common. In
the first, you have a navigation structure and the problem is presenting it compactly. In
the second, you have no structure, and the only way to survey the book is to scrub through
it and look at the pages.

## We thought the interface was the problem

The first version of the outline panel was a proper tree: parts, chapters, subsections,
expandable. It was accurate and it was slow to scan, because a tree at phone width spends
most of its horizontal space on indentation.

We assumed the fix was a better tree. The fix was to stop drawing a tree at all and render
the outline as a single indented list where depth is expressed by type size and a thin
rule, not by leading whitespace. The structure is still there; the space it occupies is
not.

The second assumption we had to drop was that readers want to jump to a chapter. They
want to jump to a *place in the argument*, which is often a section inside a chapter. A
chapter-level outline makes inspection reading impossible in a book whose chapters run
forty pages. That is why the list keeps subsection entries visible rather than collapsing
them under their parent.

## What the file format actually promises

It is worth being precise about what a reader can expect, because the two dominant formats
promise different things and neither promises a usable outline.

An EPUB 3 file contains a navigation document: a structured list of links to the places
the publisher decided were navigable. It is mandatory in the specification and optional in
practice, because the EPUB 2 files that make up most of the public-domain corpus predate
it. When a converter generates one from old markup, the result is often a list of every
heading in the file, including "Chapter" as a heading and the chapter title as a separate
one — which produces an outline with twice as many entries as chapters and no way to tell
which is which.

A PDF contains an optional outline dictionary: a tree of titles pointing at page numbers.
It is not required, and scanned books never have one. When it does exist it is almost
always accurate, because it was written by the same software that laid out the pages.

The practical consequence is that the quality of inspection reading on a device depends on
which kind of file you happened to download, and the file does not tell you before you open
it. Two editions of the same book, downloaded from the same place on different days, can
behave completely differently.

We considered detecting the outline at import time and labelling books that have none, so
the reader knows what they are getting. We did not ship it, because the label was accurate
and useless: knowing in advance that a book has no table of contents does not change what
you do with it.

## What this does not solve

Three honest limits.

It does not help with fiction, because fiction does not have an argument to inspect. The
first chapter is the sample; that is what the sample is for.

It does not reconstruct a missing outline. If a scanned PDF has no bookmarks, the reader
can offer page thumbnails and nothing smarter; producing a table of contents from a scan
requires OCR and a guess, and a wrong guess is worse than an empty panel because it sends
you to the wrong page with confidence.

And it does not replace the index. A real back-of-book index is a map of *concepts*, and
searching for a word is not the same operation — search finds the string, an index finds
the idea under a heading the author chose. Nothing in a reading app can invent that.

## The small decision it produced

The one change we kept from all of this is unglamorous: the outline panel opens at a fixed
width, single column, with subsection entries shown by default, and it remembers how deep
you went.

That is the whole feature. It is not a rethinking of inspection reading. It is a
recognition that the method needs the whole shape of a book visible in one glance, and that
a scrolling list of thirty-five lines cannot provide it unless every line is the same width.

If you want the other half of this argument — what happens when you start marking the text
you inspected — it is in the next note on annotation and the size of a margin.
