final class RodCuttingFixtures {

    private static final int[] CLASSIC_SAMPLE_PRICES = {0, 1, 5, 8, 9, 10, 17, 17, 20};

    private RodCuttingFixtures() {
        // Utility class.
    }

    static int[] classicSamplePrices() {
        return CLASSIC_SAMPLE_PRICES.clone();
    }

    static PriceTable classicSamplePriceTable() {
        return PriceTable.of(CLASSIC_SAMPLE_PRICES);
    }
}
