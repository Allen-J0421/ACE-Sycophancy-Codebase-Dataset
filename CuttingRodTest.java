public final class CuttingRodTest {

    private CuttingRodTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldReturnZeroForNullInput();
        shouldReturnZeroForNonPositiveLength();
        shouldSolveClassicSample();
        shouldSolveShorterRodLength();
        shouldRejectTooSmallPriceTable();
        System.out.println("All CuttingRod tests passed.");
    }

    private static void shouldReturnZeroForNullInput() {
        assertEquals(0, CuttingRod.cutRod(null), "null input");
    }

    private static void shouldReturnZeroForNonPositiveLength() {
        assertEquals(0, CuttingRod.cutRod(new int[] {0, 1, 2}, 0), "rod length 0");
        assertEquals(0, CuttingRod.cutRod(new int[] {0, 1, 2}, -3), "negative rod length");
    }

    private static void shouldSolveClassicSample() {
        int[] samplePrices = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        assertEquals(22, CuttingRod.cutRod(samplePrices), "classic sample");
    }

    private static void shouldSolveShorterRodLength() {
        int[] samplePrices = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        assertEquals(10, CuttingRod.cutRod(samplePrices, 4), "rod length 4");
    }

    private static void shouldRejectTooSmallPriceTable() {
        boolean threw = false;
        try {
            CuttingRod.cutRod(new int[] {0, 1, 5}, 5);
        } catch (IllegalArgumentException expected) {
            threw = true;
        }

        if (!threw) {
            throw new AssertionError("expected IllegalArgumentException for an undersized price table");
        }
    }

    private static void assertEquals(int expected, int actual, String caseName) {
        if (expected != actual) {
            throw new AssertionError(
                    caseName + ": expected " + expected + " but got " + actual);
        }
    }
}
