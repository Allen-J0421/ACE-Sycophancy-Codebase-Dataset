package queue;

import java.util.Objects;

final class TestSupport {

    private TestSupport() {
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError(message + ": expected null but was " + actual);
        }
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    static <T extends Throwable> void assertThrows(
            Class<T> expectedType,
            ThrowingRunnable action,
            String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(
                    message + ": expected " + expectedType.getSimpleName()
                            + " but caught " + throwable.getClass().getSimpleName(),
                    throwable);
        }

        throw new AssertionError(message + ": expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run();
    }
}
