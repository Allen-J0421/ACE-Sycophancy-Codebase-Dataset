package com.example.kmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

/**
 * Search session for one compiled pattern and one text input.
 */
public final class KmpMatcher {
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

    public String text() {
        return text;
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

    public List<Integer> findMatches() {
        return analyze().matchIndices();
    }

    public OptionalInt findFirst() {
        return analyze().firstMatch();
    }

    public int countMatches() {
        return analyze().count();
    }

    public boolean contains() {
        return analyze().hasMatches();
    }

    public void forEachMatch(IntConsumer matchConsumer) {
        Objects.requireNonNull(matchConsumer, "matchConsumer must not be null");
        for (int matchIndex : analyze()) {
            matchConsumer.accept(matchIndex);
        }
    }
}
