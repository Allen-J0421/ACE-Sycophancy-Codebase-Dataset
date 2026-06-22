final class RodCuttingSolver {
    private final PriceTable priceTable;
    private final int[] bestRevenueByLength;
    private final int rodLength;

    RodCuttingSolver(PriceTable priceTable) {
        this.priceTable = priceTable;
        this.rodLength = priceTable.maxLength();
        this.bestRevenueByLength = new int[rodLength + 1];
    }

    int solve() {
        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            bestRevenueByLength[currentLength] = computeBestRevenue(currentLength);
        }

        return bestRevenueByLength[rodLength];
    }

    private int computeBestRevenue(int targetLength) {
        int bestRevenue = 0;

        for (int firstCutLength = 1; firstCutLength <= targetLength; firstCutLength++) {
            bestRevenue = Math.max(
                    bestRevenue,
                    priceTable.priceFor(firstCutLength)
                            + bestRevenueByLength[targetLength - firstCutLength]
            );
        }

        return bestRevenue;
    }
}
