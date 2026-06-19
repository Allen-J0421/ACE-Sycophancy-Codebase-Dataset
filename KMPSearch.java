import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
    }

    public static void main(String[] args) {
        KmpPatternSearchingDemo.main(args);
    }

    public static List<Integer> search(String pattern, String text) {
        validateInputs(pattern, text);

        if (pattern.isEmpty()) {
            return allInsertionPoints(text.length());
        }

        if (pattern.length() > text.length()) {
            return Collections.emptyList();
        }

        return findMatches(pattern, text, buildLps(pattern));
    }

    private static List<Integer> findMatches(String pattern, String text, int[] lps) {
        List<Integer> matches = new ArrayList<>();
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == pattern.length()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex > 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return matches;
    }

    private static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int prefixLength = 0;
        int index = 1;

        while (index < pattern.length()) {
            if (pattern.charAt(index) == pattern.charAt(prefixLength)) {
                lps[index] = ++prefixLength;
                index++;
            } else if (prefixLength > 0) {
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[index] = 0;
                index++;
            }
        }

        return lps;
    }

    private static List<Integer> allInsertionPoints(int textLength) {
        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index <= textLength; index++) {
            matches.add(index);
        }
        return matches;
    }

    private static void validateInputs(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }
    }
}
