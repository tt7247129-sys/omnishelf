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
}
