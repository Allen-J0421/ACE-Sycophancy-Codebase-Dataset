import java.util.Objects;

public final class BinarySearch {
    /**
     * Return value used when the target is absent from the searched array.
     */
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    /**
     * Searches an ascending sorted array for the target value.
     *
     * @param sortedValues array sorted in ascending order
     * @param target value to search for
     * @return index of the target, or {@link #NOT_FOUND} when absent
     */
    public static int binarySearch(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues");

        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);
            int value = sortedValues[middle];

            if (value == target) {
                return middle;
            }

            if (value < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }
}
