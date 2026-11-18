---
title: "How to set up an OPDS library on your own server"
description: "OPDS is a catalogue format, not a store: a plain feed of books that any compatible reader can browse. Here is a working setup with Calibre-Web, what the reader sees, and the security details that matter."
category: "Where to Read"
pubDate: "2026-11-23"
authorNote: "Runs one on a home server and reads from it on the commute."
readingMinutes: 9
related: ["epub-mobi-azw3-which-to-keep", "choosing-an-android-ebook-reader"]
---

OPDS stands for Open Publication Distribution System: an Atom feed that lists books and the
links to download them. It is the part of "a bookstore" stripped of payments, accounts and
recommendations — just a catalogue your reader can browse.

Why bother? Because it turns a folder of files into a library you can reach from anywhere,
with covers, authors and series, without uploading anything to a company.

## What you need

- A machine that stays on: a home server, a NAS, or a small VPS
- Your books in one directory
- One server application that speaks OPDS
- A reader that can add an OPDS catalogue (OmniShelf, KOReader, Moon+ and several others)

## The five-minute setup with Calibre-Web

Calibre-Web is the most common choice: it serves a Calibre library over HTTP and exposes
OPDS at a stable path.

```
# 1. a place for the library
mkdir -p ~/books

# 2. run it (Docker shown; a Python install works the same)
docker run -d --name calibre-web \
  -p 8083:8083 \
  -e PUID=1000 -e PGID=1000 \
  -v ~/calibre-library:/config \
  -v ~/books:/books \
  --restart unless-stopped \
  technosoft2000/calibre-web
```

Open `http://your-server:8083`, finish the wizard, point the library at `/books`. The OPDS
catalogue lives at:

```
http://your-server:8083/opds
```

If you want the catalogue without the web interface, COPS is a lighter alternative: it
serves a Calibre folder as OPDS and nothing else, which is exactly what some people want.

## Adding it to a reader

In the reader, choose "Add catalogue" (or "OPDS"), and enter the URL above. If the server
sits behind a password, enter the same username and password in the catalogue entry. The
reader will show a browsable list: by author, by series, by newest.

Fetching a book downloads it into the reader's own storage, where it stays available
offline — which is the point of a catalogue on a phone.

## The security details that actually matter

**Do not expose it to the open internet without TLS.** Put it behind a reverse proxy with a
certificate (Caddy and Traefik will do this in a few lines), and require a password.
A library endpoint that answers without authentication is an open file server.

**Enable authentication in the app itself.** Calibre-Web has users and a guest mode; keep
guest disabled.

**Prefer a VPN or Tailscale for personal use.** If the server is only for you, a private
overlay network means there is nothing to expose at all, and no certificate to renew.

**Back up the library directory, not the container.** The container is disposable; the books
and the database are not.

## Common problems

**The reader shows an empty catalogue.** Almost always a trailing path issue: some servers
expose `/opds/` and redirect `/opds`, and some readers do not follow that redirect. Add the
slash.

**Covers are missing.** Calibre generates covers from metadata. If your files were added by
hand, run the metadata download once on the server.

**Downloads fail on large files.** Some proxies cap upload size. Increase the limit in the
proxy, not in the reader.

## Why this matters for readers

A private OPDS catalogue is the opposite of a store: no recommendations, no lock, no
account on someone else's machine. It is also the only way to read across devices without
handing the library to a cloud service. Readers that support it — and there are several
good ones — make a home library genuinely portable.
