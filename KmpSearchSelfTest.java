import java.util.List;
import java.util.OptionalInt;

public final class KmpSearchSelfTest {

    private KmpSearchSelfTest() {
    }

    public static void main(String[] args) {
        assertEquals(List.of(0, 9, 12), KmpSearch.search("aaba", "aabaacaadaabaaba"), "basic search");
        assertEquals(List.of(0, 1, 2), KmpSearch.search("aa", "aaaa"), "overlapping matches");
        assertEquals(List.of(), KmpSearch.search("xyz", "aaaa"), "no matches");
        assertEquals(List.of(), KmpSearch.search("longer-pattern", "short"), "pattern longer than text");
        assertEquals(OptionalInt.of(2), KmpSearch.findFirst("aaba", "xxaabaacaadaabaaba"), "first match through facade");
        assertEquals(3, KmpSearch.countMatches("aa", "aaaa"), "count matches through facade");
        assertTrue(KmpSearch.contains("aba", "xxabaxx"), "contains through facade");
        assertFalse(KmpSearch.contains("aba", "xxxx"), "contains through facade negative");
        assertEquals(List.of(0, 2), collectMatches("aba", "ababa"), "forEachMatch through facade");

        KmpPattern compiledPattern = KmpPattern.compile("aba");
        assertEquals("aba", compiledPattern.value(), "compiled pattern preserves source value");
        assertEquals(List.of(0, 2), compiledPattern.findMatchesIn("ababa"), "compiled pattern search");
        assertEquals(List.of(0, 2), compiledPattern.findMatchesIn(new StringBuilder("ababa")), "char sequence search");
        assertEquals(OptionalInt.of(0), compiledPattern.findFirstIn("ababa"), "first match search");
        assertEquals(OptionalInt.empty(), compiledPattern.findFirstIn("xxxx"), "first match no result");
        assertEquals(2, compiledPattern.countMatchesIn("ababa"), "count matches");
        assertTrue(compiledPattern.occursIn("xxabaxx"), "occursIn should detect matches");
        assertFalse(compiledPattern.occursIn("xxxx"), "occursIn should reject missing matches");
        assertEquals(List.of(0, 2), collectMatches(compiledPattern, "ababa"), "forEachMatchIn enumerates matches");
        assertThrows(UnsupportedOperationException.class, () -> compiledPattern.findMatchesIn("ababa").add(99), "match list is immutable");

        assertThrows(IllegalArgumentException.class, () -> KmpPattern.compile(""), "empty pattern rejected");
        assertThrows(NullPointerException.class, () -> KmpPattern.compile(null), "null pattern rejected");
        assertThrows(NullPointerException.class, () -> compiledPattern.findMatchesIn(null), "null text rejected");
        assertThrows(NullPointerException.class, () -> compiledPattern.forEachMatchIn("ababa", null), "null match consumer rejected");
    }

    private static List<Integer> collectMatches(KmpPattern pattern, CharSequence text) {
        List<Integer> matches = new java.util.ArrayList<>();
        pattern.forEachMatchIn(text, matches::add);
        return List.copyOf(matches);
    }

    private static List<Integer> collectMatches(CharSequence pattern, CharSequence text) {
        List<Integer> matches = new java.util.ArrayList<>();
        KmpSearch.forEachMatch(pattern, text, matches::add);
        return List.copyOf(matches);
    }

    private static void assertEquals(Object expected, Object actual, String scenario) {
        if (!expected.equals(actual)) {
            throw new AssertionError(scenario + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String scenario) {
        if (!condition) {
            throw new AssertionError(scenario);
        }
    }

    private static void assertFalse(boolean condition, String scenario) {
        assertTrue(!condition, scenario);
    }

    private static void assertThrows(
            Class<? extends Throwable> expectedType,
            ThrowingRunnable action,
            String scenario) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                    scenario + ": expected " + expectedType.getSimpleName()
                            + " but got " + thrown.getClass().getSimpleName(),
                    thrown);
        }

        throw new AssertionError(scenario + ": expected " + expectedType.getSimpleName());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
