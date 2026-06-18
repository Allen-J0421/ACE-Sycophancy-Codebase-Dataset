import java.util.Comparator;

public class SortBenchmark<T extends Comparable<T>> {
    private final Sorter<T> sorter;
    private final String name;

    public SortBenchmark(Sorter<T> sorter, String name) {
        this.sorter = sorter;
        this.name = name;
    }

    public BenchmarkResult benchmark(T[] array, int iterations) {
        long totalDuration = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;

        for (int i = 0; i < iterations; i++) {
            T[] copy = array.clone();
            sorter.sort(copy);
            SortStatistics stats = sorter.getStatistics();
            if (stats != null) {
                totalDuration += stats.getDurationNanos();
                totalComparisons += stats.getComparisons();
                totalSwaps += stats.getSwaps();
            }
        }

        return new BenchmarkResult(name, array.length, iterations,
                totalDuration / iterations, totalComparisons / iterations, totalSwaps / iterations);
    }

    public static class BenchmarkResult {
        private final String algorithmName;
        private final int arraySize;
        private final int iterations;
        private final long averageDurationNanos;
        private final long averageComparisons;
        private final long averageSwaps;

        public BenchmarkResult(String algorithmName, int arraySize, int iterations,
                long avgDuration, long avgComparisons, long avgSwaps) {
            this.algorithmName = algorithmName;
            this.arraySize = arraySize;
            this.iterations = iterations;
            this.averageDurationNanos = avgDuration;
            this.averageComparisons = avgComparisons;
            this.averageSwaps = avgSwaps;
        }

        @Override
        public String toString() {
            return String.format("%s (size=%d, iterations=%d): %.3f ms | Comparisons: %d | Swaps: %d",
                    algorithmName, arraySize, iterations,
                    averageDurationNanos / 1_000_000.0,
                    averageComparisons, averageSwaps);
        }

        public double getAverageDurationMs() {
            return averageDurationNanos / 1_000_000.0;
        }

        public long getAverageComparisons() {
            return averageComparisons;
        }

        public long getAverageSwaps() {
            return averageSwaps;
        }
    }
}
