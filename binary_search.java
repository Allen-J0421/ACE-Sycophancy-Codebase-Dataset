final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return indexOf(values, target);
    }

    static int indexOf(int[] values, int target) {
        return search(values, target).indexOrNotFound();
    }

    static SearchResult search(int[] values, int target) {
        requireValues(values);

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int middle = midpoint(low, high);
            int currentValue = values[middle];
            int comparison = Integer.compare(currentValue, target);

            if (comparison == 0) {
                return SearchResult.found(middle);
            }

            if (comparison < 0) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return SearchResult.notFound();
    }

    public static void main(String[] args) {
        System.out.println(runDemo());
    }

    private static String runDemo() {
        int[] values = { 2, 3, 4, 10, 40 };
        SearchResult result = search(values, DEMO_TARGET);

        return formatSearchResult(result);
    }

    private static void requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(SearchResult result) {
        if (!result.isFound()) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result.index();
    }

    static final class SearchResult {
        private static final SearchResult NOT_FOUND_RESULT = new SearchResult(NOT_FOUND);

        private final int index;

        private SearchResult(int index) {
            this.index = index;
        }

        private static SearchResult found(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("index must not be negative");
            }

            return new SearchResult(index);
        }

        private static SearchResult notFound() {
            return NOT_FOUND_RESULT;
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
}
