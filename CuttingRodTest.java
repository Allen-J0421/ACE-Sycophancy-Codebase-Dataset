public final class CuttingRodTest {

    private CuttingRodTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldReturnZeroForNullInput();
        shouldReturnZeroForNonPositiveLength();
        shouldSolveClassicSample();
        shouldSolveShorterRodLength();
        shouldKeepCompatibilityShim();
        shouldHandleShortPriceTable();
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
        assertEquals(22, CuttingRod.maxRevenue(RodCuttingFixtures.classicSamplePrices()),
                "classic sample");
    }

    private static void shouldSolveShorterRodLength() {
        assertEquals(10, CuttingRod.maxRevenue(RodCuttingFixtures.classicSamplePrices(), 4),
                "rod length 4");
    }

    private static void shouldKeepCompatibilityShim() {
        assertEquals(
                CuttingRod.maxRevenue(RodCuttingFixtures.classicSamplePrices()),
                CuttingRod.cutRod(RodCuttingFixtures.classicSamplePrices()),
                "compatibility shim");
    }

    private static void shouldHandleShortPriceTable() {
        assertEquals(11, CuttingRod.maxRevenue(new int[] {0, 1, 5}, 5), "short price table");
    }

    private static void assertEquals(int expected, int actual, String caseName) {
        if (expected != actual) {
            throw new AssertionError(
                    caseName + ": expected " + expected + " but got " + actual);
        }
    }
}
