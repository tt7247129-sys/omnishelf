import type { APIRoute } from 'astro';
import { getCollection } from 'astro:content';

const staticPaths = [
  '/',
  '/reading-craft/',
  '/where-to-read/',
  '/reading-life/',
  '/notes/',
  '/why/',
  '/shelf/',
  '/about/',
  '/authors/',
  '/contact/',
  '/privacy/',
  '/terms/',
];

export const GET: APIRoute = async ({ site }) => {
  const base = site?.href.replace(/\/$/, '') ?? 'https://omnishelf.cc';

  const columns = [
    { collection: 'readingCraft' as const, path: '/reading-craft/' },
    { collection: 'whereToRead' as const, path: '/where-to-read/' },
    { collection: 'readingLife' as const, path: '/reading-life/' },
  ];

  const urls = staticPaths.map((path) => ({ loc: `${base}${path}`, lastmod: '2026-11-08' }));

  for (const column of columns) {
    const entries = await getCollection(column.collection);
    for (const entry of entries) {
      urls.push({
        loc: `${base}${column.path}${entry.id}/`,
        lastmod: entry.data.updatedDate ?? entry.data.pubDate,
      });
    }
  }

  const notes = await getCollection('articles');
  for (const note of notes) {
    urls.push({ loc: `${base}/notes/${note.id}/`, lastmod: note.data.date });
  }

  const body = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${urls
  .map(
    (url) => `  <url>
    <loc>${url.loc}</loc>
    <lastmod>${url.lastmod}</lastmod>
  </url>`,
  )
  .join('\n')}
</urlset>
`;

  return new Response(body, {
    headers: { 'Content-Type': 'application/xml; charset=utf-8' },
  });
};
