package com.example.kmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Search session for one compiled pattern and one text input.
 */
public final class KmpMatcher implements KmpMatchView {
    private final KmpPattern pattern;
    private final String text;

    private KmpMatchResult cachedAnalysis;

    KmpMatcher(KmpPattern pattern, CharSequence text) {
        this.pattern = Objects.requireNonNull(pattern, "pattern must not be null");
        this.text = Objects.requireNonNull(text, "text must not be null").toString();
    }

    public KmpPattern pattern() {
        return pattern;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public String patternText() {
        return pattern.value();
    }

    public KmpMatchIterator matchIterator() {
        return pattern.newMatchIterator(text);
    }

    public KmpMatchResult analyze() {
        if (cachedAnalysis == null) {
            List<Integer> matches = new ArrayList<>();
            KmpMatchIterator iterator = matchIterator();
            iterator.forEachRemaining((int matchIndex) -> matches.add(matchIndex));
            cachedAnalysis = KmpMatchResult.from(pattern.value(), text, matches);
        }

        return cachedAnalysis;
    }

    @Override
    public List<Integer> findMatches() {
        return analyze().matchIndices();
    }

    @Override
    public List<Integer> matchIndices() {
        return analyze().matchIndices();
    }
}
