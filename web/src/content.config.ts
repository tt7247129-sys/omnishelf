import { defineCollection, z } from 'astro:content';
import { glob } from 'astro/loaders';

const articles = defineCollection({
  loader: glob({ pattern: '**/*.md', base: './src/content/articles' }),
  schema: z.object({
    title: z.string(),
    summary: z.string(),
    date: z.string(),
    order: z.number(),
    readingMinutes: z.number(),
  }),
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

export const collections = { articles, pages };
