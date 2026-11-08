---
title: "What 'find my books' actually requires from Android"
summary: "A reader has to index files it did not create, in folders it did not choose, across storage it does not own. The storage APIs were designed for the opposite case, and the gap between them is where the broad-file-access permission lives."
date: "2026-11-08"
order: 7
readingMinutes: 9
---

The first screen of a reader is a lie until it is populated. Before the library has
anything in it, the app is a shell with a menu. Everything the reader came for depends
on one operation — find the books that are already on the device — and that operation is
the hardest thing in the app to do correctly.

## The shape of the problem

Books arrive on a phone in ways the app does not control. A file downloaded from a
browser lands in Downloads. A book sent over a messenger lands in that app's folder. An
SD card carries a library copied from a laptop two years ago. A synced folder mirrors
whatever a desktop machine decided to put in it.

The reader has to enumerate all of that, identify which files are books, extract covers
and metadata, and keep the result stable across launches. It has to do it without asking
the reader to point at each folder, because a reader with six hundred books in nine
folders will not.

## What the modern APIs offer, and why it is not enough

Storage Access Framework is the sanctioned answer. The reader asks the user to pick a
directory through a system picker, and receives a persistable URI grant for that tree.

Three properties make it a poor fit here:

**Grants are per tree, chosen one at a time.** Nine folders means nine trips through a
system picker, and the picker is a file browser — the user has to navigate to each
folder manually, which is precisely the work the library was supposed to remove.

**Enumeration is tree-scoped.** A grant covers one directory and its descendants. Two
unrelated folders have no common ancestor the app is allowed to walk, so there is no
single pass that produces the library. There are N passes, one per grant.

**There is no stable path.** The app receives a document URI, not a path. Code that
opens files by path — and a rendering engine written in C is such code — cannot use it
without copying the file into app storage first. Copying a 300MB scanned PDF to read it
is not a workaround; it is a different product.

MediaStore is the other sanctioned answer, and it is narrower still: it indexes media.
Audiovisual content, images, and — on recent versions — documents of recognised types.
The formats a reader actually cares about are largely absent. EPUB, FB2, MOBI, CBZ, DJVU
and DOCX are not media types in the platform's sense, so a library built on MediaStore
finds nothing.

## What broad file access buys

Direct paths. The ability to enumerate, open, and index files where they already are,
without copying them and without a picker per folder.

That is the entire justification, and it is worth stating plainly because the permission
is a serious one: it grants access to every file the user can read. The honest way to
hold it is to use as little of it as possible and to say so in the declaration:

- The scan filters by extension and content type. Only ebook and document formats are
  read.
- Writes go to the book folder the user selects and to the app's own directory. Nothing
  else is modified.
- No file is uploaded. There is no server to upload it to.

## What happens without the permission

The app still works, and this matters for review and for readers who decline.

Files opened from another application — a file manager, a browser download, a share
sheet — arrive as a URI grant and open normally. The library can also be pointed at a
folder through the system picker, and that folder is indexed. What is lost is the
automatic scan: the reader has to open books one at a time, or point the app at each
folder.

That is a real degradation, and it is why the permission is requested at all. It is not
a degradation that breaks the app, which is the line a permission has to clear.

## The maintenance question

Broad file access is a permission Google audits, and the audit is not a formality. The
declaration has to name a concrete feature, explain why narrower APIs fail, and be
accompanied by a recording. If a future Android release makes tree enumeration across
unrelated directories practical, the justification disappears and the permission should
be removed rather than re-argued.

A permission defended because it is convenient is a permission that will eventually be
taken away, correctly.

The next note is about the format list itself: why thirty-four extensions is not a
feature.
