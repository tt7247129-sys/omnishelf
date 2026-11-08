---
title: "Project Gutenberg has 75,000 books and most people download three"
description: "The catalogue is not the bottleneck. The bottleneck is that a public-domain library is organised like an archive, and readers arrive with a title rather than a question."
category: "Where to Read"
pubDate: "2026-11-08"
readingMinutes: 8
related: ["standard-ebooks-versus-gutenberg-epub"]
---

Project Gutenberg has been giving away public-domain books since 1971, needs no account,
and offers most titles in four or five formats. It is, by any measure, one of the most
useful literary resources on the internet. It is also the site most readers visit exactly
three times.

The pattern is consistent enough to be a design observation: someone hears about it, goes
looking for one classic they have been meaning to read, downloads it, and never returns.
The library is not the problem. Seventy-five thousand titles is more than anyone can read.
The problem is that the site is built as a catalogue, and a catalogue rewards a reader who
already knows what they want.

## What happens when you search for a title

The workflow on a phone is four screens deep. Search returns a list of results with author
and title. The title page lists the formats — HTML, EPUB, Kindle, plain text, sometimes
PDF — as a column of links with no guidance about which one you want.

This is where most readers make the decision that ends the relationship. They pick plain
text, because it sounds neutral, and get a file with no italics. For a novel that is
survivable. For anything with emphasis, quotations or foreign phrases it is not: the
difference between *he said nothing* and he said nothing is the entire scene, and the plain
text version cannot express it.

The EPUB is the right default for almost everyone, and nothing on the page says so.

## The formats are not equivalent, and the site does not rank them

Gutenberg's format list is a menu of technical options, each with a different reading
experience:

- **EPUB** carries structure and styling; it is the format a phone or e-reader expects.
- **Kindle** (`.azw3`/`.mobi`) exists for older Kindle devices and is otherwise redundant.
- **Plain text** carries the words and nothing else — no italics, no chapter breaks beyond
  whitespace, no metadata.
- **HTML** is the original web edition; readable in a browser, awkward to keep in a library.
- **PDF** appears only for a minority of titles, usually ones with figures.

None of this is hidden; it is simply unranked. A catalogue built by volunteers over five
decades accumulates formats the way a house accumulates keys.

## The metadata problem, which matters more

The second reason readers do not return is that Gutenberg's metadata is bibliographic
rather than readerly. A title page lists the author in the form the source edition used —
sometimes a pseudonym, sometimes a full legal name, sometimes with dates — and the release
date of the *ebook*, not the book.

That is correct cataloguing and it is poor discovery. If you liked one Trollope novel, the
site will not tell you which of the other forty-seven are the Barsetshire ones unless you
already know. The catalogue answers "does this title exist" and not "what should I read
next", because the second question was never in scope.

## What actually helps, if you want to use it well

Three habits turn the site from a lookup table into a library.

**Search by subject, not by title.** The subject pages are hand-maintained and threadbare
compared with a modern bookstore, but they group books the way a reader thinks — "Barsetshire",
"sea stories", "early modern philosophy" — rather than by call number.

**Take the EPUB, always.** On any device made this century, it is the only format that
preserves the structure you will want when you jump between chapters.

**Keep a local copy and stop re-downloading.** The files do not expire, and a library that
lives on your device is the difference between a resource you consult and one you forget.

## What the site does better than a bookstore

It would be unfair to leave this as a catalogue critique, because three things about
Gutenberg are genuinely better than any commercial equivalent.

The files are permanent and unwatermarked. A download from 2004 still opens, has no DRM,
and does not phone home. No bookstore can promise that, because their business model
depends on the opposite.

The catalogue is complete rather than curated. A bookstore stocks what sells; Gutenberg
keeps the fourth-rate novels of the 1890s alongside the canon, which is exactly what a
reader doing research in a genre needs.

And the site is honest about provenance. Each title page names the source edition and the
proofreading project that produced it, which is more than most commercial ebook listings
offer — they usually name an imprint and nothing else.

The failure is one of presentation, not of substance. A library that cannot be browsed is
still a library; it is simply one that rewards a reader who arrives with a list.

## Searching it properly

The search box accepts author, title and subject, and the subject field is the one most
people never touch. It is worth understanding what it indexes: Library of Congress subject
headings, applied by cataloguers, in the form "English fiction -- 19th century" rather than
"Victorian novels".

So the useful queries are the formal ones. "English fiction -- 19th century" returns
thousands of titles; "Victorian novels" returns almost nothing. This is a catalogue written
for librarians, and it rewards learning four or five headings.

The second useful field is the bookshelf, a curated grouping that volunteers maintain.
Bookshelves are closer to how readers browse — "Bestsellers, American, 1895-1923" is a
real one — and they are the only place on the site where someone has already made a
judgement about what belongs together.

Neither mechanism is discoverable from the search box. Both are one click deeper on a
result page, under "Browse by" links that look like boilerplate.

## The three books everyone downloads, and why

It is worth naming the pattern, because it is diagnostic rather than mysterious. The
titles that circulate are the ones with a cultural shortcut: *Pride and Prejudice*,
*Frankenstein*, *The Great Gatsby* — books a reader can name without having read, which
means they arrive at the catalogue already knowing what to look for.

That loop is self-reinforcing. Those three are also the most thoroughly proofreaded,
because they attract volunteers; the editions are the best on the site; readers who
download them have a good experience and recommend the site for those books. The other
seventy-four thousand nine hundred and ninety-seven titles never enter the loop, and many
of them are transcriptions that have never been read by a second person.

So the useful move, if you want to actually use the library, is to arrive with a question
rather than a title. A period, a place, a genre, a translator's name — anything that can be
turned into a subject heading will return a shelf instead of a lookup, and a shelf is where
the third book comes from.

## Where this advice stops

This is not about rare books, manuscripts, or anything still in copyright — those are
different problems with different institutions. It is not about non-English catalogues,
which have their own structure and their own strengths. And it does not fix the fundamental
issue: a public-domain collection will always be stronger in the nineteenth century than in
the twentieth, so a reader whose interests are contemporary will find little here.

The other honest limit is that Gutenberg's texts are transcriptions, with the errors that
implies. A volunteer proofreads a novel over weeks; a misplaced quotation mark survives.
For most reading that is invisible. For scholarly citation it is not, which is why the next
note compares these files with a project that re-edits them.

The small thing this produced in OmniShelf: when a downloaded file has no cover and no
metadata, the library still shows the title and author parsed from the EPUB's own package
document rather than the filename, because a shelf full of files called `pg1234.epub` is a
shelf you stop using.
