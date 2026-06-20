import java.util.Objects;

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
        Objects.checkFromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex + 1; i < toIndex; i++) {
            int key = array[i];
            int j = i - 1;

            while (j >= fromIndex && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    public static int[] sortedCopy(int[] array) {
        Objects.requireNonNull(array, "array");
        return sortedCopy(array, 0, array.length);
    }

    public static int[] sortedCopy(int[] array, int fromIndex, int toIndex) {
        Objects.requireNonNull(array, "array");
        Objects.checkFromToIndex(fromIndex, toIndex, array.length);

        int[] copy = array.clone();
        sort(copy, fromIndex, toIndex);
        return copy;
    }

    public static <T extends Comparable<? super T>> void sort(T[] array) {
        Objects.requireNonNull(array, "array");
        sort(array, 0, array.length);
    }

    public static <T extends Comparable<? super T>> void sort(T[] array, int fromIndex, int toIndex) {
        Objects.requireNonNull(array, "array");
        Objects.checkFromToIndex(fromIndex, toIndex, array.length);

        for (int i = fromIndex + 1; i < toIndex; i++) {
            T key = array[i];
            int j = i - 1;

            while (j >= fromIndex && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] array) {
        Objects.requireNonNull(array, "array");
        return sortedCopy(array, 0, array.length);
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] array, int fromIndex, int toIndex) {
        Objects.requireNonNull(array, "array");
        Objects.checkFromToIndex(fromIndex, toIndex, array.length);

        T[] copy = array.clone();
        sort(copy, fromIndex, toIndex);
        return copy;
    }
}
