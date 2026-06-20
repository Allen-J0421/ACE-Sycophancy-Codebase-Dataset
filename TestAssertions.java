public final class TestAssertions {

    private TestAssertions() {
    }

    public static void assertEquals(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertThrows(
            Class<? extends RuntimeException> expectedType,
            Runnable action,
            String label
    ) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }
            throw new AssertionError(label + " threw unexpected exception " + exception, exception);
        }

        throw new AssertionError(label + " did not throw " + expectedType.getSimpleName());
    }
}
