import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedNumbers, int target) {
        Objects.requireNonNull(sortedNumbers, "sortedNumbers");

        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            final int mid = low + (high - low) / 2;
            final int value = sortedNumbers[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        final int[] sortedNumbers = { 2, 3, 4, 10, 40 };
        final int result = binarySearch(sortedNumbers, DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
