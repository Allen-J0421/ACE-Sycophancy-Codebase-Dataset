package com.example.kmp;

import java.util.List;
import java.util.OptionalInt;

/**
 * Immutable description of the matches found for a single search operation.
 */
public final class KmpMatchResult {
    private static final KmpMatchResult NO_MATCHES = new KmpMatchResult(List.of());

    private final List<Integer> matchIndices;

    private KmpMatchResult(List<Integer> matchIndices) {
        this.matchIndices = matchIndices;
    }

    public static KmpMatchResult from(List<Integer> matchIndices) {
        if (matchIndices.isEmpty()) {
            return NO_MATCHES;
        }

        return new KmpMatchResult(List.copyOf(matchIndices));
    }

    public static KmpMatchResult noMatches() {
        return NO_MATCHES;
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
    public String toString() {
        return matchIndices.toString();
    }
}
