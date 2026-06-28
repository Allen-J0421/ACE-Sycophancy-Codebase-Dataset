import java.util.Objects;

public final class BinarySearch {
    /**
     * Return value used when the target does not exist in the input array.
     */
    public static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final int DEMO_TARGET = 10;

    private BinarySearch() {
    }

    /**
     * Returns the index of {@code target} in a sorted array, or {@link #NOT_FOUND} when absent.
     *
     * @throws NullPointerException when {@code numbers} is null
     */
    public static int binarySearch(int[] numbers, int target) {
        Objects.requireNonNull(numbers, "numbers");

        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            final int mid = low + (high - low) / 2;

            if (numbers[mid] == target) {
                return mid;
            }

            if (numbers[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        final int result = binarySearch(demoNumbers(), DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static int[] demoNumbers() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
