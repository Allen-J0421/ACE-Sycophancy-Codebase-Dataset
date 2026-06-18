package binarysearch;

import java.util.OptionalInt;

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

    static void expectOptionalIntEquals(int expected, OptionalInt actual, String description) {
        if (actual.isEmpty()) {
            throw new AssertionError(description + ": expected " + expected + ", got empty result");
        }

        if (actual.getAsInt() != expected) {
            throw new AssertionError(description + ": expected " + expected + ", got " + actual.getAsInt());
        }
    }

    static void expectOptionalIntEmpty(OptionalInt actual, String description) {
        if (actual.isPresent()) {
            throw new AssertionError(description + ": expected empty result, got " + actual.getAsInt());
        }
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run();
    }
}
