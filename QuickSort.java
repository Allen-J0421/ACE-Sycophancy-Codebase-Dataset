import java.util.Arrays;
import java.util.Objects;

public final class QuickSort {
    private static final int INSERTION_SORT_THRESHOLD = 16;

    private QuickSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        sortRange(values, 0, values.length);
    }

    public static void sort(int[] values, int fromIndex, int toIndexExclusive) {
        Objects.requireNonNull(values, "values");
        sortRange(values, fromIndex, toIndexExclusive);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");
        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void sortRange(int[] values, int fromIndex, int toIndexExclusive) {
        Objects.checkFromToIndex(fromIndex, toIndexExclusive, values.length);
        if (toIndexExclusive - fromIndex < 2) {
            return;
        }

        quickSort(values, fromIndex, toIndexExclusive);
    }

    private static void quickSort(int[] values, int left, int rightExclusive) {
        RangeStack pendingRanges = new RangeStack();
        pendingRanges.push(left, rightExclusive);
        PartitionBounds partition = new PartitionBounds();

        while (pendingRanges.hasPendingRanges()) {
            left = pendingRanges.popLeft();
            rightExclusive = pendingRanges.popRightExclusive();

            while (rightExclusive - left > INSERTION_SORT_THRESHOLD) {
                partition(values, left, rightExclusive, partition);

                int lessThanLength = partition.lessThanEndExclusive - left;
                int greaterThanLength = rightExclusive - partition.greaterThanStart;

                if (lessThanLength < greaterThanLength) {
                    if (greaterThanLength > 1) {
                        pendingRanges.push(partition.greaterThanStart, rightExclusive);
                    }
                    rightExclusive = partition.lessThanEndExclusive;
                } else {
                    if (lessThanLength > 1) {
                        pendingRanges.push(left, partition.lessThanEndExclusive);
                    }
                    left = partition.greaterThanStart;
                }
            }

            insertionSort(values, left, rightExclusive);
        }
    }

    private static void partition(int[] values, int left, int rightExclusive, PartitionBounds partition) {
        int pivot = choosePivotValue(values, left, rightExclusive);
        int lessThan = left;
        int index = left;
        int greaterThan = rightExclusive - 1;

        while (index <= greaterThan) {
            int current = values[index];
            if (current < pivot) {
                swap(values, lessThan, index);
                lessThan++;
                index++;
            } else if (current > pivot) {
                swap(values, index, greaterThan);
                greaterThan--;
            } else {
                index++;
            }
        }

        partition.lessThanEndExclusive = lessThan;
        partition.greaterThanStart = greaterThan + 1;
    }

    private static int choosePivotValue(int[] values, int left, int rightExclusive) {
        int middle = left + ((rightExclusive - left - 1) >>> 1);
        int right = rightExclusive - 1;

        int leftValue = values[left];
        int middleValue = values[middle];
        int rightValue = values[right];

        if (leftValue < middleValue) {
            if (middleValue < rightValue) {
                return middleValue;
            }
            return Math.max(leftValue, rightValue);
        }

        if (leftValue < rightValue) {
            return leftValue;
        }

        return Math.max(middleValue, rightValue);
    }

    private static void insertionSort(int[] values, int left, int rightExclusive) {
        for (int index = left + 1; index < rightExclusive; index++) {
            int currentValue = values[index];
            int position = index - 1;

            while (position >= left && values[position] > currentValue) {
                values[position + 1] = values[position];
                position--;
            }

            values[position + 1] = currentValue;
        }
    }

    private static void swap(int[] values, int first, int second) {
        int temp = values[first];
        values[first] = values[second];
        values[second] = temp;
    }

    private static final class RangeStack {
        private int[] lefts = new int[16];
        private int[] rights = new int[16];
        private int size;

        private void push(int left, int rightExclusive) {
            ensureCapacity();
            lefts[size] = left;
            rights[size] = rightExclusive;
            size++;
        }

        private int popLeft() {
            size--;
            return lefts[size];
        }

        private int popRightExclusive() {
            return rights[size];
        }

        private boolean hasPendingRanges() {
            return size > 0;
        }

        private void ensureCapacity() {
            if (size < lefts.length) {
                return;
            }

            lefts = Arrays.copyOf(lefts, lefts.length << 1);
            rights = Arrays.copyOf(rights, rights.length << 1);
        }
    }

    private static final class PartitionBounds {
        private int lessThanEndExclusive;
        private int greaterThanStart;
    }
}
