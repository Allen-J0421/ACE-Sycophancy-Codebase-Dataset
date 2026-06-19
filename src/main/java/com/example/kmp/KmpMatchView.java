package com.example.kmp;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

/**
 * Shared read-only view of KMP matches for one pattern/text pair.
 */
public interface KmpMatchView extends Iterable<Integer> {
    String patternText();

    String text();

    List<Integer> matchIndices();

    default List<Integer> findMatches() {
        return matchIndices();
    }

    default int count() {
        return matchIndices().size();
    }

    default int countMatches() {
        return count();
    }

    default boolean hasMatches() {
        return !matchIndices().isEmpty();
    }

    default boolean contains() {
        return hasMatches();
    }

    default OptionalInt firstMatch() {
        return hasMatches() ? OptionalInt.of(matchIndices().get(0)) : OptionalInt.empty();
    }

    default OptionalInt findFirst() {
        return firstMatch();
    }

    default OptionalInt lastMatch() {
        List<Integer> matches = matchIndices();
        return matches.isEmpty() ? OptionalInt.empty() : OptionalInt.of(matches.get(matches.size() - 1));
    }

    default void forEachMatch(IntConsumer matchConsumer) {
        Objects.requireNonNull(matchConsumer, "matchConsumer must not be null");

        for (int matchIndex : matchIndices()) {
            matchConsumer.accept(matchIndex);
        }
    }

    @Override
    default Iterator<Integer> iterator() {
        return matchIndices().iterator();
    }
}
