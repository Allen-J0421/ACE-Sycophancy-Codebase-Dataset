class RodCuttingSolver implements Solver {

    @Override
    public RodCuttingSolution solve(PriceTable prices) {
        int n = prices.rodLength();
        RevenueTable revenue = new RevenueTable(n);
        CutChoiceTable cutChoice = new CutChoiceTable(n);

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                int candidate = prices.priceAt(j) + revenue.get(i - j);
                if (candidate > revenue.get(i)) {
                    revenue.set(i, candidate);
                    cutChoice.set(i, j);
                }
            }
        }

        return new RodCuttingSolution(revenue.get(n), cutChoice.reconstructCuts(n));
    }
}
