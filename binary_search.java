final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        return search(sortedValues, target).indexOrNotFound();
    }

    static SearchResult search(int[] sortedValues, int target) {
        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int midpoint = left + (right - left) / 2;
            int midpointValue = sortedValues[midpoint];

            if (midpointValue == target) {
                return SearchResult.foundAt(midpoint);
            }

            if (midpointValue < target) {
                left = midpoint + 1;
            } else {
                right = midpoint - 1;
            }
        }

        return SearchResult.notFound();
    }

    static boolean isFound(int resultIndex) {
        return SearchResult.fromIndex(resultIndex).isFound();
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }
}

final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        int[] sortedValues = {2, 3, 4, 10, 40};
        int target = 10;
        SearchResult result = BinarySearch.search(sortedValues, target);

        System.out.println(searchMessage(result));
    }

    private static String searchMessage(SearchResult result) {
        if (!result.isFound()) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result.indexOrNotFound();
    }
}

final class SearchResult {
    private static final int NOT_FOUND_INDEX = -1;

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult foundAt(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }

        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return new SearchResult(NOT_FOUND_INDEX);
    }

    static SearchResult fromIndex(int index) {
        if (index < NOT_FOUND_INDEX) {
            throw new IllegalArgumentException("index must be -1 or non-negative");
        }

        return new SearchResult(index);
    }

    boolean isFound() {
        return index != NOT_FOUND_INDEX;
    }

    int indexOrNotFound() {
        return index;
    }
}
