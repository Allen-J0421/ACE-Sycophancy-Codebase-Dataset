import java.util.List;

public final class KmpSearchSelfTest {

    private KmpSearchSelfTest() {
    }

    public static void main(String[] args) {
        assertEquals(List.of(0, 9, 12), KmpSearch.search("aaba", "aabaacaadaabaaba"), "basic search");
        assertEquals(List.of(0, 1, 2), KmpSearch.search("aa", "aaaa"), "overlapping matches");
        assertEquals(List.of(), KmpSearch.search("xyz", "aaaa"), "no matches");
        assertEquals(List.of(), KmpSearch.search("longer-pattern", "short"), "pattern longer than text");

        KmpPattern compiledPattern = KmpPattern.compile("aba");
        assertEquals("aba", compiledPattern.value(), "compiled pattern preserves source value");
        assertEquals(List.of(0, 2), compiledPattern.findMatchesIn("ababa"), "compiled pattern search");
        assertTrue(compiledPattern.occursIn("xxabaxx"), "occursIn should detect matches");
        assertFalse(compiledPattern.occursIn("xxxx"), "occursIn should reject missing matches");

        assertThrows(IllegalArgumentException.class, () -> KmpPattern.compile(""), "empty pattern rejected");
        assertThrows(NullPointerException.class, () -> KmpPattern.compile(null), "null pattern rejected");
        assertThrows(NullPointerException.class, () -> compiledPattern.findMatchesIn(null), "null text rejected");
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
