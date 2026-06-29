import java.util.Objects;

public final class BinarySearch {
    private static final int NOT_FOUND_INDEX = -1;
    private static final int[] SAMPLE_VALUES = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

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

    private static String searchResultMessage(int result) {
        if (result == NOT_FOUND_INDEX) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);
        System.out.println(searchResultMessage(result));
    }
}
