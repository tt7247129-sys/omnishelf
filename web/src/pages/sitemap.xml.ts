import type { APIRoute } from 'astro';
import { getCollection } from 'astro:content';

const staticPaths = [
  '/',
  '/notes/',
  '/why/',
  '/shelf/',
  '/about/',
  '/contact/',
  '/privacy/',
  '/terms/',
];

export const GET: APIRoute = async ({ site }) => {
  const base = site?.href.replace(/\/$/, '') ?? 'https://omnishelf.cc';
  const notes = await getCollection('articles');

  const urls = [
    ...staticPaths.map((path) => ({ loc: `${base}${path}`, lastmod: '2026-11-08' })),
    ...notes.map((note) => ({
      loc: `${base}/notes/${note.id}/`,
      lastmod: note.data.date,
    })),
  ];

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
