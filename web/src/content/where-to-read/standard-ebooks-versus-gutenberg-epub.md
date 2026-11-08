---
title: "Standard Ebooks versus the Gutenberg EPUB: where the difference is visible"
description: "Two editions of the same public-domain novel, one transcribed and one re-edited. The typographic gap is real, but it only shows up on certain screens and in certain reading modes — and the metadata difference matters more than the fonts."
category: "Where to Read"
pubDate: "2026-11-08"
authorNote: "Read the same chapter in both editions on three devices, which is how this comparison started."
readingMinutes: 9
related: ["gutenberg-three-books"]
---

Both are free, both are legal, both are public domain, and for a title like *Middlemarch*
they will give you a file with the same words in the same order. The difference is in what
surrounds the words, and it is the kind of difference that is either invisible or
immediately obvious depending on where you read.

We read the same chapter of the same novel in both editions on three devices and wrote
down what changed.

## What Standard Ebooks actually does

The project takes public-domain texts — often from Gutenberg itself, sometimes from
Internet Archive scans — and re-edits them: consistent spelling per the edition's period,
corrected transcription errors, modern semantic markup, a designed title page, and one
consistent stylesheet across the whole catalogue.

That last point is the one people underestimate. Gutenberg's EPUBs were produced over
decades by different volunteers with different tools; the internal markup ranges from
careful to barely-there. Standard Ebooks' files are structurally identical to each other,
which means a reader can make assumptions about them — about where chapter breaks are,
about whether emphasis is marked, about whether a title page exists.

## Where the difference is invisible

On a 6-inch phone at default text size, with the publisher's stylesheet overridden, the two
files are close to indistinguishable.

This surprised us. We expected the designed edition to feel different. It does not, because
at that size and that column width, the reader's own typography settings dominate: you have
already chosen a font, a size, a margin and a line height, and those choices erase most of
what the publisher set.

If you read on a phone with your own settings — which is what almost everyone does — the
typographic argument for Standard Ebooks mostly evaporates.

## Where it becomes obvious

Three conditions change the answer.

**A large screen with publisher formatting enabled.** On a 10-inch tablet or a desktop
reader, the designed edition shows immediately: a proper title page, epigraphs indented
correctly, verse set as verse, scene breaks as a centred mark rather than three blank
lines. Gutenberg's file at the same size looks like a document, because it is one.

**Poetry and drama.** This is where transcription-only editions break down. Line breaks in
verse and speaker labels in drama are structural, and a file that encodes them as
paragraphs with `<br>` will reflow into something that is no longer a poem. Standard
Ebooks marks them semantically, so they survive a font change.

**Any text with notes.** Footnotes that are actual links at the end of a chapter, versus
footnotes rendered as bracketed numbers with the text in a block at the end of the file.

## The part that matters more than typography

Metadata consistency. Every Standard Ebooks release has a cover, a clean title, a
consistent author name, a publication date for the original edition, and a subject list.

Gutenberg's metadata is a bibliographic record written by a cataloguer, and it is accurate
in ways that do not help a library UI. Author names appear in variant forms; the "title"
field sometimes includes the subtitle and sometimes does not; release dates refer to the
ebook, not the book.

For a reader with forty books, this is cosmetic. For a reader with four hundred, it decides
whether the shelf is usable: sorting by author splits one novelist across three spellings,
and searching for a title fails because the record contains the subtitle first.

## What we would actually recommend

Use Standard Ebooks when the title exists there and you intend to read it as a book — the
editing is better, the structure is reliable, and on a tablet or with publisher styles on,
it looks like a published book.

Use Gutenberg when the title is not in Standard Ebooks' catalogue (it is much smaller,
roughly a thousand titles against seventy-five thousand), when you want a specific
transcription of a specific edition, or when you need plain text for processing.

Do not assume the Standard Ebooks version is a better *text*. It is a better *edition* of a
text that both projects took from the same public-domain source. If you are citing, check
which edition the citation refers to; neither file tells you that on its own.

## The cover question, which is not cosmetic

A surprising amount of the difference between the two editions is the cover.

Gutenberg's EPUBs usually have no cover image at all, or a generated one with the title
set in a default typeface. Standard Ebooks commissions a consistent design for its whole
catalogue: the same layout, the same type treatment, a different colour per genre.

In a library grid, this decides whether a shelf looks like a collection or a folder of
files. Cover art is how a reader recognises a book at a glance, and a generated cover is
recognisable only by its title text — which means you read the shelf instead of scanning
it.

We did not expect this to matter as much as it did. When we put forty public-domain titles
on a shelf, the ones with designed covers were the ones we opened, and the effect was not
aesthetic — it was navigational.

There is a limit to how far this argument goes. A cover does not make a text better, and
paying for design is not a substitute for editing. Standard Ebooks does both, which is why
the comparison keeps coming back to the same conclusion: the difference is editorial
labour, and the visible parts are the ones that are easy to photograph.

## The details that decide whether a file behaves

Beyond typography, three structural differences show up in daily use.

**Internal links.** Standard Ebooks' footnotes are links that jump to a note and back.
Gutenberg's are often bracketed numbers with the text at the end of the file, so following
one means losing your place. On a phone this is the difference between using the notes and
ignoring them.

**Language metadata.** Every Standard Ebooks file declares its language, which is what lets
a reading system pick a hyphenation dictionary and a spell-checker. Gutenberg files
frequently omit it, and an omitted language is why automatic hyphenation behaves oddly on
some public-domain titles.

**Semantic markup.** Chapter titles are marked as headings rather than styled paragraphs.
That sounds like housekeeping, but it is what makes the reader's own outline, its "jump to
chapter" and its table of contents work at all. A file with styled paragraphs has an
outline with one entry.

## Where this stops

This comparison is about English-language prose, verse and drama in the Western canon. It
says nothing about scanned PDFs, about illustrated books, or about the many Gutenberg
titles in other languages, where the editing situation is different again.

It also does not settle the question of which one to keep. We keep both for a handful of
titles — the Standard Ebooks edition for reading, the Gutenberg plain text for searching —
and that redundancy is itself a small admission that neither is complete.

The feature this produced in OmniShelf is unglamorous: the library prefers the EPUB's
embedded metadata over the filename, and when two files claim the same title and author it
keeps both and marks the duplicate, rather than silently choosing one.
