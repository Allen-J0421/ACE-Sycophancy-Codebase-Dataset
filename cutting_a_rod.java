final class CuttingRod {

    static int cutRod(int[] priceTableWithSentinel) {
        return solve(PriceTable.fromSentinelArray(priceTableWithSentinel));
    }

    static int maxRevenueForPricesByLength(int[] pricesByLength) {
        return solve(PriceTable.fromPricesByLength(pricesByLength));
    }

    private static int solve(PriceTable priceTable) {
        return new RodCuttingSolver(priceTable).solve();
    }

    public static void main(String[] args) {
        int[] pricesByLength = {1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(maxRevenueForPricesByLength(pricesByLength));
    }
}
