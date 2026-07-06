package rabinkarp;

import java.util.List;

public final class RabinKarpTest {
    private RabinKarpTest() {}

    public static void main(String[] args) {
        expect(RabinKarp.search("geeks", "geeksforgeeks"), List.of(0, 8), "geeks in geeksforgeeks");
        expect(RabinKarp.search("aa", "aaa"), List.of(0, 1), "aa in aaa");
        expect(RabinKarp.search("abc", "abcabc"), List.of(0, 3), "abc in abcabc");
        expect(RabinKarp.search("z", "abcdef"), List.of(), "no match");
        expect(RabinKarp.search("a", "a"), List.of(0), "single char exact match");

        RabinKarpPattern compiled = RabinKarpPattern.compile("geeks");
        expect(compiled.searchIn("geeksforgeeks"), List.of(0, 8), "compiled pattern: geeks in geeksforgeeks");

        System.out.println("All tests passed.");
    }

    private static void expect(List<Integer> actual, List<Integer> expected, String label) {
        if (!actual.equals(expected)) {
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
        }
    }
}
