import java.util.ArrayDeque;
import java.util.Deque;
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

    private static void sortRange(int[] values, int fromIndex, int toIndexExclusive) {
        Objects.checkFromToIndex(fromIndex, toIndexExclusive, values.length);
        if (toIndexExclusive - fromIndex < 2) {
            return;
        }

        quickSort(values, fromIndex, toIndexExclusive);
    }

    private static void quickSort(int[] values, int left, int rightExclusive) {
        Deque<Range> pendingRanges = new ArrayDeque<>();
        pendingRanges.push(new Range(left, rightExclusive));

        while (!pendingRanges.isEmpty()) {
            Range range = pendingRanges.pop();
            left = range.left();
            rightExclusive = range.rightExclusive();

            while (rightExclusive - left > INSERTION_SORT_THRESHOLD) {
                Partition partition = partition(values, left, rightExclusive);
                Range lessThanRange = new Range(left, partition.lessThanEndExclusive());
                Range greaterThanRange = new Range(partition.greaterThanStart(), rightExclusive);

                if (lessThanRange.length() < greaterThanRange.length()) {
                    if (greaterThanRange.length() > 1) {
                        pendingRanges.push(greaterThanRange);
                    }
                    rightExclusive = lessThanRange.rightExclusive();
                } else {
                    if (lessThanRange.length() > 1) {
                        pendingRanges.push(lessThanRange);
                    }
                    left = greaterThanRange.left();
                }
            }

            insertionSort(values, left, rightExclusive);
        }
    }

    private static Partition partition(int[] values, int left, int rightExclusive) {
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

        return new Partition(lessThan, greaterThan + 1);
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

    private record Partition(int lessThanEndExclusive, int greaterThanStart) {}

    private record Range(int left, int rightExclusive) {
        private int length() {
            return rightExclusive - left;
        }
    }
}
