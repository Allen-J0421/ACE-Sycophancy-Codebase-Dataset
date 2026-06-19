import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable compiled representation of a pattern for Knuth-Morris-Pratt searches.
 */
public final class KmpPattern {
    private final String value;
    private final int[] longestPrefixSuffix;

    private KmpPattern(String value, int[] longestPrefixSuffix) {
        this.value = value;
        this.longestPrefixSuffix = longestPrefixSuffix;
    }

    public static KmpPattern compile(String pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");

        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }

        return new KmpPattern(pattern, buildLongestPrefixSuffixTable(pattern));
    }

    public String value() {
        return value;
    }

    public List<Integer> findMatchesIn(String text) {
        Objects.requireNonNull(text, "text must not be null");

        if (value.length() > text.length()) {
            return List.of();
        }

        List<Integer> matches = new ArrayList<>();
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == value.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == value.length()) {
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

    public boolean occursIn(String text) {
        return !findMatchesIn(text).isEmpty();
    }

    static int[] buildLongestPrefixSuffixTable(String pattern) {
        int[] table = new int[pattern.length()];
        int prefixLength = 0;
        int currentIndex = 1;

        while (currentIndex < pattern.length()) {
            if (pattern.charAt(currentIndex) == pattern.charAt(prefixLength)) {
                table[currentIndex] = ++prefixLength;
                currentIndex++;
                continue;
            }

            if (prefixLength > 0) {
                prefixLength = table[prefixLength - 1];
            } else {
                table[currentIndex] = 0;
                currentIndex++;
            }
        }

        return table;
    }
}
