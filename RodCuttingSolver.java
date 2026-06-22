final class RodCuttingSolver {
    private final PriceTable priceTable;
    private final RevenueTable revenueTable;
    private final int rodLength;

    RodCuttingSolver(PriceTable priceTable) {
        this.priceTable = priceTable;
        this.rodLength = priceTable.maxLength();
        this.revenueTable = new RevenueTable(rodLength);
    }

    int solve() {
        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            revenueTable.recordBestRevenue(currentLength, computeBestRevenue(currentLength));
        }

        return revenueTable.bestRevenueFor(rodLength);
    }

    private int computeBestRevenue(int targetLength) {
        int bestRevenue = 0;

        for (int firstCutLength = 1; firstCutLength <= targetLength; firstCutLength++) {
            bestRevenue = Math.max(
                    bestRevenue,
                    priceTable.priceFor(firstCutLength)
                            + revenueTable.bestRevenueFor(targetLength - firstCutLength)
            );
        }

        return bestRevenue;
    }
}
