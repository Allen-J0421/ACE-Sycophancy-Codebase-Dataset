import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    public static int indexOf(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues must not be null");

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (sortedValues[mid] == target) {
                return mid;
            }

            if (sortedValues[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int result = binarySearch(values, target);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
