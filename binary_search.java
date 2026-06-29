final class BinarySearch {
    private static final int[] DEMO_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final int NOT_FOUND = -1;
    private static final String FOUND_MESSAGE = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
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

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_NUMBERS, DEMO_TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE + result;
    }
}
