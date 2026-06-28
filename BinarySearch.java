import java.util.Objects;

/**
 * Search utilities for sorted integer arrays.
 */
public final class BinarySearch {
    /**
     * Returned when the target value is absent from the searched array.
     */
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    /**
     * Use {@link #indexOf(int[], int)}.
     */
    @Deprecated
    public static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    /**
     * Returns the index of {@code target} in {@code sortedValues}, or {@link #NOT_FOUND}.
     *
     * <p>The input array must be sorted in ascending order.
     */
    public static int indexOf(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues");

        int low = 0;
        int high = sortedValues.length;

        while (low < high) {
            int mid = midpoint(low, high);
            int value = sortedValues[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }
}
