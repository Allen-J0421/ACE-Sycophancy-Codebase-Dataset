package binarysearch;

final class BinarySearchAssertions {
    private BinarySearchAssertions() {
        // Test utility class.
    }

    static void expectEquals(int expected, int actual, String description) {
        if (expected != actual) {
            throw new AssertionError(description + ": expected " + expected + ", got " + actual);
        }
    }

    static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action, String description) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(description + ": expected " + expectedType.getSimpleName() + ", got " + throwable.getClass().getSimpleName(), throwable);
        }

        throw new AssertionError(description + ": expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run();
    }
}
