import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

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
