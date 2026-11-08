// @ts-check
import { defineConfig } from 'astro/config';

// Static output: every page is pre-rendered, so a crawler or an AdSense
// reviewer with JavaScript disabled still receives the full text.
export default defineConfig({
  site: 'https://omnishelf.cc',
  output: 'static',
  trailingSlash: 'ignore',
  build: {
    format: 'directory',
  },
  markdown: {
    shikiConfig: { theme: 'css-variables' },
  },
});
