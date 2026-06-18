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
        Range range = validateRange(values.length, fromIndex, toIndex);

        if (range.length() < 2) {
            return;
        }

        quickSort(values, range);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void quickSort(int[] values, Range range) {
        while (range.length() > INSERTION_SORT_THRESHOLD) {
            Partition partition = partition(values, range, values[selectPivotIndex(values, range)]);

            if (partition.left().length() < partition.right().length()) {
                quickSort(values, partition.left());
                range = partition.right();
            } else {
                quickSort(values, partition.right());
                range = partition.left();
            }
        }

        insertionSort(values, range);
    }

    private static Partition partition(int[] values, Range range, int pivotValue) {
        int lessThanBoundary = range.fromIndex;
        int currentIndex = range.fromIndex;
        int greaterThanBoundary = range.toIndex;

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

        return new Partition(
                new Range(range.fromIndex, lessThanBoundary),
                new Range(greaterThanBoundary, range.toIndex));
    }

    private static int selectPivotIndex(int[] values, Range range) {
        int lastIndex = range.toIndex - 1;
        int middleIndex = range.fromIndex + (range.length() >>> 1);
        return medianOfThreeIndex(values, range.fromIndex, middleIndex, lastIndex);
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

    private static void insertionSort(int[] values, Range range) {
        for (int index = range.fromIndex + 1; index < range.toIndex; index++) {
            int currentValue = values[index];
            int insertionIndex = index - 1;

            while (insertionIndex >= range.fromIndex && values[insertionIndex] > currentValue) {
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

    private static Range validateRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + length);
        }

        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") must be <= toIndex (" + toIndex + ")");
        }

        return new Range(fromIndex, toIndex);
    }

    private static final class Partition {

        private final Range left;
        private final Range right;

        private Partition(Range left, Range right) {
            this.left = left;
            this.right = right;
        }

        private Range left() {
            return left;
        }

        private Range right() {
            return right;
        }
    }

    private static final class Range {

        private final int fromIndex;
        private final int toIndex;

        private Range(int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }

        private int length() {
            return toIndex - fromIndex;
        }
    }
}
