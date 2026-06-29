final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int comparison = compare(values[mid], target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
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

    private static int compare(int value, int target) {
        return Integer.compare(value, target);
    }

    private static String resultMessage(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    public static void main(String[] args) {
        int result = binarySearch(DEMO_VALUES, DEMO_TARGET);

        System.out.println(resultMessage(result));
    }
}
