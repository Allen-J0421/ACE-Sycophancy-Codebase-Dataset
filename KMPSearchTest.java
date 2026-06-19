import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class KMPSearchTest {

    private KMPSearchTest() {
    }

    public static void main(String[] args) {
        assertMatches("aaba", "aabaacaadaabaaba", Arrays.asList(0, 9, 12));
        assertMatches("aa", "aaaa", Arrays.asList(0, 1, 2));
        assertMatches("abc", "def", Collections.emptyList());
        assertMatches("abcdef", "abc", Collections.emptyList());
        assertMatches("", "abc", Arrays.asList(0, 1, 2, 3));
        assertRejectsNullInput();
    }

    private static void assertMatches(String pattern, String text, List<Integer> expected) {
        List<Integer> actual = KMPSearch.search(pattern, text);
        if (!actual.equals(expected)) {
            String message = "Expected matches " + expected + " for pattern '" + pattern + "' in text '"
                    + text + "', but got " + actual + ".";
            throw new AssertionError(message);
        }
    }

    private static void assertRejectsNullInput() {
        try {
            KMPSearch.search(null, "abc");
            throw new AssertionError("Expected null pattern to be rejected.");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }

        try {
            KMPSearch.search("abc", null);
            throw new AssertionError("Expected null text to be rejected.");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }
}
