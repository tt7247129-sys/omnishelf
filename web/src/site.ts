/**
 * One place for every fact the site repeats: the store links, the app's numbers, the
 * contact address. When the Play listing goes public, flip `play.published` here and
 * every button, QR caption and redirect follows.
 */

export const SITE = {
  name: 'OmniShelf',
  origin: 'https://omnishelf.cc',
  email: 'hello@omnishelf.cc',
  github: 'https://github.com/tt7247129-sys/omnishelf',
  play: {
    packageId: 'cc.omnishelf.reader',
    /**
     * Closed testing means there is no public store page yet: the button joins the test
     * instead of pretending a listing exists. Flip to true on the day it goes live.
     */
    published: false,
    storeUrl: 'https://play.google.com/store/apps/details?id=cc.omnishelf.reader',
    testingUrl: 'https://play.google.com/apps/testing/cc.omnishelf.reader',
  },
  app: {
    version: '1.3.0',
    formats: 34,
    languages: 55,
    minAndroid: '7.0',
  },
  /** Search-engine ownership checks; both empty until the owner adds the property. */
  verification: {
    google: import.meta.env.PUBLIC_GOOGLE_SITE_VERIFICATION ?? '',
    bing: import.meta.env.PUBLIC_BING_SITE_VERIFICATION ?? '',
  },
  analytics: {
    /**
     * Optional. Set PUBLIC_UMAMI_SRC and PUBLIC_UMAMI_WEBSITE_ID in the build environment
     * to switch on cookieless page and event counting; empty means no script at all.
     */
    umamiSrc: import.meta.env.PUBLIC_UMAMI_SRC ?? '',
    umamiWebsiteId: import.meta.env.PUBLIC_UMAMI_WEBSITE_ID ?? '',
  },
} as const;

/** Where a download click actually goes today. */
export const PLAY_TARGET = SITE.play.published ? SITE.play.storeUrl : SITE.play.testingUrl;

/** What the primary button says, which depends on whether there is a listing yet. */
export const PLAY_LABEL = SITE.play.published ? 'Get it on Google Play' : 'Join the closed test';

/** The internal link every call to action points at, carrying where it was clicked. */
export function playLink(src: string, campaign = 'site'): string {
  return `/go/play/?src=${encodeURIComponent(src)}&utm_campaign=${encodeURIComponent(campaign)}`;
}

/**
 * The Play URL including an install referrer, so the app can tell that a install came from
 * this site. utm_* is what Play Install Referrer hands back on first launch.
 */
export function playUrlWithReferrer(src: string, campaign = 'site'): string {
  const referrer = new URLSearchParams({
    utm_source: 'omnishelf.cc',
    utm_medium: 'web',
    utm_campaign: campaign,
    utm_content: src,
  }).toString();
  return `${SITE.play.storeUrl}&referrer=${encodeURIComponent(referrer)}`;
}
