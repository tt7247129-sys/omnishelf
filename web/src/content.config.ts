import { defineCollection, z } from 'astro:content';
import { glob } from 'astro/loaders';

/** Engineering notes: how the reader itself was built. */
const articles = defineCollection({
  loader: glob({ pattern: '**/*.md', base: './src/content/articles' }),
  schema: z.object({
    title: z.string(),
    summary: z.string(),
    date: z.string(),
    order: z.number(),
    readingMinutes: z.number(),
    authorNote: z.string().optional(),
  }),
});

const columnArticle = z.object({
  title: z.string(),
  description: z.string(),
  category: z.enum(['Reading Craft', 'Where to Read', 'Reading Life']),
  pubDate: z.string(),
  updatedDate: z.string().optional(),
  readingMinutes: z.number(),
  related: z.array(z.string()).default([]),
  /** One sentence tying the author's background to this specific piece. */
  authorNote: z.string().optional(),
});

const readingCraft = defineCollection({
  loader: glob({ pattern: '**/*.md', base: './src/content/reading-craft' }),
  schema: columnArticle,
});

const whereToRead = defineCollection({
  loader: glob({ pattern: '**/*.md', base: './src/content/where-to-read' }),
  schema: columnArticle,
});

const readingLife = defineCollection({
  loader: glob({ pattern: '**/*.md', base: './src/content/reading-life' }),
  schema: columnArticle,
});

/**
 * Every page's copy lives here, not in the .astro template. The templates only
 * decide where the text goes.
 */
const pages = defineCollection({
  loader: glob({ pattern: '**/*.md', base: './src/content/pages' }),
  schema: z.object({
    title: z.string(),
    description: z.string(),
    eyebrow: z.string().default(''),
  }),
});

export const collections = {
  articles,
  pages,
  readingCraft,
  whereToRead,
  readingLife,
};
