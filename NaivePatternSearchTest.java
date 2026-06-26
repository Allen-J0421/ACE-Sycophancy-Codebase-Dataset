import java.util.List;

public final class NaivePatternSearchTest {

    private static final SearchApplication APPLICATION = SearchApplication.createDefault();

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
        assertApplicationOutput("application formats the search output",
                new SearchRequest("banana", "ana"), "1 3");
        assertApplicationSearch("application returns the same search result contract",
                new SearchRequest("banana", "ana"), List.of(1, 3));
        assertSearchInput("uses CLI defaults when no arguments are provided",
                new String[0], "aabaacaadaabaaba", "aaba");
        assertSearchInput("allows overriding the text only",
                new String[] {"banana"}, "banana", "aaba");
        assertSearchInput("allows overriding both text and pattern",
                new String[] {"banana", "ana"}, "banana", "ana");
        assertApplicationRunFromArgs("application can execute directly from CLI args",
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

    private static void assertApplicationOutput(
            String scenario, SearchRequest request, String expectedOutput) {
        assertEquals(scenario, expectedOutput, APPLICATION.run(request));
    }

    private static void assertApplicationSearch(
            String scenario, SearchRequest request, List<Integer> expectedMatches) {
        SearchResult result = APPLICATION.search(request);
        assertEquals(scenario + " (request)", request, result.request());
        assertEquals(scenario + " (matches)", expectedMatches, result.matchIndexes());
    }

    private static void assertApplicationRunFromArgs(
            String scenario, String[] args, String expectedOutput) {
        assertEquals(scenario, expectedOutput, APPLICATION.run(args));
    }

    private static void assertSearchInput(
            String scenario, String[] args, String expectedText, String expectedPattern) {
        SearchRequest input = NaivePatternSearchCli.parseArguments(args);
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
