class CuttingRodTest {
    private static final int[] SAMPLE_PRICES_BY_LENGTH = {0, 1, 5, 8, 9, 10, 17, 17, 20};

    public static void main(String[] args) {
        returnsBestRevenueForSamplePrices();
        returnsBestRevenueForExplicitRodLength();
        returnsZeroForOnlySentinelPrice();
        rejectsMissingPriceTable();
        rejectsEmptyPriceTable();
        rejectsNegativeRodLength();
        rejectsRodLengthOutsidePriceTable();
    }

    private static void returnsBestRevenueForSamplePrices() {
        assertEquals(22, CuttingRod.cutRod(SAMPLE_PRICES_BY_LENGTH));
    }

    private static void returnsBestRevenueForExplicitRodLength() {
        assertEquals(10, CuttingRod.cutRod(SAMPLE_PRICES_BY_LENGTH, 4));
    }

    private static void returnsZeroForOnlySentinelPrice() {
        int[] pricesByLength = {0};

        assertEquals(0, CuttingRod.cutRod(pricesByLength));
    }

    private static void rejectsMissingPriceTable() {
        assertThrowsIllegalArgument(() -> CuttingRod.cutRod(null));
    }

    private static void rejectsEmptyPriceTable() {
        assertThrowsIllegalArgument(() -> CuttingRod.cutRod(new int[0]));
    }

    private static void rejectsNegativeRodLength() {
        assertThrowsIllegalArgument(() -> CuttingRod.cutRod(new int[] {0, 1}, -1));
    }

    private static void rejectsRodLengthOutsidePriceTable() {
        assertThrowsIllegalArgument(() -> CuttingRod.cutRod(new int[] {0, 1}, 2));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrowsIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException");
    }
}
