import java.util.Objects;

public final class BinarySearch {
    private static final int NOT_FOUND = -1;

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
        final int[] sortedValues = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = binarySearch(sortedValues, target);

        printSearchResult(result);
    }

    private static void printSearchResult(final int index) {
        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(final int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
