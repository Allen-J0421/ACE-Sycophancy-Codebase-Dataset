class RodCuttingSolver implements Solver {

    @Override
    public RodCuttingSolution solve(RodCuttingProblem problem) {
        PriceTable prices = problem.prices();
        int n = prices.rodLength();
        RevenueTable revenue = new RevenueTable(n);
        CutChoiceTable cutChoice = new CutChoiceTable(n);

        for (int i = 1; i <= n; i++) {
            BestCut best = computeBestCut(i, prices, revenue);
            revenue.set(i, best.revenue());
            cutChoice.set(i, best.cutLength());
        }

        return new RodCuttingSolution(revenue.get(n), cutChoice.reconstructCuts(n));
    }

    private BestCut computeBestCut(int rodLength, PriceTable prices, RevenueTable revenue) {
        int bestRevenue = 0;
        int bestCut = 0;
        for (int cut = 1; cut <= rodLength; cut++) {
            int candidate = prices.priceAt(cut) + revenue.get(rodLength - cut);
            if (candidate > bestRevenue) {
                bestRevenue = candidate;
                bestCut = cut;
            }
        }
        return new BestCut(bestCut, bestRevenue);
    }
}
