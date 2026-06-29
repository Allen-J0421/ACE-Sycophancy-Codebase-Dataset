import java.util.Objects;

public final class BinarySearch {
    private static final int NOT_FOUND_INDEX = -1;

    private BinarySearch() {
    }

    /**
     * Searches a sorted array for the target value.
     *
     * @param values sorted values to search
     * @param target value to find
     * @return index of the target, or -1 when the target is not present
     */
    public static int binarySearch(int[] values, int target) {
        Objects.requireNonNull(values, "values");

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int candidate = values[mid];

            if (candidate == target) {
                return mid;
            }

            if (candidate < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND_INDEX;
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND_INDEX) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;

        int result = binarySearch(values, target);
        System.out.println(formatSearchResult(result));
    }
}
