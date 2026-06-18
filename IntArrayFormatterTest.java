public final class IntArrayFormatterTest {
    private IntArrayFormatterTest() {
    }

    public static void main(String[] args) {
        assertFormattedValues();
        assertNullRejected();
    }

    private static void assertFormattedValues() {
        String formattedValues = IntArrayFormatter.format(new int[] {1, 5, 7});

        if (!"1 5 7 ".equals(formattedValues)) {
            throw new AssertionError("formatted values should preserve the legacy console output");
        }
    }

    private static void assertNullRejected() {
        try {
            IntArrayFormatter.format(null);
            throw new AssertionError("null values should be rejected for formatting");
        } catch (NullPointerException expected) {
            // Expected by the public formatting contract.
        }
    }
}
