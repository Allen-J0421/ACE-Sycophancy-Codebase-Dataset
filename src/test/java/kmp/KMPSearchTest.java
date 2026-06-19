package kmp;

import java.util.Arrays;
import java.util.List;

public final class KMPSearchTest {

    private KMPSearchTest() {
    }

    public static void main(String[] args) {
        assertMatches("aaba", "aabaacaadaabaaba", indexes(0, 9, 12));
        assertMatches("aa", "aaaa", indexes(0, 1, 2));
        assertMatches("a", "banana", indexes(1, 3, 5));
        assertMatches("abc", "def", indexes());
        assertMatches("abcdef", "abc", indexes());
        assertMatches("abc", "", indexes());
        assertMatches("", "", indexes(0));
        assertMatches("", "abc", indexes(0, 1, 2, 3));
        assertMatches(new StringBuilder("aba"), new StringBuilder("ababa"), indexes(0, 2));

        assertRejects("null pattern", () -> KMPSearch.search(null, "abc"));
        assertRejects("null text", () -> KMPSearch.search("abc", null));

        assertPrefixTable("aaba", 0, 1, 0, 1);
        assertPrefixTable("aaaa", 0, 1, 2, 3);
        assertPrefixTable("ababaca", 0, 0, 1, 2, 3, 0, 1);
    }

    private static void assertMatches(CharSequence pattern, CharSequence text, List<Integer> expected) {
        List<Integer> actual = KMPSearch.search(pattern, text);
        if (!actual.equals(expected)) {
            String message = "Expected matches " + expected + " for pattern '" + pattern + "' in text '"
                    + text + "', but got " + actual + ".";
            throw new AssertionError(message);
        }
    }

    private static List<Integer> indexes(Integer... values) {
        return Arrays.asList(values);
    }

    private static void assertPrefixTable(CharSequence pattern, int... expected) {
        int[] actual = PrefixTable.build(pattern);
        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError("Expected prefix table " + Arrays.toString(expected) + " for pattern '"
                    + pattern + "', but got " + Arrays.toString(actual) + ".");
        }
    }

    private static void assertRejects(String scenario, Runnable action) {
        boolean rejected = false;
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }

        if (!rejected) {
            throw new AssertionError("Expected " + scenario + " to be rejected.");
        }
    }
}
