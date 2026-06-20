package sorting;

import java.util.Objects;
import java.util.Comparator;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array");
        sort(array, 0, array.length);
    }

    public static void sort(int[] array, int fromIndex, int toIndex) {
        Objects.requireNonNull(array, "array");
        sortRange(array, range(fromIndex, toIndex, array.length));
    }

    public static int[] sortedCopy(int[] array) {
        Objects.requireNonNull(array, "array");
        return sortedCopy(array, 0, array.length);
    }

    public static int[] sortedCopy(int[] array, int fromIndex, int toIndex) {
        Objects.requireNonNull(array, "array");

        int[] copy = array.clone();
        sortRange(copy, range(fromIndex, toIndex, array.length));
        return copy;
    }

    public static <T extends Comparable<? super T>> void sort(T[] array) {
        sort(array, Comparator.naturalOrder());
    }

    public static <T extends Comparable<? super T>> void sort(T[] array, int fromIndex, int toIndex) {
        sort(array, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        sort(array, 0, array.length, comparator);
    }

    public static <T> void sort(T[] array, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");

        sortRange(array, range(fromIndex, toIndex, array.length), comparator);
    }

    private static void sortRange(int[] array, Range range) {
        for (int i = range.fromIndex() + 1; i < range.toIndex(); i++) {
            int key = array[i];
            int j = i - 1;

            while (j >= range.fromIndex() && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    private static <T> void sortRange(T[] array, Range range, Comparator<? super T> comparator) {
        for (int i = range.fromIndex() + 1; i < range.toIndex(); i++) {
            T key = array[i];
            int j = i - 1;

            while (j >= range.fromIndex() && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] array) {
        return sortedCopy(array, Comparator.naturalOrder());
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] array, int fromIndex, int toIndex) {
        return sortedCopy(array, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> T[] sortedCopy(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        return sortedCopy(array, 0, array.length, comparator);
    }

    public static <T> T[] sortedCopy(T[] array, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");

        T[] copy = array.clone();
        sortRange(copy, range(fromIndex, toIndex, array.length), comparator);
        return copy;
    }

    private static Range range(int fromIndex, int toIndex, int length) {
        Objects.checkFromToIndex(fromIndex, toIndex, length);
        return new Range(fromIndex, toIndex);
    }

    private record Range(int fromIndex, int toIndex) {
    }
}
