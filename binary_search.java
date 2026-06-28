class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_SORTED_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    static int binarySearch(int[] sortedArray, int target) {
        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int comparison = compareAt(sortedArray, mid, target);

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

    public static void main(String[] args) {
        runDemo();
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static int compareAt(int[] sortedArray, int index, int target) {
        return Integer.compare(sortedArray[index], target);
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_SORTED_ARRAY, DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
