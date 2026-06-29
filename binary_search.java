final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return search(values, target).index();
    }

    static SearchResult search(int[] values, int target) {
        SearchBounds bounds = SearchBounds.forValues(values);

        while (bounds.hasValues()) {
            int mid = bounds.midpoint();
            int candidate = values[mid];

            if (candidate == target) {
                return SearchResult.found(mid);
            }

            if (candidate < target) {
                bounds.discardLowerHalf(mid);
            } else {
                bounds.discardUpperHalf(mid);
            }
        }

        return SearchResult.notFound();
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }

    static final class SearchResult {
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

        boolean wasFound() {
            return index != NOT_FOUND;
        }

        int index() {
            return index;
        }
    }

    private static final class SearchBounds {
        private int low;
        private int high;

        private SearchBounds(int low, int high) {
            this.low = low;
            this.high = high;
        }

        private static SearchBounds forValues(int[] values) {
            return new SearchBounds(0, values.length - 1);
        }

        private boolean hasValues() {
            return low <= high;
        }

        private int midpoint() {
            return low + (high - low) / 2;
        }

        private void discardLowerHalf(int mid) {
            low = mid + 1;
        }

        private void discardUpperHalf(int mid) {
            high = mid - 1;
        }
    }
}

final class BinarySearchDemo {
    private static final int[] VALUES = { 2, 3, 4, 10, 40 };
    private static final int TARGET = 10;

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        BinarySearch.SearchResult result = BinarySearch.search(VALUES, TARGET);
        System.out.println(SearchResultFormatter.format(result));
    }
}

final class SearchResultFormatter {
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private SearchResultFormatter() {
    }

    static String format(BinarySearch.SearchResult result) {
        if (!result.wasFound()) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result.index();
    }
}
