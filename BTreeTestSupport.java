import java.util.List;

final class BTreeTestSupport {
    private BTreeTestSupport() {
    }

    static void runTest(String name, Runnable test) {
        try {
            test.run();
        } catch (Throwable throwable) {
            throw new AssertionError("Test failed: " + name, throwable);
        }
    }

    static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action, String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(message + " Expected exception type: " + expectedType.getSimpleName()
                    + ", Actual: " + throwable.getClass().getSimpleName(), throwable);
        }

        throw new AssertionError(message);
    }

    static void assertTreeContents(BTree tree, List<Integer> expectedKeys) {
        assertEquals(expectedKeys, tree.toList(), "Unexpected in-order traversal.");
        assertEquals(formatKeys(expectedKeys), tree.toString(), "Unexpected string representation.");
        assertEquals(expectedKeys.isEmpty(), tree.isEmpty(), "Unexpected empty-state result.");
    }

    static void assertMatchesScenario(BTreeScenario scenario) {
        BTree tree = scenario.createTree();
        assertTreeContents(tree, scenario.expectedKeys());

        for (BTreeScenario.SearchExpectation expectation : scenario.searchExpectations()) {
            assertSearchExpectation(tree, expectation);
        }
    }

    static void assertContains(BTree tree, int key) {
        assertTrue(tree.contains(key), "Expected inserted key to be found: " + key);
    }

    static void assertDoesNotContain(BTree tree, int key) {
        assertFalse(tree.contains(key), "Expected missing key to be absent: " + key);
    }

    static void assertSearchExpectation(BTree tree, BTreeScenario.SearchExpectation expectation) {
        assertTrue(expectation.matches(tree), expectation.failureMessage(tree));
    }

    private static String formatKeys(List<Integer> keys) {
        StringBuilder builder = new StringBuilder();
        for (int key : keys) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(key);
        }
        return builder.toString();
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
