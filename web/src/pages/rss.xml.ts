import type { APIRoute } from 'astro';
import { getCollection } from 'astro:content';

const escape = (value: string) =>
  value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');

export const GET: APIRoute = async ({ site }) => {
  const base = site?.href.replace(/\/$/, '') ?? 'https://omnishelf.cc';

  const columns = [
    { collection: 'readingCraft' as const, path: '/reading-craft/' },
    { collection: 'whereToRead' as const, path: '/where-to-read/' },
    { collection: 'readingLife' as const, path: '/reading-life/' },
  ];

  const items: { title: string; description: string; url: string; date: string }[] = [];

  for (const column of columns) {
    const entries = await getCollection(column.collection);
    for (const entry of entries) {
      items.push({
        title: entry.data.title,
        description: entry.data.description,
        url: `${base}${column.path}${entry.id}/`,
        date: entry.data.pubDate,
      });
    }
  }

  const notes = await getCollection('articles');
  for (const note of notes) {
    items.push({
      title: note.data.title,
      description: note.data.summary,
      url: `${base}/notes/${note.id}/`,
      date: note.data.date,
    });
  }

  items.sort((a, b) => (a.date < b.date ? 1 : -1));
  const latest = items.slice(0, 30);
  const lastBuild = latest[0]?.date ?? new Date().toISOString().slice(0, 10);

  const body = `<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <title>OmniShelf — a reader's notebook</title>
    <link>${base}/</link>
    <description>Essays on reading craft, legal places to find books, and the physical life of reading.</description>
    <language>en</language>
    <lastBuildDate>${new Date(lastBuild).toUTCString()}</lastBuildDate>
    <atom:link href="${base}/rss.xml" rel="self" type="application/rss+xml" />
${latest
  .map(
    (item) => `    <item>
      <title>${escape(item.title)}</title>
      <link>${item.url}</link>
      <guid isPermaLink="true">${item.url}</guid>
      <description>${escape(item.description)}</description>
      <pubDate>${new Date(item.date).toUTCString()}</pubDate>
    </item>`,
  )
  .join('\n')}
  </channel>
</rss>
`;

  return new Response(body, {
    headers: { 'Content-Type': 'application/rss+xml; charset=utf-8' },
  });
};
