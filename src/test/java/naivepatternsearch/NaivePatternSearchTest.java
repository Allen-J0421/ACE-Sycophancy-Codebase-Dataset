package naivepatternsearch;

import java.util.Arrays;
import java.util.List;

public final class NaivePatternSearchTest {

    private NaivePatternSearchTest() {
        // Test harness.
    }

    public static void main(String[] args) {
        assertMatches("aaba", "aabaacaadaabaaba", Arrays.asList(0, 9, 12));
        assertMatches("aa", "aaaa", Arrays.asList(0, 1, 2));
        assertMatches("", "abc", Arrays.asList(0, 1, 2, 3));
        assertMatches("abcd", "ab", List.of());
        assertMatches(new StringBuilder("aba"), new StringBuilder("ababa"), Arrays.asList(0, 2));
        PatternSearcher searcher = PatternSearchers.naive();
        assertUnmodifiable(searcher.search("aba", "ababa"));
        assertThrowsNullPointer(() -> searcher.search(null, "text"));
        assertThrowsNullPointer(() -> searcher.search("pattern", null));
    }

    private static void assertMatches(CharSequence pattern, CharSequence text, List<Integer> expected) {
        List<Integer> actual = PatternSearchers.naive().search(pattern, text);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertUnmodifiable(List<Integer> matches) {
        try {
            matches.add(99);
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("Expected result list to be unmodifiable");
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
