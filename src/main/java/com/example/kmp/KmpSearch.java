package com.example.kmp;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

public final class KmpSearch {

    private KmpSearch() {
        // Utility class.
    }

    /**
     * Returns the zero-based starting indices of every occurrence of {@code pattern} in {@code text}.
     *
     * @param pattern the non-empty pattern to search for
     * @param text the text to search within
     * @return a list of match starting indices in ascending order
     * @throws NullPointerException if {@code pattern} or {@code text} is null
     * @throws IllegalArgumentException if {@code pattern} is empty
     */
    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        return matcher(pattern, text).findMatches();
    }

    public static KmpMatchResult analyze(CharSequence pattern, CharSequence text) {
        return matcher(pattern, text).analyze();
    }

    public static KmpMatcher matcher(CharSequence pattern, CharSequence text) {
        return compile(pattern).matcher(text);
    }

    public static KmpMatchIterator matchIterator(CharSequence pattern, CharSequence text) {
        return matcher(pattern, text).matchIterator();
    }

    public static OptionalInt findFirst(CharSequence pattern, CharSequence text) {
        return matcher(pattern, text).findFirst();
    }

    public static int countMatches(CharSequence pattern, CharSequence text) {
        return matcher(pattern, text).countMatches();
    }

    public static boolean contains(CharSequence pattern, CharSequence text) {
        return matcher(pattern, text).contains();
    }

    public static void forEachMatch(CharSequence pattern, CharSequence text, IntConsumer matchConsumer) {
        matcher(pattern, text).forEachMatch(matchConsumer);
    }

    public static KmpPattern compile(CharSequence pattern) {
        return KmpPattern.compile(pattern);
    }
}
