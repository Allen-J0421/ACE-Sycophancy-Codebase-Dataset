import java.util.Objects;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array");

        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;

            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    public static int[] sortedCopy(int[] array) {
        Objects.requireNonNull(array, "array");

        int[] copy = array.clone();
        sort(copy);
        return copy;
    }
}
