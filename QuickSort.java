import java.util.Objects;

public final class QuickSort {
    private static final int[] SAMPLE_VALUES = {10, 7, 8, 9, 1, 5};

    private QuickSort() {
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        quickSort(values, 0, values.length - 1);
    }

    private static void quickSort(int[] values, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }

        int pivotIndex = partition(values, startIndex, endIndex);

        quickSort(values, startIndex, pivotIndex - 1);
        quickSort(values, pivotIndex + 1, endIndex);
    }

    private static int partition(int[] values, int startIndex, int endIndex) {
        int pivot = values[endIndex];
        int nextLowerValueIndex = startIndex;

        for (int currentIndex = startIndex; currentIndex < endIndex; currentIndex++) {
            if (values[currentIndex] < pivot) {
                swap(values, nextLowerValueIndex, currentIndex);
                nextLowerValueIndex++;
            }
        }

        swap(values, nextLowerValueIndex, endIndex);
        return nextLowerValueIndex;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }

        int temporaryValue = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temporaryValue;
    }

    private static void printValues(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
    }

    public static void main(String[] args) {
        int[] values = SAMPLE_VALUES.clone();

        sort(values);

        printValues(values);
    }
}
