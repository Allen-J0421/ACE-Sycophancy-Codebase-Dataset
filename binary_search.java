final class BinarySearch {
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        SearchRange range = SearchRange.from(values);

        while (range.hasCandidates()) {
            int mid = range.midpoint();
            int currentValue = values[mid];
            int comparison = compare(currentValue, target);

            if (matchesTarget(comparison)) {
                return mid;
            }

            narrowSearchRange(range, mid, comparison);
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static int compare(int value, int target) {
        return Integer.compare(value, target);
    }

    private static boolean matchesTarget(int comparison) {
        return comparison == 0;
    }

    private static boolean isBelowTarget(int comparison) {
        return comparison < 0;
    }

    private static void narrowSearchRange(SearchRange range, int midpoint, int comparison) {
        if (isBelowTarget(comparison)) {
            range.discardAtOrBelow(midpoint);
        } else {
            range.discardAtOrAbove(midpoint);
        }
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_VALUES, DEMO_TARGET);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (isFound(result)) {
            return FOUND_MESSAGE_PREFIX + result;
        }

        return NOT_FOUND_MESSAGE;
    }

    private static boolean isFound(int result) {
        return result != NOT_FOUND;
    }

    private static final class SearchRange {
        private int low;
        private int high;

        private SearchRange(int low, int high) {
            this.low = low;
            this.high = high;
        }

        private static SearchRange from(int[] values) {
            return new SearchRange(0, values.length - 1);
        }

        private boolean hasCandidates() {
            return low <= high;
        }

        private int midpoint() {
            return low + (high - low) / 2;
        }

        private void discardAtOrBelow(int index) {
            low = index + 1;
        }

        private void discardAtOrAbove(int index) {
            high = index - 1;
        }
    }
}
