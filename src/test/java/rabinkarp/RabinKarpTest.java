package rabinkarp;

import java.util.List;

public final class RabinKarpTest {

    private RabinKarpTest() {
        // Test harness only.
    }

    public static void main(String[] args) {
        expect(List.of(0, 8), RabinKarp.search("geeks", "geeksforgeeks"), "basic match");
        expect(List.of(0, 1, 2, 3), RabinKarp.search("", "abc"), "empty pattern");
        expect(List.of(), RabinKarp.search("abcd", "abc"), "pattern longer than text");
        expect(List.of(), RabinKarp.search("xyz", "abcdef"), "no match");
        expect(
                List.of(0, 8),
                RabinKarp.search(new StringBuilder("geeks"), new StringBuilder("geeksforgeeks")),
                "char sequence input");
        expect(
                List.of(0, 8),
                new RabinKarpMatcher(256, 101).search("geeks", "geeksforgeeks"),
                "custom matcher");
    }

    private static void expect(List<Integer> expected, List<Integer> actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    label + " failed: expected " + expected + " but got " + actual);
        }
    }
}
