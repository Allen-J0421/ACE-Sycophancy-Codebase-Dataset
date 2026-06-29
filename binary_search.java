final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedNumbers, int target) {
        int left = 0;
        int right = sortedNumbers.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);
            int value = sortedNumbers[middle];

            if (value == target) {
                return middle;
            }

            if (value < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    static String formatSearchResult(int result) {
        if (isNotFound(result)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private static boolean isNotFound(int result) {
        return result == NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int[] sortedNumbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedNumbers, target);

        System.out.println(formatSearchResult(result));
    }
}
