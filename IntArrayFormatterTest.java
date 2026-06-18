public final class IntArrayFormatterTest {
    private IntArrayFormatterTest() {
    }

    public static void main(String[] args) {
        assertFormattedValues();
        assertNullRejected();
    }

    private static void assertFormattedValues() {
        String formattedValues = IntArrayFormatter.format(new int[] {1, 5, 7});

        TestAssertions.assertStringEquals(
                "formatted values should preserve the legacy console output", formattedValues, "1 5 7 ");
    }

    private static void assertNullRejected() {
        TestAssertions.assertThrowsNullPointer("format null values", () -> IntArrayFormatter.format(null));
    }
}
