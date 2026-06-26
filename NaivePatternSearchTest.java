import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NaivePatternSearchTest {

    private NaivePatternSearchTest() {
        // Test harness.
    }

    public static void main(String[] args) {
        assertMatches("aaba", "aabaacaadaabaaba", Arrays.asList(0, 9, 12));
        assertMatches("aa", "aaaa", Arrays.asList(0, 1, 2));
        assertMatches("", "abc", Arrays.asList(0, 1, 2, 3));
        assertMatches("abcd", "ab", Collections.emptyList());
        assertThrowsNullPointer(() -> NaivePatternSearch.search(null, "text"));
        assertThrowsNullPointer(() -> NaivePatternSearch.search("pattern", null));
    }

    private static void assertMatches(String pattern, String text, List<Integer> expected) {
        List<Integer> actual = NaivePatternSearch.search(pattern, text);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrowsNullPointer(Runnable action) {
        try {
            action.run();
        } catch (NullPointerException expected) {
            return;
        }
        throw new AssertionError("Expected NullPointerException");
    }
}
