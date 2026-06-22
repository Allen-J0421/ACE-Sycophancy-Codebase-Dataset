public final class CuttingRodTest {

    private static final int[] CLASSIC_SAMPLE_PRICES = {0, 1, 5, 8, 9, 10, 17, 17, 20};

    private CuttingRodTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldReturnZeroForNullInput();
        shouldReturnZeroForNonPositiveLength();
        shouldSolveClassicSample();
        shouldSolveShorterRodLength();
        shouldKeepCompatibilityShim();
        shouldRejectTooSmallPriceTable();
        System.out.println("All CuttingRod tests passed.");
    }

    private static void shouldReturnZeroForNullInput() {
        assertEquals(0, CuttingRod.cutRod(null), "null input");
    }

    private static void shouldReturnZeroForNonPositiveLength() {
        assertEquals(0, CuttingRod.maxRevenue(new int[] {0, 1, 2}, 0), "rod length 0");
        assertEquals(0, CuttingRod.maxRevenue(new int[] {0, 1, 2}, -3), "negative rod length");
    }

    private static void shouldSolveClassicSample() {
        assertEquals(22, CuttingRod.maxRevenue(CLASSIC_SAMPLE_PRICES), "classic sample");
    }

    private static void shouldSolveShorterRodLength() {
        assertEquals(10, CuttingRod.maxRevenue(CLASSIC_SAMPLE_PRICES, 4), "rod length 4");
    }

    private static void shouldKeepCompatibilityShim() {
        assertEquals(
                CuttingRod.maxRevenue(CLASSIC_SAMPLE_PRICES),
                CuttingRod.cutRod(CLASSIC_SAMPLE_PRICES),
                "compatibility shim");
    }

    private static void shouldRejectTooSmallPriceTable() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CuttingRod.maxRevenue(new int[] {0, 1, 5}, 5),
                "undersized price table");
    }

    private static void assertEquals(int expected, int actual, String caseName) {
        if (expected != actual) {
            throw new AssertionError(
                    caseName + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrows(
            Class<? extends Throwable> expectedType, ThrowingRunnable action, String caseName) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw new AssertionError(
                    caseName + ": expected " + expectedType.getSimpleName()
                            + " but got " + actual.getClass().getSimpleName(),
                    actual);
        }

        throw new AssertionError(caseName + ": expected " + expectedType.getSimpleName());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
