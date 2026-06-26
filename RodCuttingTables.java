class RodCuttingTables {
    private final RevenueTable revenue;
    private final CutChoiceTable cutChoice;

    RodCuttingTables(int size) {
        revenue = new RevenueTable(size);
        cutChoice = new CutChoiceTable(size);
    }

    int revenueFor(int length) {
        return revenue.get(length);
    }

    void record(int length, BestCut best) {
        revenue.set(length, best.revenue());
        cutChoice.set(length, best.cutLength());
    }

    RodCuttingSolution toSolution() {
        int n = revenue.size();
        return new RodCuttingSolution(revenue.get(n), cutChoice.reconstructCuts(n));
    }
}
