import java.util.Objects;

public final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    /**
     * Searches a sorted array for the target value.
     *
     * @return the matching index, or {@code -1} when the target is absent
     */
    public static int binarySearch(int[] values, int target) {
        Objects.requireNonNull(values, "values");

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int current = values[mid];

            if (current == target) {
                return mid;
            }

            if (current < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    public static void main(String[] args) {
        int result = binarySearch(sampleValues(), SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }
}
