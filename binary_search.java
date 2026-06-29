final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        return search(sortedValues, target).indexOrNotFound();
    }

    static SearchResult search(int[] sortedValues, int target) {
        int[] values = java.util.Objects.requireNonNull(sortedValues, "sortedValues");
        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int midpoint = midpoint(left, right);
            int midpointValue = values[midpoint];

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

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
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
    private static final SearchResult NOT_FOUND = new SearchResult(NOT_FOUND_INDEX);

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
        return NOT_FOUND;
    }

    boolean isFound() {
        return index != NOT_FOUND_INDEX;
    }

    int indexOrNotFound() {
        return index;
    }
}

final class BinarySearchTest {
    private static final int NOT_FOUND = -1;

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        for (SearchCase searchCase : searchCases()) {
            assertSearch(searchCase);
        }
        assertRejectsNullInput();

        System.out.println("BinarySearchTest passed");
    }

    private static SearchCase[] searchCases() {
        return new SearchCase[] {
                new SearchCase(new int[] {}, 10, NOT_FOUND),
                new SearchCase(new int[] {10}, 10, 0),
                new SearchCase(new int[] {10}, 5, NOT_FOUND),
                new SearchCase(new int[] {2, 3, 4, 10, 40}, 2, 0),
                new SearchCase(new int[] {2, 3, 4, 10, 40}, 10, 3),
                new SearchCase(new int[] {2, 3, 4, 10, 40}, 40, 4),
                new SearchCase(new int[] {2, 3, 4, 10, 40}, 5, NOT_FOUND),
        };
    }

    private static void assertSearch(SearchCase searchCase) {
        int actualIndex = BinarySearch.binarySearch(searchCase.sortedValues, searchCase.target);
        SearchResult result = BinarySearch.search(searchCase.sortedValues, searchCase.target);

        if (actualIndex != searchCase.expectedIndex) {
            throw new AssertionError(
                    "Expected binarySearch to return " + searchCase.expectedIndex + " but got " + actualIndex);
        }

        if (result.indexOrNotFound() != searchCase.expectedIndex) {
            throw new AssertionError(
                    "Expected search result index to be "
                            + searchCase.expectedIndex
                            + " but got "
                            + result.indexOrNotFound());
        }

        if (result.isFound() != searchCase.expectsFoundResult()) {
            throw new AssertionError(
                    "SearchResult found state did not match expected index " + searchCase.expectedIndex);
        }
    }

    private static void assertRejectsNullInput() {
        try {
            BinarySearch.search(null, 10);
        } catch (NullPointerException exception) {
            if (!"sortedValues".equals(exception.getMessage())) {
                throw new AssertionError("Expected null input message to name sortedValues");
            }
            return;
        }

        throw new AssertionError("Expected search to reject null sortedValues");
    }

    private static final class SearchCase {
        private final int[] sortedValues;
        private final int target;
        private final int expectedIndex;

        private SearchCase(int[] sortedValues, int target, int expectedIndex) {
            this.sortedValues = sortedValues;
            this.target = target;
            this.expectedIndex = expectedIndex;
        }

        private boolean expectsFoundResult() {
            return expectedIndex != NOT_FOUND;
        }
    }
}
