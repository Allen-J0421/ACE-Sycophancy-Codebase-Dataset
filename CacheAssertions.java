import java.util.Objects;

public final class CacheAssertions {
    private CacheAssertions() {
    }

    public static void assertTrue(String description, boolean condition) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    public static void assertFalse(String description, boolean condition) {
        if (condition) {
            throw new AssertionError(description);
        }
    }

    public static void assertEquals(String description, Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(
                description + ": expected " + expected + ", but got " + actual
            );
        }
    }

    public static void assertThrows(
        String description,
        Class<? extends Throwable> expectedType,
        ThrowingRunnable action
    ) {
        try {
            action.run();
            throw new AssertionError("expected " + expectedType.getSimpleName() + " for " + description);
        } catch (Throwable throwable) {
            if (!expectedType.isInstance(throwable)) {
                throw new AssertionError(
                    description + ": expected " + expectedType.getSimpleName()
                        + ", but got " + throwable.getClass().getSimpleName(),
                    throwable
                );
            }
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run();
    }
}
