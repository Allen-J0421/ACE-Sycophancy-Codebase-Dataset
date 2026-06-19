import java.util.Arrays;
import java.util.List;

public final class RabinKarpTest {

    private RabinKarpTest() {
    }

    public static void main(String[] args) {
        assertMatches("finds repeated matches", "geeks", "geeksforgeeks", 0, 8);
        assertMatches("finds overlapping matches", "aa", "aaaa", 0, 1, 2);
        assertMatches("supports empty patterns", "", "abc", 0, 1, 2, 3);
        assertMatches("returns no matches when pattern is longer", "abcd", "abc");
        assertMatches(
            "accepts non-string CharSequence inputs",
            new StringBuilder("aba"),
            new StringBuffer("ababa"),
            0,
            2
        );
    }

    private static void assertMatches(
        String scenario,
        CharSequence pattern,
        CharSequence text,
        Integer... expected
    ) {
        List<Integer> actual = RabinKarp.search(pattern, text);
        List<Integer> expectedMatches = Arrays.asList(expected);
        if (!actual.equals(expectedMatches)) {
            throw new AssertionError(
                scenario + ": expected " + expectedMatches + " but got " + actual
            );
        }
    }
}
