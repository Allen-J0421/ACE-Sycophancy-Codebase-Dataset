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
        assertEquals("joins match indexes for CLI output",
                "0 9 12", NaivePatternSearch.joinMatchIndexes(List.of(0, 9, 12)));
        assertSearchInput("uses CLI defaults when no arguments are provided",
                new String[0], "aabaacaadaabaaba", "aaba");
        assertSearchInput("allows overriding the text only",
                new String[] {"banana"}, "banana", "aaba");
        assertSearchInput("allows overriding both text and pattern",
                new String[] {"banana", "ana"}, "banana", "ana");
    }

    private static void assertMatches(
            String scenario, String pattern, String text, List<Integer> expectedMatches) {
        assertEquals(scenario, expectedMatches, NaivePatternSearch.search(pattern, text));
    }

    private static void assertSearchInput(
            String scenario, String[] args, String expectedText, String expectedPattern) {
        NaivePatternSearchCli.SearchInput input = NaivePatternSearchCli.parseArguments(args);
        assertEquals(scenario + " (text)", expectedText, input.text());
        assertEquals(scenario + " (pattern)", expectedPattern, input.pattern());
    }

    private static void assertEquals(String scenario, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    scenario + ": expected " + expected + " but was " + actual);
        }
    }
}
