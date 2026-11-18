---
title: "Choosing an Android ebook reader: four apps, one honest table"
description: "Moon+ Reader, KOReader, Librera and OmniShelf solve different problems. A comparison written by someone who builds one of them, with the rows that do not flatter the author's own app."
category: "Where to Read"
pubDate: "2026-11-18"
authorNote: "Builds OmniShelf, and lists its weaknesses in the table below because a comparison that hides them is an advertisement."
readingMinutes: 9
related: ["cbz-cbr-comics-on-android", "djvu-on-android"]
---

There are four Android readers worth installing, and they are not interchangeable. The
usual comparison pages list every reader as having "many formats, many features", which
tells a reader nothing. This one is organised around the question people actually ask:
*which of these will I still be using in six months?*

## The short version

- **Moon+ Reader** — the most features, including options nobody else has. Paid for the
  full version, closed source, and it shows advertising in the free version.
- **KOReader** — made for e-ink devices and readers who like to configure everything.
  Superb on a Kindle-like screen, unusual on a phone.
- **Librera** — the most formats and the most localisation, free, closed source now, and
  its interface has accumulated a decade of features.
- **OmniShelf** — a fork of Librera with the interface rebuilt. Free, open source, no ads,
  fewer features than Moon+.

## What actually differs

| | Moon+ | KOReader | Librera | OmniShelf |
|---|---|---|---|---|
| Price | free with ads; paid removes them | free | free | free |
| Source | closed | open | closed | open (AGPLv3) |
| Formats | many | many | 34 | 34 |
| PDF support | strong, with reflow | basic | strong | strong |
| CBZ / CBR comics | yes | yes | yes | yes |
| DJVU | no | yes | yes | yes |
| Offline text-to-speech | yes | limited | yes | yes |
| Highlights and notes | yes | yes | yes | yes |
| Cloud sync | yes, paid | via plugins | no | no |
| Interface languages | ~10 | ~20 | 30+ | 54 |
| Ads or trackers | ads in free version | none | none | none |
| Complexity | high | very high | high | medium |

If one row matters more than the rest to you, you already know the answer. The rows below
are the ones that decide it for most people.

## The row that matters most: what you read

If your library is mostly EPUB novels, every app in the table does the job and you should
pick on interface and price. If it contains scanned PDFs, check whether the app renders
them without stalling; PDF rendering is the first thing to break on a phone. If it
contains comics, CBZ and CBR support is not optional — and a reader that lists the format
but refuses to open a file with image-only pages is worse than useless.

## The row people forget: what the app sends

A reader does not need the internet. It needs your storage. Any app that asks for network
access to open a local file is asking for a permission it does not need, and no entry in
the table above except one requires it — the exception is cloud sync, which you can simply
not use.

## Where each one wins

**Moon+ Reader** wins on options: dictionaries, TTS voices, themes, gestures, and a
reflow mode for PDFs that other apps do not have. If you want the maximum number of
switches, this is the app.

**KOReader** wins on an e-ink screen. The page-turn handling, the typography and the
dictionary integration were designed around a device that refreshes slowly. On an OLED
phone it feels like a tool for a different machine.

**Librera** wins on breadth: the format list is long, the localisation is deep, and it has
features accumulated over many years, from page cropping to text-to-speech voices.
Its interface is the cost: the drawer, the header and the floating bars overlap, which is
what led to the fork described in the next section.

**OmniShelf** wins on exactly three things: no advertising or tracking code at all, an
interface rebuilt around one bar per screen, and 54 interface languages. It loses on
features — there is no cloud sync, no PDF reflow, no plugin system.

## Why a fork of Librera exists

Because the interface is where readers give up. Librera's drawer plus header puts two
navigation layers on one screen, and its tinted bars made the icons nearly invisible.
Those are fixable problems, and the AGPL licence allows a fork to fix them. The result is
not a better app than Moon+ in features. It is a smaller app that a reader with a plain
local EPUB library can use without seeing an advertisement or granting a permission.

## How to decide in ten minutes

1. Install two of them, not four.
2. Open the same three files: your most common EPUB, your heaviest scanned PDF, and a CBZ
   if you read comics.
3. Use each for one evening.
4. Delete the one whose interface you thought about while reading. The best reader is the
   one you stop noticing.
