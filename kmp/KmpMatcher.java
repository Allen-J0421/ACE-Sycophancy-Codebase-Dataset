package kmp;

import java.util.ArrayList;
import java.util.List;

public final class KmpMatcher {

    private KmpMatcher() {
        // Utility class.
    }

    public static KmpSearchResult search(KmpSearchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Search request must not be null.");
        }

        if (request.patternLength() == 0
            || request.textLength() == 0
            || request.patternLength() > request.textLength()) {
            return KmpSearchResult.of(request, new ArrayList<Integer>());
        }

        int[] lps = buildLps(request.pattern());
        List<Integer> matches = new ArrayList<>();
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < request.textLength()) {
            if (request.text().charAt(textIndex) == request.pattern().charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == request.patternLength()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex != 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return KmpSearchResult.of(request, matches);
    }

    public static List<Integer> findAllMatches(CharSequence pattern, CharSequence text) {
        return search(KmpSearchRequest.of(pattern, text)).matches();
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
}
