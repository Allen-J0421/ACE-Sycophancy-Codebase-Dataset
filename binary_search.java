final class BinarySearch {
    private static final int DEMO_TARGET = 10;
    private static final int NOT_FOUND_INDEX = -1;
    private static final String FOUND_MESSAGE = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    /**
     * Returns the index of {@code target} in an ascending sorted array, or
     * {@link #NOT_FOUND_INDEX} when the target is absent.
     */
    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int current = sortedNumbers[mid];

            if (current == target) {
                return mid;
            }

            if (current < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND_INDEX;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        System.out.println(demoSearchResult());
    }

    private static String demoSearchResult() {
        int[] numbers = { 2, 3, 4, 10, 40 };
        int result = binarySearch(numbers, DEMO_TARGET);
        return formatSearchResult(result);
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND_INDEX) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE + result;
    }
}
