package sorting;

/**
 * Internal helpers shared by {@link Sorter} implementations. Package-private:
 * not part of the public API.
 */
final class SortSupport {

    private SortSupport() {
        // Utility class; not instantiable.
    }

    /** Swaps the elements at indices {@code i} and {@code j} of an object array. */
    static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /** Swaps the elements at indices {@code i} and {@code j} of an int array. */
    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
