class RodCuttingSolver implements Solver {

    @Override
    public RodCuttingSolution solve(RodCuttingProblem problem) {
        int n = problem.rodLength();
        RodCuttingTables tables = new RodCuttingTables(n);

        for (int i = 1; i <= n; i++) {
            tables.record(i, computeBestCut(i, problem, tables));
        }

        return tables.toSolution();
    }

    private BestCut computeBestCut(int rodLength, RodCuttingProblem problem, RodCuttingTables tables) {
        int bestRevenue = 0;
        int bestCut = 0;
        for (int cut = 1; cut <= rodLength; cut++) {
            int candidate = problem.priceAt(cut) + tables.revenueFor(rodLength - cut);
            if (candidate > bestRevenue) {
                bestRevenue = candidate;
                bestCut = cut;
            }
        }
        return new BestCut(bestCut, bestRevenue);
    }
}
