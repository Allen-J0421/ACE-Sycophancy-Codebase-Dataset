package com.example.kmp;

import java.util.List;

/**
 * Immutable description of the matches found for a single search operation.
 */
public final class KmpMatchResult implements KmpMatchView {
    private final KmpPattern pattern;
    private final String text;
    private final List<Integer> matchIndices;

    private KmpMatchResult(KmpPattern pattern, String text, List<Integer> matchIndices) {
        this.pattern = pattern;
        this.text = text;
        this.matchIndices = matchIndices;
    }

    public static KmpMatchResult from(KmpPattern pattern, String text, List<Integer> matchIndices) {
        return new KmpMatchResult(pattern, text, List.copyOf(matchIndices));
    }

    @Override
    public KmpPattern pattern() {
        return pattern;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public List<Integer> matchIndices() {
        return matchIndices;
    }

    @Override
    public String toString() {
        return matchIndices.toString();
    }
}
