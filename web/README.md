# omnishelf.cc

The website for OmniShelf, the Android reader. Astro, static output, no runtime.

Every word of copy lives in `src/content/` as Markdown. The `.astro` files decide where
text goes; they do not contain it.

```
web/
├── astro.config.mjs          site URL, static output
├── package.json
├── public/                   robots.txt, icon, apple-touch-icon
└── src/
    ├── content.config.ts     collections: articles, pages
    ├── content/
    │   ├── articles/         9 long-form notes (800–1,100 words each)
    │   └── pages/            home, why, about, contact, privacy, terms, shelf
    ├── layouts/
    │   ├── Base.astro        <head>, header, footer
    │   └── Page.astro        renders a `pages` entry
    ├── components/           Header, Footer, ArticleCard, Prose
    ├── pages/                routes; sitemap.xml is generated at build time
    └── styles/
        ├── tokens.css        the entire visual system
        └── global.css        layout and prose rules
```

## Build

```bash
pnpm install
pnpm build     # → dist/
pnpm dev       # local preview on http://localhost:4321
```

Node 22 and pnpm 9 are what CI uses.

## Deployment

`.github/workflows/deploy.yml` builds `web/` and publishes `web/dist` to GitHub Pages.
It runs on pushes to `site-live` and by hand, **not** on `master`: a push to master must
not take the live site down while the domain is being moved.

### Order matters — Cloudflare + GitHub Pages

The brief asks for Cloudflare Proxy (orange cloud) with SSL/TLS set to **Full (strict)**.
That is the correct end state, and it cannot be the first state: GitHub has to issue a
TLS certificate for `omnishelf.cc` before Cloudflare can validate the origin, and the
proxy hides the origin from the certificate authority.

Do it in this order:

1. **Repository → Settings → Pages → Custom domain**: enter `omnishelf.cc`, save.
2. **Cloudflare → DNS**, with the proxy **off** (grey cloud) for both records:

   | Type | Name | Content | Proxy |
   |---|---|---|---|
   | CNAME | `@` | `tt7247129-sys.github.io` | DNS only |
   | CNAME | `www` | `tt7247129-sys.github.io` | DNS only |

3. Wait until GitHub shows "Certificate issued" (minutes to an hour). `https://omnishelf.cc`
   must answer directly, not through Cloudflare.
4. **Cloudflare → SSL/TLS**: set encryption mode to **Full (strict)**. Turn on *Always Use HTTPS*.
5. **Cloudflare → DNS**: switch both records to **Proxied** (orange cloud).
6. Verify: `curl -sI https://omnishelf.cc | head -1` returns 200 or 301, and the browser
   shows a valid certificate with no redirect loop.

If you turn the proxy on before step 3, the certificate challenge fails and the site
enters a redirect loop. If you set Full (strict) before GitHub has a certificate for the
domain, Cloudflare returns 526.

### After the domain works

Done already: the domain is live, the certificate is approved, HTTPS is enforced, and
the legacy `site/` placeholder has been removed.

Remaining, in the Play Console: set the privacy policy URL to
`https://omnishelf.cc/privacy/`. The old `github.io` address still redirects here, so the
listing does not break in the meantime.

Then submit `https://omnishelf.cc/sitemap.xml` in Google Search Console.

## Publishing rules

- No advertising code is deployed. AdSense is submitted against the site as it is, with
  the content complete and no ad slots.
- Every page is readable with JavaScript disabled; the HTML carries the full text.
- `dist/` is generated; never edit it.
