class RodCuttingStrategyFactory {

    static RodCuttingStrategy create(SolverType type) {
        switch (type) {
            case ITERATIVE_DP:       return new IterativeDPStrategy();
            case MEMOIZED_RECURSIVE: return new MemoizedRecursiveStrategy();
            default: throw new IllegalArgumentException("Unknown solver type: " + type);
        }
    }
}
