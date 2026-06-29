final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] array, int target) {
        return search(array, target).index();
    }

    static SearchResult search(int[] array, int target) {
        SearchBounds bounds = SearchBounds.forArray(array);

        while (bounds.isValid()) {
            int mid = bounds.midpoint();
            int currentValue = array[mid];

            if (currentValue == target) {
                return SearchResult.found(mid);
            }

            bounds.movePast(mid, currentValue, target);
        }

        return SearchResult.notFound();
    }

    static boolean found(int result) {
        return SearchResult.fromIndex(result).found();
    }

    private static final class SearchBounds {
        private int low;
        private int high;

        private SearchBounds(int low, int high) {
            this.low = low;
            this.high = high;
        }

        static SearchBounds forArray(int[] array) {
            return new SearchBounds(0, array.length - 1);
        }

        boolean isValid() {
            return low <= high;
        }

        int midpoint() {
            return low + (high - low) / 2;
        }

        void movePast(int index, int value, int target) {
            if (value < target) {
                low = index + 1;
            } else {
                high = index - 1;
            }
        }
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }
}

final class SearchResult {
    private static final int NOT_FOUND_INDEX = -1;

    private final int index;
    private final boolean found;

    private SearchResult(int index, boolean found) {
        this.index = index;
        this.found = found;
    }

    static SearchResult found(int index) {
        return new SearchResult(index, true);
    }

    static SearchResult notFound() {
        return new SearchResult(NOT_FOUND_INDEX, false);
    }

    static SearchResult fromIndex(int index) {
        return index == NOT_FOUND_INDEX ? notFound() : found(index);
    }

    int index() {
        return index;
    }

    boolean found() {
        return found;
    }
}

final class BinarySearchDemo {
    private static final int[] SAMPLE_ARRAY = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        SearchResult result = BinarySearch.search(SAMPLE_ARRAY, SAMPLE_TARGET);

        System.out.println(SearchResultFormatter.format(result));
    }
}

final class SearchResultFormatter {
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private SearchResultFormatter() {
    }

    static String format(int result) {
        return format(SearchResult.fromIndex(result));
    }

    static String format(SearchResult result) {
        return result.found()
                ? FOUND_MESSAGE_PREFIX + result.index()
                : NOT_FOUND_MESSAGE;
    }
}
