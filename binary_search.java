final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        return search(sortedValues, target).index();
    }

    static SearchResult search(int[] sortedValues, int target) {
        validateInput(sortedValues);

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (sortedValues[mid] == target) {
                return SearchResult.foundAt(mid);
            }

            if (sortedValues[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }

    private static void validateInput(int[] sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }
}

final class SearchResult {
    private static final int NOT_FOUND_INDEX = -1;

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult foundAt(int index) {
        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return new SearchResult(NOT_FOUND_INDEX);
    }

    boolean isFound() {
        return index != NOT_FOUND_INDEX;
    }

    int index() {
        return index;
    }
}

final class BinarySearchDemo {
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final int TARGET = 10;

    private BinarySearchDemo() {
    }

    static String formatSearchResult(SearchResult result) {
        if (result.isFound()) {
            return FOUND_MESSAGE_PREFIX + result.index();
        }

        return NOT_FOUND_MESSAGE;
    }

    public static void main(String[] args) {
        SearchResult result = BinarySearch.search(demoValues(), TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static int[] demoValues() {
        return new int[] {2, 3, 4, 10, 40};
    }
}
