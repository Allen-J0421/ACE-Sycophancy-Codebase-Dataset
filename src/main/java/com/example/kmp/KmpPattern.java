package com.example.kmp;

import java.util.ArrayList;
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

    public KmpMatchIterator matchIteratorIn(CharSequence text) {
        validateText(text);
        return new KmpMatchIterator(value, longestPrefixSuffix, text);
    }

    public KmpMatchResult analyzeIn(CharSequence text) {
        List<Integer> matches = new ArrayList<>();
        KmpMatchIterator iterator = matchIteratorIn(text);
        iterator.forEachRemaining((int matchIndex) -> matches.add(matchIndex));

        if (matches.isEmpty()) {
            return KmpMatchResult.noMatches();
        }

        return KmpMatchResult.from(matches);
    }

    public List<Integer> findMatchesIn(CharSequence text) {
        return analyzeIn(text).matchIndices();
    }

    public OptionalInt findFirstIn(CharSequence text) {
        KmpMatchIterator iterator = matchIteratorIn(text);
        return iterator.hasNext() ? OptionalInt.of(iterator.nextInt()) : OptionalInt.empty();
    }

    public int countMatchesIn(CharSequence text) {
        int matchCount = 0;
        KmpMatchIterator iterator = matchIteratorIn(text);

        while (iterator.hasNext()) {
            iterator.nextInt();
            matchCount++;
        }

        return matchCount;
    }

    public boolean occursIn(CharSequence text) {
        return matchIteratorIn(text).hasNext();
    }

    public void forEachMatchIn(CharSequence text, IntConsumer matchConsumer) {
        Objects.requireNonNull(matchConsumer, "matchConsumer must not be null");
        matchIteratorIn(text).forEachRemaining(matchConsumer);
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

    private void validateText(CharSequence text) {
        Objects.requireNonNull(text, "text must not be null");
    }
}
