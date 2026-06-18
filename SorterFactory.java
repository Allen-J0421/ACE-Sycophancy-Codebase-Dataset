class SorterFactory {
    public enum Algorithm {
        QUICKSORT_CLASSIC,
        QUICKSORT_HYBRID,
        QUICKSORT_THREEWAY,
        HEAPSORT,
        INTROSORT
    }

    private static SortingConfiguration defaultConfig = new SortingConfiguration();

    public static <T extends Comparable<T>> Sorter<T> create(Algorithm algorithm) {
        return create(algorithm, defaultConfig);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> Sorter<T> create(
            Algorithm algorithm,
            SortingConfiguration config) {
        switch (algorithm) {
            case QUICKSORT_CLASSIC:
                return (Sorter<T>) new QuickSortImpl<>();
            case QUICKSORT_HYBRID:
                return (Sorter<T>) new HybridQuickSort<T>(new MedianOfThreePivotSelector<T>());
            case QUICKSORT_THREEWAY:
                return (Sorter<T>) new ThreeWayQuickSort<>();
            case HEAPSORT:
                return (Sorter<T>) new HeapSort<>();
            case INTROSORT:
                return (Sorter<T>) new IntroSort<>();
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    public static <T extends Comparable<T>> Sorter<T> createIntroSort() {
        return create(Algorithm.INTROSORT);
    }

    public static <T extends Comparable<T>> Sorter<T> createHybrid() {
        return create(Algorithm.QUICKSORT_HYBRID);
    }

    public static <T extends Comparable<T>> Sorter<T> createThreeWay() {
        return create(Algorithm.QUICKSORT_THREEWAY);
    }

    public static void setDefaultConfiguration(SortingConfiguration config) {
        defaultConfig = config;
    }

    public static SortingConfiguration getDefaultConfiguration() {
        return defaultConfig;
    }
}
