import java.util.List;

public final class NaivePatternSearchTest {

    private NaivePatternSearchTest() {
    }

    public static void main(String[] args) {
        assertMatches("finds repeated pattern matches",
                "aaba", "aabaacaadaabaaba", List.of(0, 9, 12));
        assertMatches("returns no matches when pattern is absent",
                "xyz", "aabaacaadaabaaba", List.of());
        assertMatches("returns no matches when pattern is longer than text",
                "longpattern", "short", List.of());
        assertMatches("supports empty patterns consistently",
                "", "abc", List.of(0, 1, 2, 3));
        assertEquals("formats matches for CLI output",
                "0 9 12", NaivePatternSearch.formatMatches(List.of(0, 9, 12)));
    }

    private static void assertMatches(
            String scenario, String pattern, String text, List<Integer> expectedMatches) {
        assertEquals(scenario, expectedMatches, NaivePatternSearch.search(pattern, text));
    }

    private static void assertEquals(String scenario, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    scenario + ": expected " + expected + " but was " + actual);
        }
    }
}
