final class TestAssertions {
    private TestAssertions() {
    }

    static IntAssertion assertThat(int actual) {
        return new IntAssertion(actual);
    }

    static <T extends Throwable> void assertThrows(
            Class<T> expectedType,
            ThrowingRunnable action,
            String message) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }

            throw new AssertionError(
                    message + " expected exception:<" + expectedType.getName()
                            + "> but was:<" + actual.getClass().getName() + ">",
                    actual);
        }

        throw new AssertionError(message + " expected exception:<" + expectedType.getName() + "> but nothing was thrown");
    }

    static final class IntAssertion {
        private final int actual;

        private IntAssertion(int actual) {
            this.actual = actual;
        }

        void isEqualTo(int expected, String message) {
            if (actual != expected) {
                throw new AssertionError(message + " expected:<" + expected + "> but was:<" + actual + ">");
            }
        }
    }

    interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
