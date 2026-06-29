final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int middle = midpoint(low, high);
            int candidate = sortedValues[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    static boolean isFound(int index) {
        return index != NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }
}

final class BinarySearchDemo {
    private static final SearchCase DEMO_CASE = new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 10);

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        printSearchResult(DEMO_CASE);
    }

    private static void printSearchResult(SearchCase searchCase) {
        int index = BinarySearch.binarySearch(searchCase.sortedValues(), searchCase.target());

        System.out.println(SearchResultFormatter.format(index));
    }
}

final class SearchCase {
    private final int[] sortedValues;
    private final int target;

    SearchCase(int[] sortedValues, int target) {
        this.sortedValues = sortedValues.clone();
        this.target = target;
    }

    int[] sortedValues() {
        return sortedValues.clone();
    }

    int target() {
        return target;
    }
}

final class SearchResultFormatter {
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private SearchResultFormatter() {
    }

    static String format(int index) {
        if (!BinarySearch.isFound(index)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
