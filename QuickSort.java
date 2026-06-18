import java.util.Arrays;
import java.util.Objects;

public final class QuickSort {

    private static final int INSERTION_SORT_THRESHOLD = 12;

    private QuickSort() {
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        sort(values, 0, values.length);
    }

    public static void sort(int[] values, int fromIndex, int toIndex) {
        Objects.requireNonNull(values, "values");
        validateRange(values.length, fromIndex, toIndex);

        if (toIndex - fromIndex < 2) {
            return;
        }

        quickSort(values, fromIndex, toIndex);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void quickSort(int[] values, int fromIndex, int toIndex) {
        while (toIndex - fromIndex > INSERTION_SORT_THRESHOLD) {
            Partition partition = partition(values, fromIndex, toIndex, values[selectPivotIndex(values, fromIndex, toIndex)]);
            int leftPartitionSize = partition.lessThanEnd - fromIndex;
            int rightPartitionSize = toIndex - partition.greaterThanStart;

            if (leftPartitionSize < rightPartitionSize) {
                quickSort(values, fromIndex, partition.lessThanEnd);
                fromIndex = partition.greaterThanStart;
            } else {
                quickSort(values, partition.greaterThanStart, toIndex);
                toIndex = partition.lessThanEnd;
            }
        }

        insertionSort(values, fromIndex, toIndex);
    }

    private static Partition partition(int[] values, int fromIndex, int toIndex, int pivotValue) {
        int lessThanBoundary = fromIndex;
        int currentIndex = fromIndex;
        int greaterThanBoundary = toIndex;

        while (currentIndex < greaterThanBoundary) {
            if (values[currentIndex] < pivotValue) {
                swap(values, lessThanBoundary, currentIndex);
                lessThanBoundary++;
                currentIndex++;
            } else if (values[currentIndex] > pivotValue) {
                greaterThanBoundary--;
                swap(values, currentIndex, greaterThanBoundary);
            } else {
                currentIndex++;
            }
        }

        return new Partition(lessThanBoundary, greaterThanBoundary);
    }

    private static int selectPivotIndex(int[] values, int fromIndex, int toIndex) {
        int lastIndex = toIndex - 1;
        int middleIndex = fromIndex + ((toIndex - fromIndex) >>> 1);
        return medianOfThreeIndex(values, fromIndex, middleIndex, lastIndex);
    }

    private static int medianOfThreeIndex(int[] values, int firstIndex, int secondIndex, int thirdIndex) {
        int firstValue = values[firstIndex];
        int secondValue = values[secondIndex];
        int thirdValue = values[thirdIndex];

        if (firstValue < secondValue) {
            if (secondValue < thirdValue) {
                return secondIndex;
            }

            return firstValue < thirdValue ? thirdIndex : firstIndex;
        }

        if (firstValue < thirdValue) {
            return firstIndex;
        }

        return secondValue < thirdValue ? thirdIndex : secondIndex;
    }

    private static void insertionSort(int[] values, int fromIndex, int toIndex) {
        for (int index = fromIndex + 1; index < toIndex; index++) {
            int currentValue = values[index];
            int insertionIndex = index - 1;

            while (insertionIndex >= fromIndex && values[insertionIndex] > currentValue) {
                values[insertionIndex + 1] = values[insertionIndex];
                insertionIndex--;
            }

            values[insertionIndex + 1] = currentValue;
        }
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        if (leftIndex == rightIndex) {
            return;
        }

        int temporaryValue = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporaryValue;
    }

    private static void validateRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + length);
        }

        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") must be <= toIndex (" + toIndex + ")");
        }
    }

    private static final class Partition {

        private final int lessThanEnd;
        private final int greaterThanStart;

        private Partition(int lessThanEnd, int greaterThanStart) {
            this.lessThanEnd = lessThanEnd;
            this.greaterThanStart = greaterThanStart;
        }
    }
}
