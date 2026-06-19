package kmp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class KmpMatcher {

    private KmpMatcher() {
        // Utility class.
    }

    static List<Integer> findAllMatches(CharSequence pattern, CharSequence text) {
        SearchInput input = SearchInput.of(pattern, text);
        if (input.isTriviallyEmpty()) {
            return Collections.emptyList();
        }

        int[] lps = buildLps(input.pattern);
        List<Integer> matches = new ArrayList<Integer>();
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < input.textLength) {
            if (input.text.charAt(textIndex) == input.pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == input.patternLength) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex != 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return Collections.unmodifiableList(matches);
    }

    private static int[] buildLps(CharSequence pattern) {
        int[] lps = new int[pattern.length()];
        int prefixLength = 0;

        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(prefixLength)) {
                lps[i++] = ++prefixLength;
            } else if (prefixLength != 0) {
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }

    private static final class SearchInput {
        private final CharSequence pattern;
        private final CharSequence text;
        private final int patternLength;
        private final int textLength;

        private SearchInput(CharSequence pattern, CharSequence text) {
            this.pattern = pattern;
            this.text = text;
            this.patternLength = pattern.length();
            this.textLength = text.length();
        }

        private static SearchInput of(CharSequence pattern, CharSequence text) {
            if (pattern == null || text == null) {
                throw new IllegalArgumentException("Pattern and text must not be null.");
            }

            return new SearchInput(pattern, text);
        }

        private boolean isTriviallyEmpty() {
            return patternLength == 0 || textLength == 0 || patternLength > textLength;
        }
    }
}
