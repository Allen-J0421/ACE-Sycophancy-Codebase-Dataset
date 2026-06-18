package lru.support;

public final class TestSupport {
    private TestSupport() {
        // No instances.
    }

    public static void assertEquals(int expected, Integer actual, String message) {
        if (actual == null || actual != expected) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    public static void assertNull(Integer actual, String message) {
        if (actual != null) {
            throw new AssertionError(message + ": expected null, got " + actual);
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void expectNullPointer(Runnable operation) {
        try {
            operation.run();
            throw new AssertionError("expected NullPointerException");
        } catch (NullPointerException expected) {
            // Expected.
        }
    }
}
