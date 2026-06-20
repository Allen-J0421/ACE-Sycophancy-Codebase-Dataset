import java.util.Objects;

public final class InsertionSort {

    private static final int[] DEMO_INPUT = {12, 11, 13, 5, 6};

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

    private static void printArray(int[] array) {
        for (int value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] sorted = sortedCopy(DEMO_INPUT);
        printArray(sorted);
    }
}
