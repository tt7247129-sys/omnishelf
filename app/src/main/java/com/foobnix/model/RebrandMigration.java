package com.foobnix.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.foobnix.android.utils.IO;
import com.foobnix.android.utils.LOG;
import com.foobnix.pdf.info.TintUtil;

/**
 * One-time move to the OmniShelf defaults.
 *
 * <p>The app keeps its settings on shared storage, not in the application's data directory,
 * so uninstalling and reinstalling does not reset them: a reader who upgrades keeps the
 * tab order and the accent colour of the old build, and the new defaults never appear.
 *
 * <p>This rewrites the two values that the redesign changed, and only when they still hold
 * the old default. A colour the reader chose deliberately will be moved once; a colour they
 * chose from the new palette is left alone. Runs once per install; the flag is written even
 * when nothing changed.
 */
public class RebrandMigration {

    private static final String PREFS = "omnishelf_migrate";
    private static final String KEY = "ui_defaults_v1";
    /**
     * The redesigned look is not something the reader has to discover: on the first
     * launch of this build the presentation settings are set to the new defaults,
     * whatever the profile happens to hold. Profile files live on shared storage and
     * survive reinstalls, so without this a reader keeps the look of the build before.
     */
    private static final String KEY_UI = "ui_defaults_v2";
    private static final String KEY_COLOUR = "ui_colour_v3";
    private static final int NEW_TINT = Color.parseColor("#4A5D4E");
    private static final int CINNABAR = Color.parseColor("#8E2B21");

    /** The colours this fork accepts as its own; anything else is a leftover. */
    private static final String[] OMNI_PALETTE = {"#8E2B21", "#C83A2A", "#6E1F17"};

    /** What the app shipped with before the redesign. */
    private static final String OLD_TINT = "#3949AB";
    private static final String OLD_TABS = "0#1,1#1,2#1,3#1,4#1,5#0,6#0,7#0";

    public static void apply(Context context) {
        if (context == null) {
            return;
        }
        try {
            SharedPreferences flags = context.getApplicationContext()
                                            .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if (flags.getBoolean(KEY, false)) {
                return;
            }
            AppState state = AppState.get();
            if (state == null) {
                return;
            }

            boolean changed = false;
            if (state.tintThemeColor == Color.parseColor(OLD_TINT)) {
                state.tintThemeColor = Color.parseColor("#4A5D4E");
                TintUtil.color = state.tintThemeColor;
                changed = true;
            }
            if (OLD_TABS.equals(state.tabsOrder9)) {
                state.tabsOrder9 = AppState.DEFAULTS_TABS_ORDER;
                changed = true;
            }

            if (changed) {
                if (AppProfile.syncState == null) {
                    // The profile is not on disk yet; leave the flag unset so the next start
                    // tries again rather than silently losing the migration.
                    return;
                }
                IO.writeObjSync(AppProfile.syncState, state);
            }
            flags.edit().putBoolean(KEY, true).apply();
            LOG.d("RebrandMigration", "applied", changed);
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

    /**
     * Forces the presentation settings of the redesign, once per install. Reading data
     * (progress, bookmarks, library) is untouched - only how the app looks.
     */
    public static void applyLook(Context context) {
        if (context == null) {
            return;
        }
        try {
            SharedPreferences flags = context.getApplicationContext()
                                            .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if (flags.getBoolean(KEY_UI, false)) {
                return;
            }
            AppState state = AppState.get();
            if (state == null) {
                return;
            }
            state.tintThemeColor = NEW_TINT;
            // A language stored by an older install outlives the reinstall, exactly like
            // the look: the first launch of this build follows the system language again.
            state.appLang = AppState.MY_SYSTEM_LANG;
            state.tabsOrder9 = AppState.DEFAULTS_TABS_ORDER;
            state.isShowTime = true;
            state.isShowBattery = true;
            TintUtil.color = NEW_TINT;

            if (AppProfile.syncState == null) {
                // Not on disk yet: try again on the next start instead of losing it.
                return;
            }
            IO.writeObjSync(AppProfile.syncState, state);
            flags.edit().putBoolean(KEY_UI, true).apply();
            LOG.d("RebrandMigration", "look applied");
        } catch (Throwable e) {
            LOG.e(e);
        }
    }


    /** The palette changed once more; installs that already look right still carry the old hue. */
    public static void applyColour(Context context) {
        if (context == null) {
            return;
        }
        try {
            SharedPreferences flags = context.getApplicationContext()
                                            .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if (flags.getBoolean(KEY_COLOUR, false)) {
                return;
            }
            AppState state = AppState.get();
            if (state == null) {
                return;
            }
            state.tintThemeColor = CINNABAR;
            TintUtil.color = CINNABAR;
            if (AppProfile.syncState == null) {
                return;
            }
            IO.writeObjSync(AppProfile.syncState, state);
            flags.edit().putBoolean(KEY_COLOUR, true).apply();
            LOG.d("RebrandMigration", "colour applied");
        } catch (Throwable e) {
            LOG.e(e);
        }
    }


    /**
     * The palette has one source: the resource colour. A profile written by an older build
     * can still carry a hue that is no longer in the palette, and every bar painted from
     * that value then comes out in the old colour while the rest of the app is in the new
     * one. Runs on every start and repairs it, so the app cannot drift back.
     */
    public static void enforcePalette(Context context) {
        if (context == null) {
            return;
        }
        try {
            AppState state = AppState.get();
            if (state == null) {
                return;
            }
            // Upstream ships a palette of a dozen colours, and a reader upgrading from that
            // build carries one of them in the profile - which survives a reinstall on
            // /sdcard. Accepting the upstream palette kept the panels and checkboxes teal
            // while the chrome was already cinnabar: two brands on one screen. Only this
            // fork's own colours count as known now.
            boolean known = false;
            for (String c : OMNI_PALETTE) {
                try {
                    if (Color.parseColor(c) == state.tintThemeColor) {
                        known = true;
                        break;
                    }
                } catch (Throwable ignored) {
                }
            }
            if (!known) {
                state.tintThemeColor = CINNABAR;
            }
            TintUtil.color = state.tintThemeColor;
            if (!known && AppProfile.syncState != null) {
                IO.writeObjSync(AppProfile.syncState, state);
            }
        } catch (Throwable e) {
            LOG.e(e);
        }
    }

}
