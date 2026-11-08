---
title: "What a reader should say when the file will not open"
summary: "An inherited error dialog offered to hand the book to a different app — one sold by the upstream author. It looked helpful. It was a sales funnel wearing an error message."
date: "2026-11-08"
order: 9
readingMinutes: 7
---

Every reader has a dialog for the file it cannot open. It is the least-designed screen in
the application and the one people see at their worst moment: they have just tried to
read something and failed.

Ours inherited a bad one, and it took a while to notice why it was bad.

## What the dialog used to do

When a document failed to open — a corrupt download, a format the engine could not parse,
a file that had crashed the app on the previous attempt — the app displayed a message and
offered two options.

The first was to try again. Reasonable.

The second offered to open the file in a different application, described as a newer
reader from the same author. If that application was not installed, the second option
became a link to its store page.

Read that again: the error path for a file the app could not open was also an
advertisement for a paid product by the same developer.

Nobody wrote that as a cynical decision. It was added, plausibly, as a helpful escape
hatch — this file did not work here, perhaps it works there. But the effect is what
matters: a moment of failure became a moment of monetisation, in an app whose entire
proposition is that it does not do that.

## Why it survived the rebrand

Because it was not a banner or a purchase button. It was a dialog about failure, buried
in a class named after that other application, and it only appeared when something went
wrong — which, during testing with well-formed files, was never.

This is the general shape of inherited problems: the code that embarrasses you is the
code that runs on the unhappy path, because the happy path is the one everybody tests.

## What it does now

The dialog reports the failure, offers a retry, and stops.

No third-party offer, no store link, no suggestion that the reader's problem would be
solved by installing something else. If the file crashed the app last time, the message
says that explicitly, because "it failed" and "it killed us last time" are different
facts and the second one changes what a sensible reader does next.

Three details make the new dialog more useful than the old one:

**It distinguishes the failure modes.** A file that threw an exception during parsing, a
file that produced zero pages, and a file that took the process down are three different
problems with three different likely causes. The message names which one happened.

**It keeps the retry.** Some failures are transient — a file still being written by
another app, a storage volume that was momentarily unavailable.

**It does not pretend to know why.** The dialog does not guess that the file is corrupt,
because it usually does not know. It says the document could not be opened, which is the
only claim the code can support.

## The crash loop

There is a worse version of the same path: a file that does not merely fail to open, but
takes the process down while opening it. Native parsers make this a real category rather
than a theoretical one.

If the app tries again automatically on the next launch — because the book was the last
one open — the result is a crash on every start, with no way into the application to turn
the feature off. That is how a single malformed file becomes an uninstall.

The guard is a small record written before the file is parsed and cleared after the open
succeeds. If the record is still there on the next launch, the file crashed the app last
time: the reader is told, the automatic reopen is skipped, and the book is marked in the
library so it is not tapped again by accident.

Two lines of persistent state, written in the one place where the app cannot afford to be
clever. It is the least elegant code in the reader and the reason a single bad file does
not cost a user their library.

## What the message refuses to say

It does not say the file is corrupt, because the app does not know that. A file that
fails to parse may be truncated, encrypted, password-protected, or perfectly valid in a
way the parser does not implement.

It does not suggest that another application might open it, because that is an
advertisement dressed as advice, and because the honest answer is that the reader already
has the app that was supposed to.

It does not apologise. An error message that spends its first sentence on feelings spends
its only sentence on something other than the failure.


## The rule behind it

The unhappy path is where a product's values are visible, because nobody is watching it
in a demo.

An app that promises no advertising has to mean no advertising in the error dialog, no
advertising in the upgrade prompt that does not exist, and no advertising in the empty
state that suggests what to read next. Each of those is a place where a business model
would normally leak in, and each of them has to be checked deliberately rather than
assumed.

We found ours by reading the strings, not by using the app. That is the uncomfortable
lesson: the happy path is well tested, and the values live everywhere else.
