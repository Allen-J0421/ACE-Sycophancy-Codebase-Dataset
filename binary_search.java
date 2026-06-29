final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return search(values, target).indexOrNotFound();
    }

    private static SearchResult search(int[] values, int target) {
        SearchRange range = new SearchRange(values.length);

        while (range.hasValues()) {
            int mid = range.midpoint();
            int midValue = values[mid];

            if (isMatch(midValue, target)) {
                return SearchResult.foundAt(mid);
            }

            if (isBelowTarget(midValue, target)) {
                range.discardLeftThrough(mid);
            } else {
                range.discardRightThrough(mid);
            }
        }

        return SearchResult.notFound();
    }

    private static boolean isMatch(int value, int target) {
        return value == target;
    }

    private static boolean isBelowTarget(int value, int target) {
        return value < target;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        SearchResult result = search(demoValues(), DEMO_TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static int[] demoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(SearchResult result) {
        if (!result.isFound()) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result.index();
    }

    private static final class SearchResult {
        private final int index;

        private SearchResult(int index) {
            this.index = index;
        }

        static SearchResult foundAt(int index) {
            return new SearchResult(index);
        }

        static SearchResult notFound() {
            return new SearchResult(NOT_FOUND);
        }

        boolean isFound() {
            return index != NOT_FOUND;
        }

        int index() {
            return index;
        }

        int indexOrNotFound() {
            return index;
        }
    }

    private static final class SearchRange {
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
