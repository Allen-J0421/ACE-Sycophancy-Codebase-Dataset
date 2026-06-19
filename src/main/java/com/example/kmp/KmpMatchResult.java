package com.example.kmp;

import java.util.List;

/**
 * Immutable description of the matches found for a single search operation.
 */
public final class KmpMatchResult implements KmpMatchView {
    private final String patternText;
    private final String text;
    private final List<Integer> matchIndices;

    private KmpMatchResult(String patternText, String text, List<Integer> matchIndices) {
        this.patternText = patternText;
        this.text = text;
        this.matchIndices = matchIndices;
    }

    public static KmpMatchResult from(String patternText, String text, List<Integer> matchIndices) {
        return new KmpMatchResult(patternText, text, List.copyOf(matchIndices));
    }

    @Override
    public String patternText() {
        return patternText;
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
