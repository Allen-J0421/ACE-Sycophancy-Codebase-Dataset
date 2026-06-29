final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_ARRAY = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    public static void main(String[] args) {
        runSampleSearch();
    }

    private static void runSampleSearch() {
        int result = binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }

    static int binarySearch(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int value = array[mid];

            if (isMatch(value, target)) {
                return mid;
            }

            if (isBeforeTarget(value, target)) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static boolean isMatch(int value, int target) {
        return value == target;
    }

    private static boolean isBeforeTarget(int value, int target) {
        return value < target;
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
