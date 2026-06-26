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
        assertSearchResult("search result keeps the originating request",
                new SearchRequest("banana", "ana"), List.of(1, 3));
        assertImmutability("search results expose an immutable match list",
                new SearchRequest("aaaa", "aa"));
        assertFormattedOutput("search output formatting uses match indexes",
                new SearchRequest("banana", "ana"), "1 3");
        assertSearchInput("uses CLI defaults when no arguments are provided",
                new String[0], NaivePatternSearchCli.defaultRequest().text(),
                NaivePatternSearchCli.defaultRequest().pattern());
        assertSearchInput("allows overriding the text only",
                new String[] {"banana"}, "banana", "aaba");
        assertSearchInput("allows overriding both text and pattern",
                new String[] {"banana", "ana"}, "banana", "ana");
        assertCliOutput("cli renders output from raw args",
                new String[] {"banana", "ana"}, "1 3");
    }

    private static void assertMatches(
            String scenario, String pattern, String text, List<Integer> expectedMatches) {
        assertEquals(scenario, expectedMatches, NaivePatternSearch.search(pattern, text));
    }

    private static void assertSearchResult(
            String scenario, SearchRequest request, List<Integer> expectedMatches) {
        SearchResult result = NaivePatternSearch.search(request);
        assertEquals(scenario + " (request)", request, result.request());
        assertEquals(scenario + " (matches)", expectedMatches, result.matchIndexes());
        assertEquals(scenario + " (hasMatches)", !expectedMatches.isEmpty(), result.hasMatches());
    }

    private static void assertImmutability(String scenario, SearchRequest request) {
        SearchResult result = NaivePatternSearch.search(request);

        try {
            result.matchIndexes().add(99);
            throw new AssertionError(scenario + ": expected matchIndexes to be immutable");
        } catch (UnsupportedOperationException expected) {
        }
    }

    private static void assertFormattedOutput(
            String scenario, SearchRequest request, String expectedOutput) {
        SearchResult result = NaivePatternSearch.search(request);
        assertEquals(scenario, expectedOutput, NaivePatternSearch.format(result));
    }

    private static void assertSearchInput(
            String scenario, String[] args, String expectedText, String expectedPattern) {
        SearchRequest input = NaivePatternSearchCli.parseArguments(args);
        assertEquals(scenario + " (text)", expectedText, input.text());
        assertEquals(scenario + " (pattern)", expectedPattern, input.pattern());
    }

    private static void assertCliOutput(
            String scenario, String[] args, String expectedOutput) {
        SearchRequest request = NaivePatternSearchCli.parseArguments(args);
        SearchResult result = NaivePatternSearch.search(request);
        assertEquals(scenario, expectedOutput, NaivePatternSearch.format(result));
    }

    private static void assertEquals(String scenario, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    scenario + ": expected " + expected + " but was " + actual);
        }
    }
}
