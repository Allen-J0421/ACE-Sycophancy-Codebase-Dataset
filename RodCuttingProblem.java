final class RodCuttingProblem {
    private final PriceTable priceTable;

    private RodCuttingProblem(PriceTable priceTable) {
        this.priceTable = priceTable;
    }

    static RodCuttingProblem fromSentinelPriceTable(int[] priceTableWithSentinel) {
        return new RodCuttingProblem(PriceTable.fromSentinelArray(priceTableWithSentinel));
    }

    static RodCuttingProblem fromPricesByLength(int[] pricesByLength) {
        return new RodCuttingProblem(PriceTable.fromPricesByLength(pricesByLength));
    }

    int rodLength() {
        return priceTable.maxLength();
    }

    int maxRevenue() {
        return solve().maxRevenue();
    }

    int pieceCount() {
        return solve().pieceCount();
    }

    RodCuttingSolution solve() {
        return new RodCuttingSolver(priceTable).solve();
    }
}
