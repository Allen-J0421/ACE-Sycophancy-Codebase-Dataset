class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        SearchRange range = new SearchRange(values.length);

        while (range.hasValues()) {
            int mid = range.midpoint();
            int midValue = values[mid];

            if (isMatch(midValue, target)) {
                return mid;
            }

            if (isBelowTarget(midValue, target)) {
                range.discardLeftThrough(mid);
            } else {
                range.discardRightThrough(mid);
            }
        }

        return NOT_FOUND;
    }

    private static boolean isMatch(int value, int target) {
        return value == target;
    }

    private static boolean isBelowTarget(int value, int target) {
        return value < target;
    }

    public static void main(String[] args) {
        int result = binarySearch(DEMO_VALUES, DEMO_TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private static class SearchRange {
        private int low;
        private int high;

        SearchRange(int size) {
            low = 0;
            high = size - 1;
        }

        boolean hasValues() {
            return low <= high;
        }

        int midpoint() {
            return low + (high - low) / 2;
        }

        void discardLeftThrough(int index) {
            low = index + 1;
        }

        void discardRightThrough(int index) {
            high = index - 1;
        }
    }
}
