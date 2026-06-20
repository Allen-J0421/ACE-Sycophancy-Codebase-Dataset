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
        checkRange(array.length, fromIndex, toIndex);

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
        checkRange(array.length, fromIndex, toIndex);

        int[] copy = array.clone();
        sort(copy, fromIndex, toIndex);
        return copy;
    }

    private static void checkRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex (" + fromIndex + ") is negative");
        }
        if (toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "toIndex (" + toIndex + ") is greater than array length (" + length + ")");
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") is greater than toIndex (" + toIndex + ")");
        }
    }
}
