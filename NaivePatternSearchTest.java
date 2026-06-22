import java.util.List;

public final class NaivePatternSearchTest {
    private NaivePatternSearchTest() {
    }

    public static void main(String[] args) {
        findsExpectedMatches();
        preservesLegacySearchMethod();
        formatsMatchOutputWithLegacyTrailingSpace();
        rendersDefaultDemoOutput();
        rejectsNullPattern();
        rejectsNullText();
        rejectsNullMatchFormatting();
    }

    private static void findsExpectedMatches() {
        List<SearchCase> cases = List.of(
                new SearchCase("sample matches", "aaba", "aabaacaadaabaaba", List.of(0, 9, 12)),
                new SearchCase("overlapping matches", "aa", "aaaa", List.of(0, 1, 2)),
                new SearchCase("empty pattern", "", "abc", List.of(0, 1, 2, 3)),
                new SearchCase("pattern longer than text", "abcd", "abc", List.of()));

        for (SearchCase testCase : cases) {
            assertMatches(
                    testCase.name(),
                    NaivePatternSearch.findOccurrences(testCase.pattern(), testCase.text()),
                    testCase.expectedMatches());
        }
    }

    private static void preservesLegacySearchMethod() {
        assertMatches(
                "legacy search method",
                NaivePatternSearch.search("aba", "ababa"),
                List.of(0, 2));
    }

    private static void formatsMatchOutputWithLegacyTrailingSpace() {
        assertEquals("0 9 12 ", MatchFormatter.format(List.of(0, 9, 12)));
    }

    private static void rendersDefaultDemoOutput() {
        assertEquals("0 9 12 ", NaivePatternSearchDemo.defaultOutput());
    }

    private static void rejectsNullPattern() {
        assertThrows(
                "null pattern",
                NullPointerException.class,
                () -> NaivePatternSearch.findOccurrences(null, "abc"));
    }

    private static void rejectsNullText() {
        assertThrows(
                "null text",
                NullPointerException.class,
                () -> NaivePatternSearch.findOccurrences("abc", null));
    }

    private static void rejectsNullMatchFormatting() {
        assertThrows(
                "null matches",
                NullPointerException.class,
                () -> MatchFormatter.format(null));
    }

    private static void assertMatches(String name, List<Integer> actual, List<Integer> expected) {
        assertEquals(name, expected, actual);
    }

    private static void assertEquals(String expected, String actual) {
        assertEquals("string value", expected, actual);
    }

    private static void assertEquals(String name, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + ": expected " + expected + " but found " + actual);
        }
    }

    private static void assertThrows(String name, Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }

            throw new AssertionError(
                    name + ": expected " + expectedType.getSimpleName()
                            + " but found " + exception.getClass().getSimpleName(),
                    exception);
        }

        throw new AssertionError(name + ": expected " + expectedType.getSimpleName());
    }

    private record SearchCase(String name, String pattern, String text, List<Integer> expectedMatches) {
    }
}
