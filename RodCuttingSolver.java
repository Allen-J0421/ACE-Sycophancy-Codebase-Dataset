class RodCuttingSolver implements Solver {

    @Override
    public RodCuttingSolution solve(PriceTable prices) {
        int n = prices.rodLength();
        RevenueTable revenue = new RevenueTable(n);
        CutChoiceTable cutChoice = new CutChoiceTable(n);

        for (int i = 1; i <= n; i++) {
            computeBestCut(i, prices, revenue, cutChoice);
        }

        return new RodCuttingSolution(revenue.get(n), cutChoice.reconstructCuts(n));
    }

    private void computeBestCut(int rodLength, PriceTable prices, RevenueTable revenue, CutChoiceTable cutChoice) {
        for (int cut = 1; cut <= rodLength; cut++) {
            int candidate = prices.priceAt(cut) + revenue.get(rodLength - cut);
            if (candidate > revenue.get(rodLength)) {
                revenue.set(rodLength, candidate);
                cutChoice.set(rodLength, cut);
            }
        }
    }
}
