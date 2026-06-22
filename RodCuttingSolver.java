final class RodCuttingSolver {
    private final PriceTable priceTable;
    private final RevenueTable revenueTable;
    private final CutChoiceTable cutChoiceTable;
    private final int rodLength;

    RodCuttingSolver(PriceTable priceTable) {
        this.priceTable = priceTable;
        this.rodLength = priceTable.maxLength();
        this.revenueTable = new RevenueTable(rodLength);
        this.cutChoiceTable = new CutChoiceTable(rodLength);
    }

    RodCuttingSolution solve() {
        for (int currentLength = 1; currentLength <= rodLength; currentLength++) {
            BestCut bestCut = computeBestCut(currentLength);
            revenueTable.recordBestRevenue(currentLength, bestCut.revenue());
            cutChoiceTable.recordFirstCut(currentLength, bestCut.firstCutLength());
        }

        return new RodCuttingSolution(
                revenueTable.bestRevenueFor(rodLength),
                cutChoiceTable.reconstructCuts(rodLength)
        );
    }

    private BestCut computeBestCut(int targetLength) {
        int bestRevenue = 0;
        int bestFirstCutLength = 0;

        for (int firstCutLength = 1; firstCutLength <= targetLength; firstCutLength++) {
            int candidateRevenue = priceTable.priceFor(firstCutLength)
                    + revenueTable.bestRevenueFor(targetLength - firstCutLength);

            if (candidateRevenue > bestRevenue) {
                bestRevenue = candidateRevenue;
                bestFirstCutLength = firstCutLength;
            }
        }

        return new BestCut(bestRevenue, bestFirstCutLength);
    }

    private record BestCut(int revenue, int firstCutLength) {
    }
}
