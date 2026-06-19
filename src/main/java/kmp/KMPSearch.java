package kmp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
    }

    public static void main(String[] args) {
        KmpPatternSearchingDemo.main(args);
    }

    /**
     * Returns every zero-based position where {@code pattern} appears in {@code text}.
     * Empty patterns match every insertion point from {@code 0} through {@code text.length()}.
     */
    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        validateInputs(pattern, text);

        if (pattern.length() == 0) {
            return allInsertionPoints(text.length());
        }

        if (pattern.length() > text.length()) {
            return Collections.emptyList();
        }

        return findMatches(pattern, text, PrefixTable.build(pattern));
    }

    private static List<Integer> findMatches(CharSequence pattern, CharSequence text, int[] prefixTable) {
        List<Integer> matches = new ArrayList<>();
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == pattern.length()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = prefixTable[patternIndex - 1];
                }
            } else if (patternIndex > 0) {
                patternIndex = prefixTable[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return matches;
    }

    private static List<Integer> allInsertionPoints(int textLength) {
        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index <= textLength; index++) {
            matches.add(index);
        }
        return matches;
    }

    private static void validateInputs(CharSequence pattern, CharSequence text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }
    }
}
