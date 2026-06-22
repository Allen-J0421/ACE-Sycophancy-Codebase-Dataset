import java.util.Arrays;
import java.util.List;

public final class NaivePatternSearchTest {
    private NaivePatternSearchTest() {
    }

    public static void main(String[] args) {
        findsSampleMatches();
        findsOverlappingMatches();
        returnsEveryPositionForEmptyPattern();
        returnsNoMatchesWhenPatternIsLongerThanText();
        preservesLegacySearchMethod();
    }

    private static void findsSampleMatches() {
        assertMatches(
                NaivePatternSearch.findOccurrences("aaba", "aabaacaadaabaaba"),
                0, 9, 12);
    }

    private static void findsOverlappingMatches() {
        assertMatches(
                NaivePatternSearch.findOccurrences("aa", "aaaa"),
                0, 1, 2);
    }

    private static void returnsEveryPositionForEmptyPattern() {
        assertMatches(
                NaivePatternSearch.findOccurrences("", "abc"),
                0, 1, 2, 3);
    }

    private static void returnsNoMatchesWhenPatternIsLongerThanText() {
        assertMatches(NaivePatternSearch.findOccurrences("abcd", "abc"));
    }

    private static void preservesLegacySearchMethod() {
        assertMatches(
                NaivePatternSearch.search("aba", "ababa"),
                0, 2);
    }

    private static void assertMatches(List<Integer> actual, Integer... expected) {
        List<Integer> expectedMatches = Arrays.asList(expected);

        if (!actual.equals(expectedMatches)) {
            throw new AssertionError("Expected " + expectedMatches + " but found " + actual);
        }
    }
}
