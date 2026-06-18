import java.util.Comparator;

public class ArraysSortAdapter {
    public static <T extends Comparable<T>> void sort(T[] array) {
        Sorter<T> sorter = SorterFactory.createOptimized();
        sorter.sort(array);
    }

    public static <T extends Comparable<T>> void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0) {
            return;
        }
        SortConfiguration<T> config = SortConfiguration.<T>builder()
                .withComparator(comparator)
                .build();
        OptimizedMergeSort<T> sorter = new OptimizedMergeSort<T>(config);
        sorter.sort(array, comparator);
    }

    public static <T extends Comparable<T>> void parallelSort(T[] array) {
        sort(array);
    }

    public static <T extends Comparable<T>> void parallelSort(T[] array, Comparator<T> comparator) {
        sort(array, comparator);
    }

    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
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

    public static <T extends Comparable<T>> boolean isSorted(T[] array, Comparator<T> comparator) {
        if (array == null || array.length <= 1) {
            return true;
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (comparator.compare(array[i], array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }
}
