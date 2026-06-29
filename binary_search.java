class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static SearchResult search(int[] sortedValues, int target) {
        int resultIndex = binarySearch(sortedValues, target);

        if (isFound(resultIndex)) {
            return SearchResult.foundAt(resultIndex);
        }

        return SearchResult.notFound();
    }

    static int binarySearch(int[] sortedValues, int target) {
        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int middle = middleIndex(left, right);
            int candidate = sortedValues[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    static boolean isFound(int resultIndex) {
        return resultIndex != NOT_FOUND;
    }

    private static int middleIndex(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}

class BinarySearchDemo {
    private static final int[] SORTED_VALUES = { 2, 3, 4, 10, 40 };
    private static final int TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        SearchResult result = BinarySearch.search(SORTED_VALUES, TARGET);

        System.out.println(SearchResultFormatter.format(result));
    }
}

final class SearchResult {
    private static final SearchResult NOT_FOUND = new SearchResult(-1);

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult foundAt(int index) {
        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return NOT_FOUND;
    }

    boolean wasFound() {
        return index >= 0;
    }

    int index() {
        if (!wasFound()) {
            throw new IllegalStateException("Search result has no index.");
        }

        return index;
    }
}

final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(SearchResult result) {
        if (!result.wasFound()) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result.index();
    }
}
