package com.example.kmp;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

/**
 * Immutable compiled representation of a pattern for Knuth-Morris-Pratt searches.
 */
public final class KmpPattern {
    private final String value;
    private final int[] longestPrefixSuffix;

    private KmpPattern(String value, int[] longestPrefixSuffix) {
        this.value = value;
        this.longestPrefixSuffix = longestPrefixSuffix;
    }

    public static KmpPattern compile(CharSequence pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");

        String patternValue = pattern.toString();
        if (patternValue.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }

        return new KmpPattern(patternValue, buildLongestPrefixSuffixTable(patternValue));
    }

    public String value() {
        return value;
    }

    public KmpMatcher matcher(CharSequence text) {
        return new KmpMatcher(this, text);
    }

    public KmpMatchResult analyzeIn(CharSequence text) {
        return matcher(text).analyze();
    }

    public List<Integer> findMatchesIn(CharSequence text) {
        return matcher(text).findMatches();
    }

    public OptionalInt findFirstIn(CharSequence text) {
        return matcher(text).findFirst();
    }

    public int countMatchesIn(CharSequence text) {
        return matcher(text).countMatches();
    }

    public boolean occursIn(CharSequence text) {
        return matcher(text).contains();
    }

    public void forEachMatchIn(CharSequence text, IntConsumer matchConsumer) {
        matcher(text).forEachMatch(matchConsumer);
    }

    public KmpMatchIterator matchIteratorIn(CharSequence text) {
        return matcher(text).matchIterator();
    }

    static int[] buildLongestPrefixSuffixTable(String pattern) {
        int[] table = new int[pattern.length()];
        int prefixLength = 0;
        int currentIndex = 1;

        while (currentIndex < pattern.length()) {
            if (pattern.charAt(currentIndex) == pattern.charAt(prefixLength)) {
                table[currentIndex] = ++prefixLength;
                currentIndex++;
                continue;
            }

            if (prefixLength > 0) {
                prefixLength = table[prefixLength - 1];
            } else {
                table[currentIndex] = 0;
                currentIndex++;
            }
        }

        return table;
    }

    KmpMatchIterator newMatchIterator(CharSequence text) {
        Objects.requireNonNull(text, "text must not be null");
        return new KmpMatchIterator(value, longestPrefixSuffix, text);
    }
}
