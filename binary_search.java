final class BinarySearch {
    private static final int NOT_FOUND = -1;

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

    static boolean found(int index) {
        return index != NOT_FOUND;
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

    static final class SearchResult {
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
            return found(index);
        }

        int index() {
            return index;
        }
    }
}

final class BinarySearchDemo {
    private static final int TARGET = 10;

    private BinarySearchDemo() {
    }

    static String formatSearchResult(BinarySearch.SearchResult result) {
        if (result.isFound()) {
            return "Element is present at index " + result.index();
        }

        return "Element is not present in array";
    }

    public static void main(String[] args) {
        BinarySearch.SearchResult result = BinarySearch.search(demoValues(), TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static int[] demoValues() {
        return new int[] {2, 3, 4, 10, 40};
    }
}
