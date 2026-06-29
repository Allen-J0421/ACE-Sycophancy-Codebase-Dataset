final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    public static void main(String[] args) {
        runSampleSearch();
    }

    private static void runSampleSearch() {
        int result = binarySearch(sampleArray(), SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static int[] sampleArray() {
        return new int[] {2, 3, 4, 10, 40};
    }

    static int binarySearch(int[] array, int target) {
        int left = 0;
        int right = array.length - 1;

        while (left <= right) {
            int mid = midpoint(left, right);
            int value = array[mid];

            if (isMatch(value, target)) {
                return mid;
            }

            if (isBeforeTarget(value, target)) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
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
