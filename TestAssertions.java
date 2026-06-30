final class TestAssertions {
    private TestAssertions() {
    }

    static IntAssertion assertThat(int actual) {
        return new IntAssertion(actual);
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
}
