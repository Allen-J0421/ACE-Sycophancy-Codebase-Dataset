import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class KmpSearch {

    private KmpSearch() {
        // Utility class.
    }

    /**
     * Returns the zero-based starting indices of every occurrence of {@code pattern} in {@code text}.
     *
     * @param pattern the non-empty pattern to search for
     * @param text the text to search within
     * @return a list of match starting indices in ascending order
     * @throws NullPointerException if {@code pattern} or {@code text} is null
     * @throws IllegalArgumentException if {@code pattern} is empty
     */
    public static List<Integer> search(String pattern, String text) {
        validateInputs(pattern, text);

        if (pattern.length() > text.length()) {
            return List.of();
        }

        int[] longestPrefixSuffix = buildLongestPrefixSuffixTable(pattern);
        List<Integer> matches = new ArrayList<>();

        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == pattern.length()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = longestPrefixSuffix[patternIndex - 1];
                }
                continue;
            }

            if (patternIndex > 0) {
                patternIndex = longestPrefixSuffix[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return matches;
    }

    static int[] buildLongestPrefixSuffixTable(String pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");

        int[] longestPrefixSuffix = new int[pattern.length()];
        int prefixLength = 0;
        int currentIndex = 1;

        while (currentIndex < pattern.length()) {
            if (pattern.charAt(currentIndex) == pattern.charAt(prefixLength)) {
                longestPrefixSuffix[currentIndex] = ++prefixLength;
                currentIndex++;
                continue;
            }

            if (prefixLength > 0) {
                prefixLength = longestPrefixSuffix[prefixLength - 1];
            } else {
                longestPrefixSuffix[currentIndex] = 0;
                currentIndex++;
            }
        }

        return longestPrefixSuffix;
    }

    private static void validateInputs(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern must not be null");
        Objects.requireNonNull(text, "text must not be null");

        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
    }
}
