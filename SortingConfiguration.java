class SortingConfiguration {
    private int insertionSortThreshold = 10;
    private int introSortMaxDepth = 16;
    private boolean enableMetrics = false;
    private int randomSeed = 42;

    public SortingConfiguration() {
    }

    public SortingConfiguration(int insertionThreshold, int maxDepth) {
        this.insertionSortThreshold = insertionThreshold;
        this.introSortMaxDepth = maxDepth;
    }

    public int getInsertionSortThreshold() {
        return insertionSortThreshold;
    }

    public SortingConfiguration setInsertionSortThreshold(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException("Threshold must be >= 0");
        this.insertionSortThreshold = threshold;
        return this;
    }

    public int getIntroSortMaxDepth() {
        return introSortMaxDepth;
    }

    public SortingConfiguration setIntroSortMaxDepth(int depth) {
        if (depth < 1) throw new IllegalArgumentException("Depth must be >= 1");
        this.introSortMaxDepth = depth;
        return this;
    }

    public boolean isMetricsEnabled() {
        return enableMetrics;
    }

    public SortingConfiguration enableMetrics(boolean enable) {
        this.enableMetrics = enable;
        return this;
    }

    public int getRandomSeed() {
        return randomSeed;
    }

    public SortingConfiguration setRandomSeed(int seed) {
        this.randomSeed = seed;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
            "SortingConfiguration{threshold=%d, depth=%d, metrics=%s, seed=%d}",
            insertionSortThreshold, introSortMaxDepth, enableMetrics, randomSeed
        );
    }
}
