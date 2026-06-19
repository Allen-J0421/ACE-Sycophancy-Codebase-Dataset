import java.util.ArrayList;
import java.util.List;

/**
 * Knuth–Morris–Pratt (KMP) substring search.
 *
 * <p>Finds every starting index at which a pattern occurs in a text in
 * {@code O(n + m)} time, where {@code n} is the text length and {@code m} is the
 * pattern length. This is achieved by precomputing the pattern's
 * "longest proper prefix which is also a suffix" (LPS) table, which lets the
 * search skip characters that are already known to match instead of
 * backtracking in the text.
 *
 * <p>This is a stateless utility class; it is not meant to be instantiated.
 */
final class KMPSearch {

    private KMPSearch() {
        // Utility class: prevent instantiation.
    }

    /**
     * Returns the starting indices of every occurrence of {@code pattern} in
     * {@code text}. Overlapping occurrences are reported.
     *
     * @param pattern the (non-null) substring to search for
     * @param text    the (non-null) text to search within
     * @return an immutable-in-spirit list of zero-based start indices, in
     *         ascending order; empty if there are no matches or if
     *         {@code pattern} is empty
     * @throws NullPointerException if {@code pattern} or {@code text} is null
     */
    static List<Integer> search(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new NullPointerException("pattern and text must not be null");
        }

        List<Integer> matches = new ArrayList<>();
        int patternLength = pattern.length();
        int textLength = text.length();

        // An empty pattern has no meaningful occurrences; the pattern also
        // cannot fit if it is longer than the text.
        if (patternLength == 0 || patternLength > textLength) {
            return matches;
        }

        int[] lps = computeLpsTable(pattern);

        int textIndex = 0;     // current position in the text
        int patternIndex = 0;  // current position in the pattern
        while (textIndex < textLength) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
                if (patternIndex == patternLength) {
                    // Full match: record its start and resume using the LPS
                    // table so overlapping matches are not missed.
                    matches.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex != 0) {
                // Mismatch after some matches: fall back in the pattern only.
                patternIndex = lps[patternIndex - 1];
            } else {
                // Mismatch at the start of the pattern: advance the text.
                textIndex++;
            }
        }
        return matches;
    }

    /**
     * Builds the longest-proper-prefix-suffix (LPS) table for {@code pattern}.
     *
     * <p>{@code lps[i]} holds the length of the longest proper prefix of
     * {@code pattern[0..i]} that is also a suffix of {@code pattern[0..i]}.
     *
     * @param pattern a non-empty pattern
     * @return the LPS table, one entry per pattern character
     */
    private static int[] computeLpsTable(String pattern) {
        int length = pattern.length();
        int[] lps = new int[length];

        int prefixLength = 0; // length of the current matching prefix
        int i = 1;            // lps[0] is always 0, so start at 1
        while (i < length) {
            if (pattern.charAt(i) == pattern.charAt(prefixLength)) {
                prefixLength++;
                lps[i] = prefixLength;
                i++;
            } else if (prefixLength != 0) {
                // Fall back to the previous best prefix and retry without
                // advancing i.
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = search(pattern, text);
        System.out.println("Pattern \"" + pattern + "\" found at indices: "
                + formatIndices(matches));
    }

    /** Renders the match indices as a space-separated string for display. */
    private static String formatIndices(List<Integer> indices) {
        StringBuilder sb = new StringBuilder();
        for (int index : indices) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(index);
        }
        return sb.toString();
    }
}
