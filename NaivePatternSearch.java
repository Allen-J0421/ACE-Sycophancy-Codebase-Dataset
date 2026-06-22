import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Naive (brute-force) string pattern searching.
 *
 * <p>Scans the text from left to right and, at every candidate starting
 * position, compares the pattern character by character. Runs in O(n * m)
 * time for a text of length {@code n} and a pattern of length {@code m}.
 */
public class NaivePatternSearch {

    /**
     * Finds every starting index in {@code text} where {@code pattern} occurs.
     *
     * @param pattern the substring to search for
     * @param text    the text to search within
     * @return an immutable list of zero-based starting indices, in ascending
     *         order; empty if the pattern does not occur
     * @throws NullPointerException     if {@code pattern} or {@code text} is null
     * @throws IllegalArgumentException if {@code pattern} is empty
     */
    static List<Integer> search(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new NullPointerException("pattern and text must not be null");
        }
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }

        int patternLength = pattern.length();
        int textLength = text.length();
        List<Integer> matches = new ArrayList<>();

        for (int start = 0; start <= textLength - patternLength; start++) {
            if (matchesAt(pattern, text, start)) {
                matches.add(start);
            }
        }

        return Collections.unmodifiableList(matches);
    }

    /**
     * Returns whether {@code pattern} matches {@code text} starting at
     * {@code start}. The caller guarantees the window fits within the text.
     */
    private static boolean matchesAt(String pattern, String text, int start) {
        for (int offset = 0; offset < pattern.length(); offset++) {
            if (text.charAt(start + offset) != pattern.charAt(offset)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = search(pattern, text);

        System.out.println("Pattern \"" + pattern + "\" found at indices: " + matches);
    }
}
