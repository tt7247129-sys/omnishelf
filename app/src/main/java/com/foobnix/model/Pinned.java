package com.foobnix.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.foobnix.dao2.FileMeta;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Books pinned to the top of the shelf.
 *
 * <p>Deliberately kept out of the book database: adding a column to it means a schema
 * migration, and a migration that goes wrong takes the whole library with it. A separate
 * preference file can only ever lose the pins themselves, which is a failure a reader can
 * shrug at.
 *
 * <p>Paths are the key. If a book is moved its pin stops matching, and that is the correct
 * outcome - silently following the file would pin something the reader did not choose.
 */
public class Pinned {

    private static final String PREFS = "omnishelf_pinned";
    private static final String KEY = "paths";
    private static final String SEP = "\n";

    /** Pinned books, oldest first, so a newly pinned book does not jump the queue. */
    public static Set<String> getAll(Context context) {
        Set<String> result = new LinkedHashSet<String>();
        if (context == null) {
            return result;
        }
        String raw = prefs(context).getString(KEY, "");
        if (raw == null || raw.length() == 0) {
            return result;
        }
        for (String path : raw.split(SEP)) {
            if (path != null && path.length() > 0) {
                result.add(path);
            }
        }
        return result;
    }

    public static boolean isPinned(Context context, String path) {
        return path != null && getAll(context).contains(path);
    }

    public static boolean toggle(Context context, String path) {
        if (context == null || path == null) {
            return false;
        }
        Set<String> all = getAll(context);
        boolean nowPinned;
        if (all.contains(path)) {
            all.remove(path);
            nowPinned = false;
        } else {
            all.add(path);
            nowPinned = true;
        }
        save(context, all);
        return nowPinned;
    }

    public static void clear(Context context) {
        if (context != null) {
            prefs(context).edit().remove(KEY).apply();
        }
    }

    /**
     * Stable partition: pinned books first, everything else in the order it arrived.
     *
     * <p>A stable pass rather than a comparator, so the chosen sort still governs within
     * each group and nothing rearranges beyond the pins moving up.
     *
     * @return the same list instance, reordered in place
     */
    public static List<FileMeta> partition(Context context, List<FileMeta> books) {
        if (books == null || books.size() < 2) {
            return books;
        }
        Set<String> pinned = getAll(context);
        if (pinned.isEmpty()) {
            return books;
        }
        List<FileMeta> top = new ArrayList<FileMeta>();
        List<FileMeta> rest = new ArrayList<FileMeta>(books.size());
        for (FileMeta book : books) {
            if (book != null && book.getPath() != null && pinned.contains(book.getPath())) {
                top.add(book);
            } else {
                rest.add(book);
            }
        }
        if (top.isEmpty()) {
            return books;
        }
        books.clear();
        books.addAll(top);
        books.addAll(rest);
        return books;
    }

    private static void save(Context context, Set<String> paths) {
        StringBuilder builder = new StringBuilder();
        for (String path : paths) {
            if (builder.length() > 0) {
                builder.append(SEP);
            }
            builder.append(path);
        }
        prefs(context).edit().putString(KEY, builder.toString()).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                      .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
