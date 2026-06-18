import java.util.Objects;

public final class QuickSort {
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
        while (startIndex < endIndex) {
            int pivotIndex = partition(values, startIndex, endIndex);

            int leftPartitionSize = pivotIndex - startIndex;
            int rightPartitionSize = endIndex - pivotIndex;

            // Recurse into the smaller partition first to limit stack growth.
            if (leftPartitionSize < rightPartitionSize) {
                quickSort(values, startIndex, pivotIndex - 1);
                startIndex = pivotIndex + 1;
            } else {
                quickSort(values, pivotIndex + 1, endIndex);
                endIndex = pivotIndex - 1;
            }
        }
    }

    private static int partition(int[] values, int startIndex, int endIndex) {
        int pivotIndex = choosePivotIndex(startIndex, endIndex);
        int pivot = values[pivotIndex];
        int nextLowerValueIndex = startIndex;

        swap(values, pivotIndex, endIndex);

        for (int currentIndex = startIndex; currentIndex < endIndex; currentIndex++) {
            if (values[currentIndex] < pivot) {
                swap(values, nextLowerValueIndex, currentIndex);
                nextLowerValueIndex++;
            }
        }

        swap(values, nextLowerValueIndex, endIndex);
        return nextLowerValueIndex;
    }

    private static int choosePivotIndex(int startIndex, int endIndex) {
        return startIndex + (endIndex - startIndex) / 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }

        int temporaryValue = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temporaryValue;
    }

    public static void main(String[] args) {
        QuickSortDemo.main(args);
    }
}
