public final class CuttingRodTest {

    private CuttingRodTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldCopyInputPriceTables();
        shouldReportAvailablePriceLengths();
        shouldReturnZeroForNullTable();
        shouldReturnZeroForNonPositiveLength();
        shouldSolveClassicSample();
        shouldSolveShorterRodLength();
        shouldHandleShortPriceTable();
        System.out.println("All CuttingRod tests passed.");
    }

    private static void shouldCopyInputPriceTables() {
        int[] prices = {0, 1, 5, 8};
        PriceTable priceTable = PriceTable.of(prices);
        prices[1] = 100;

        assertEquals(8, priceTable.priceFor(3), "copied price table");
    }

    private static void shouldReportAvailablePriceLengths() {
        PriceTable priceTable = PriceTable.of(new int[] {0, 1, 5});

        assertTrue(priceTable.hasPriceFor(1), "length 1 available");
        assertFalse(priceTable.hasPriceFor(3), "length 3 unavailable");
    }

    private static void shouldReturnZeroForNullTable() {
        assertEquals(0, CuttingRod.maxRevenue(null), "null input");
    }

    private static void shouldReturnZeroForNonPositiveLength() {
        assertEquals(0, CuttingRod.maxRevenue(PriceTable.of(new int[] {0, 1, 2}), 0),
                "rod length 0");
        assertEquals(0, CuttingRod.maxRevenue(PriceTable.of(new int[] {0, 1, 2}), -3),
                "negative rod length");
    }

    private static void shouldSolveClassicSample() {
        assertEquals(22, CuttingRod.maxRevenue(RodCuttingFixtures.classicSamplePriceTable()),
                "classic sample");
    }

    private static void shouldSolveShorterRodLength() {
        assertEquals(10, CuttingRod.maxRevenue(RodCuttingFixtures.classicSamplePriceTable(), 4),
                "rod length 4");
    }

    private static void shouldHandleShortPriceTable() {
        assertEquals(11, CuttingRod.maxRevenue(PriceTable.of(new int[] {0, 1, 5}), 5),
                "short price table");
    }

    private static void assertEquals(int expected, int actual, String caseName) {
        if (expected != actual) {
            throw new AssertionError(
                    caseName + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String caseName) {
        if (!condition) {
            throw new AssertionError(caseName + ": expected true");
        }
    }

    private static void assertFalse(boolean condition, String caseName) {
        if (condition) {
            throw new AssertionError(caseName + ": expected false");
        }
    }
}
