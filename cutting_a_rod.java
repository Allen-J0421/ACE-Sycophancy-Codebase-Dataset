final class CuttingRod {

    static int cutRod(int[] priceTableWithSentinel) {
        return RodCuttingProblem
                .fromSentinelPriceTable(priceTableWithSentinel)
                .solve()
                .maxRevenue();
    }

    static int maxRevenueForPricesByLength(int[] pricesByLength) {
        return RodCuttingProblem
                .fromPricesByLength(pricesByLength)
                .solve()
                .maxRevenue();
    }

    static RodCuttingSolution solveForSentinelPriceTable(int[] priceTableWithSentinel) {
        return RodCuttingProblem.fromSentinelPriceTable(priceTableWithSentinel).solve();
    }

    static RodCuttingSolution solveForPricesByLength(int[] pricesByLength) {
        return RodCuttingProblem.fromPricesByLength(pricesByLength).solve();
    }

    public static void main(String[] args) {
        int[] pricesByLength = {1, 5, 8, 9, 10, 17, 17, 20};
        RodCuttingProblem problem = RodCuttingProblem.fromPricesByLength(pricesByLength);
        System.out.println(problem.solve().maxRevenue());
    }
}
