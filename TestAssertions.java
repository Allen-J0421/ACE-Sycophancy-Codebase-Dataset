import java.util.Arrays;

final class TestAssertions {
    private TestAssertions() {
    }

    static void assertMatrixEquals(int[][] expected, int[][] actual) {
        if (!Arrays.deepEquals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.deepToString(expected) + " but was " + Arrays.deepToString(actual)
            );
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but was \"" + actual + "\"");
        }
    }

    static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }
            throw new AssertionError(
                "Expected " + expectedType.getSimpleName() + " but caught " + error.getClass().getSimpleName(),
                error
            );
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown.");
    }
}
