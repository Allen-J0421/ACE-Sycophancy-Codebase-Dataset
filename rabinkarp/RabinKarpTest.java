package rabinkarp;

import java.util.List;

public final class RabinKarpTest {
    private RabinKarpTest() {}

    public static void main(String[] args) {
        runSuite(StringMatcherFactory.rabinKarp(), "RabinKarpMatcher");
        runSuite(StringMatcherFactory.naive(), "NaiveMatcher");

        RabinKarpPattern compiled = RabinKarpPattern.compile("geeks");
        expect(compiled.searchIn("geeksforgeeks"), List.of(0, 8), "compiled pattern: geeks in geeksforgeeks");

        System.out.println("All tests passed.");
    }

    private static void runSuite(StringMatcher matcher, String label) {
        expect(matcher.search("geeks", "geeksforgeeks"), List.of(0, 8),  label + ": geeks in geeksforgeeks");
        expect(matcher.search("aa",    "aaa"),            List.of(0, 1),  label + ": aa in aaa");
        expect(matcher.search("abc",   "abcabc"),         List.of(0, 3),  label + ": abc in abcabc");
        expect(matcher.search("z",     "abcdef"),         List.of(),      label + ": no match");
        expect(matcher.search("a",     "a"),              List.of(0),     label + ": single char exact match");
    }

    private static void expect(List<Integer> actual, List<Integer> expected, String label) {
        if (!actual.equals(expected)) {
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
        }
    }
}
