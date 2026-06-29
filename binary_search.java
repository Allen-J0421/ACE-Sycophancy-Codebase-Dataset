final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        SearchBounds bounds = SearchBounds.forValues(values);

        while (bounds.hasRemainingValues()) {
            int mid = bounds.midpoint();
            int comparison = compare(values[mid], target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                bounds.discardLowerHalf(mid);
            } else {
                bounds.discardUpperHalf(mid);
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

    private static boolean isFound(int result) {
        return result != NOT_FOUND;
    }

    private static final class SearchBounds {
        private int low;
        private int high;

        private SearchBounds(int valueCount) {
            low = 0;
            high = valueCount - 1;
        }

        static SearchBounds forValues(int[] values) {
            return new SearchBounds(values.length);
        }

        boolean hasRemainingValues() {
            return low <= high;
        }

        int midpoint() {
            return BinarySearch.midpoint(low, high);
        }

        void discardLowerHalf(int mid) {
            low = mid + 1;
        }

        void discardUpperHalf(int mid) {
            high = mid - 1;
        }
    }

    private static String resultMessage(int result) {
        if (isFound(result)) {
            return FOUND_MESSAGE_PREFIX + result;
        }

        return NOT_FOUND_MESSAGE;
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_VALUES, DEMO_TARGET);

        System.out.println(resultMessage(result));
    }

    public static void main(String[] args) {
        runDemo();
    }
}
