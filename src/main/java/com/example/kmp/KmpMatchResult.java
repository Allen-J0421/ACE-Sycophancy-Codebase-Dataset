package com.example.kmp;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

/**
 * Immutable description of the matches found for a single search operation.
 */
public final class KmpMatchResult implements Iterable<Integer> {
    private final String pattern;
    private final String text;
    private final List<Integer> matchIndices;

    private KmpMatchResult(String pattern, String text, List<Integer> matchIndices) {
        this.pattern = pattern;
        this.text = text;
        this.matchIndices = matchIndices;
    }

    public static KmpMatchResult from(String pattern, String text, List<Integer> matchIndices) {
        return new KmpMatchResult(pattern, text, List.copyOf(matchIndices));
    }

    public String pattern() {
        return pattern;
    }

    public String text() {
        return text;
    }

    public List<Integer> matchIndices() {
        return matchIndices;
    }

    public int count() {
        return matchIndices.size();
    }

    public boolean hasMatches() {
        return !matchIndices.isEmpty();
    }

    public OptionalInt firstMatch() {
        return hasMatches() ? OptionalInt.of(matchIndices.get(0)) : OptionalInt.empty();
    }

    public OptionalInt lastMatch() {
        return hasMatches() ? OptionalInt.of(matchIndices.get(matchIndices.size() - 1)) : OptionalInt.empty();
    }

    @Override
    public Iterator<Integer> iterator() {
        return matchIndices.iterator();
    }

    @Override
    public String toString() {
        return matchIndices.toString();
    }
}
