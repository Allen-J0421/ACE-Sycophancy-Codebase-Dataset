import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(final int[] sortedValues, final int target) {
        Objects.requireNonNull(sortedValues, "sortedValues must not be null");

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            final int mid = low + (high - low) / 2;
            final int midValue = sortedValues[mid];

            if (midValue == target) {
                return mid;
            }

            if (midValue < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(final String[] args) {
        BinarySearchDemo.main(args);
    }
}
