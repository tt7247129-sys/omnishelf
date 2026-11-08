---
title: "Why automatic hyphenation is off until you ask for it"
summary: "Justified text in a narrow column needs hyphenation to avoid rivers. Automatic hyphenation needs a language it can trust, and a document does not always have one. The default is ragged right, and here is the reasoning."
date: "2026-11-08"
order: 5
readingMinutes: 7
---

Open a PDF article reflowed into a single column on a phone and look at the right edge.
If the text is justified and hyphenation is off, you will see the classic symptom:
several lines in a row with enormous gaps between words, forming pale diagonal rivers
down the paragraph. It looks like a mistake because it is one.

The fix is hyphenation. Turning it on by default would be the obvious move, and we did
not do it. This is why.

## What hyphenation needs to work

A hyphenation engine needs three things: a language, a set of patterns or a dictionary
for that language, and a way to decide which language a given run of text is in.

The third is the hard one. A file has a metadata language tag, and the tag is frequently
wrong: converters emit `en` for everything, translated works keep the source language,
and academic PDFs routinely contain three languages in one document — an English body, a
German abstract, a French epigraph. The reflowed text stream does not carry per-paragraph
language information, because the original PDF rarely does either.

Hyphenate English text with German patterns and you do not get a small error rate. You
get words broken in places that are not syllable boundaries, several times per page, in
a way that is immediately visible to any reader of the language and impossible to fix
without opening the original.

## What it costs to be wrong

Ragged right text with a few loose lines is an aesthetic imperfection. A document where
every fourth line breaks a word incorrectly is a reading defect: it interrupts the eye
at the exact moment the eye is moving fastest.

There is a second cost that only appears later: soft hyphens travel. Copy a passage with
hyphenation enabled out of a reflowed document and you can carry invisible break
characters into another application, where they either render as boxes or silently break
search. This is a known and tedious class of bug, and it is entirely avoidable by not
inserting hyphens unless the reader has asked for them.

## The compromise

Hyphenation is a per-document setting, exposed in the text settings, off by default.

When it is on, the language comes from the document metadata if it exists, and from the
system locale if it does not. There is no detection heuristic — guessing the language of
a paragraph by character frequency is right most of the time and spectacularly wrong on
short text, which is precisely where a wrong hyphen looks worst.

When it is off, justification still works; the gaps are simply allowed to be larger. On
a narrow measure that is a real trade, and it is the reader's to make.

## The wider point

This is the same decision as the tap zones and the highlights, applied to text: the
default has to be the state that fails gracefully. Hyphenation off produces slightly
loose lines. Hyphenation on with the wrong dictionary produces visibly broken words that
the reader cannot diagnose and cannot turn off per-paragraph.

A reader should default to the failure mode that a person can understand. Everything
else is a setting.

## What about scripts that do not hyphenate

Chinese, Japanese and Korean do not break words at hyphens; they break lines between
characters, with rules about which characters may not start or end a line. Applying a
Latin hyphenation dictionary to CJK text produces nothing useful and a certain amount of
damage.

The setting is therefore language-aware rather than global: it applies to the languages
the engine has patterns for, and is ignored for the rest. That sounds obvious and is easy
to get wrong, because the language of a document and the language of a paragraph inside
it are different questions, and only the first one has an answer.

## The one case where we override the default

There is exactly one situation where hyphenation is turned on without being asked: a PDF
reflowed into a very narrow measure, on a device reporting a small screen, with text that
is justified.

In that case the rivers are not an aesthetic issue; the line spacing collapses into
recognisable vertical channels and the paragraph becomes hard to scan. The override
applies only to the reflowed view — never to an EPUB, where the file's own typography is
respected — and it is visible in the settings, so a reader who dislikes it can see that
it is on and turn it off.

## Why not detect the language

Because detection is the part that fails silently.

A three-line caption in French inside an English article is too short for any character-
frequency model to classify, and a wrong classification hyphenates it badly. The metadata
language, when it exists, is at least a claim made by whoever produced the file; a
guessed language is a claim made by us, and when it is wrong nobody can tell why the text
looks broken.


The next note is about listening: why text-to-speech is a permissions problem before it
is a voice problem.
