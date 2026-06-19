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
    private final CharSequence text;

    private KmpMatchResult cachedAnalysis;

    KmpMatcher(KmpPattern pattern, CharSequence text) {
        this.pattern = Objects.requireNonNull(pattern, "pattern must not be null");
        this.text = Objects.requireNonNull(text, "text must not be null");
    }

    public KmpPattern pattern() {
        return pattern;
    }

    public CharSequence text() {
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
            cachedAnalysis = matches.isEmpty() ? KmpMatchResult.noMatches() : KmpMatchResult.from(matches);
        }

        return cachedAnalysis;
    }

    public List<Integer> findMatches() {
        return analyze().matchIndices();
    }

    public OptionalInt findFirst() {
        KmpMatchIterator iterator = matchIterator();
        return iterator.hasNext() ? OptionalInt.of(iterator.nextInt()) : OptionalInt.empty();
    }

    public int countMatches() {
        int matchCount = 0;
        KmpMatchIterator iterator = matchIterator();

        while (iterator.hasNext()) {
            iterator.nextInt();
            matchCount++;
        }

        return matchCount;
    }

    public boolean contains() {
        return matchIterator().hasNext();
    }

    public void forEachMatch(IntConsumer matchConsumer) {
        Objects.requireNonNull(matchConsumer, "matchConsumer must not be null");
        matchIterator().forEachRemaining(matchConsumer);
    }
}
