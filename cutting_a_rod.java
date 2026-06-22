final class CuttingRod {

    static int cutRod(int[] priceTableWithSentinel) {
        return solveForSentinelPriceTable(priceTableWithSentinel).maxRevenue();
    }

    static int maxRevenueForPricesByLength(int[] pricesByLength) {
        return solveForPricesByLength(pricesByLength).maxRevenue();
    }

    static RodCuttingSolution solveForSentinelPriceTable(int[] priceTableWithSentinel) {
        return solve(PriceTable.fromSentinelArray(priceTableWithSentinel));
    }

    static RodCuttingSolution solveForPricesByLength(int[] pricesByLength) {
        return solve(PriceTable.fromPricesByLength(pricesByLength));
    }

    private static RodCuttingSolution solve(PriceTable priceTable) {
        return new RodCuttingSolver(priceTable).solve();
    }

    public static void main(String[] args) {
        int[] pricesByLength = {1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(maxRevenueForPricesByLength(pricesByLength));
    }
}
