import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public final class SortingUtils {
    private SortingUtils() {
    }

    public static <T extends Comparable<T>> SortResult sortAndMeasure(T[] array) {
        return sortAndMeasure(array, Comparable::compareTo);
    }

    public static <T extends Comparable<T>> SortResult sortAndMeasure(
            T[] array, Comparator<T> comparator) {
        return sortAndMeasure(array, comparator, SortProfile.BALANCED.create());
    }

    public static <T extends Comparable<T>> SortResult sortAndMeasure(
            T[] array, Comparator<T> comparator, SortConfiguration<T> config) {
        long startNanos = System.nanoTime();

        try {
            ModernMergeSort<T> sorter = new ModernMergeSort<T>(config);
            sorter.sort(array, comparator);

            long durationNanos = System.nanoTime() - startNanos;
            SortStatistics stats = sorter.getStatistics();

            return new SortResult(
                    durationNanos,
                    stats != null ? stats.getComparisons() : 0,
                    stats != null ? stats.getSwaps() : 0,
                    true,
                    "Sort completed successfully");
        } catch (Exception e) {
            long durationNanos = System.nanoTime() - startNanos;
            return new SortResult(
                    durationNanos,
                    0,
                    0,
                    false,
                    "Sort failed: " + e.getMessage());
        }
    }

    public static <T extends Comparable<T>> void benchmarkComparison(
            T[] data, String... algorithmNames) {
        System.out.println("Benchmarking " + algorithmNames.length + " algorithms:");

        for (String name : algorithmNames) {
            @SuppressWarnings("unchecked")
            T[] copy = (T[]) data.clone();
            SortResult result = sortAndMeasure(copy);

            String output = String.format(
                    "  %s: %.3f ms (comparisons: %d, swaps: %d)",
                    String.format("%-20s", name),
                    result.durationMs(),
                    result.comparisons(),
                    result.swaps());
            System.out.println(output);
        }
    }

    public static <T extends Comparable<T>> boolean verifySorted(T[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }
}
