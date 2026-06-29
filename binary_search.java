final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return search(values, target).index();
    }

    private static SearchResult search(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (hasSearchRange(low, high)) {
            int mid = midpoint(low, high);
            int candidate = values[mid];

            if (candidate == target) {
                return SearchResult.found(mid);
            }

            if (candidate < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }

    private static boolean hasSearchRange(int low, int high) {
        return low <= high;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        SearchResult result = search(DEMO_VALUES, DEMO_TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(SearchResult result) {
        if (!result.wasFound()) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result.index();
    }

    private static final class SearchResult {
        private final int index;

        private SearchResult(int index) {
            this.index = index;
        }

        private static SearchResult found(int index) {
            return new SearchResult(index);
        }

        private static SearchResult notFound() {
            return new SearchResult(NOT_FOUND);
        }

        private boolean wasFound() {
            return index != NOT_FOUND;
        }

        private int index() {
            return index;
        }
    }
}
