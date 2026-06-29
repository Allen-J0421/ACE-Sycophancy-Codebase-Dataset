class BinarySearch {
    static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedArray, int target) {
        java.util.Objects.requireNonNull(sortedArray, "sortedArray");

        int left = 0;
        int right = sortedArray.length - 1;

        while (left <= right) {
            int midpoint = midpoint(left, right);
            int comparison = Integer.compare(sortedArray[midpoint], target);

            if (comparison == 0) {
                return midpoint;
            }

            if (comparison < 0) {
                left = midpoint + 1;
            } else {
                right = midpoint - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}

final class BinarySearchDemo {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        int result = BinarySearch.binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);

        System.out.println(SearchResultFormatter.format(result));
    }
}

final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}

final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertSearchFindsExistingValues();
        assertSearchReportsMissingValues();
        assertSearchHandlesEmptyArrays();

        System.out.println("All binary search tests passed");
    }

    private static void assertSearchFindsExistingValues() {
        int[] numbers = { 2, 3, 4, 10, 40 };

        assertEquals(0, BinarySearch.binarySearch(numbers, 2));
        assertEquals(3, BinarySearch.binarySearch(numbers, 10));
        assertEquals(4, BinarySearch.binarySearch(numbers, 40));
    }

    private static void assertSearchReportsMissingValues() {
        int[] numbers = { 2, 3, 4, 10, 40 };

        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(numbers, 1));
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(numbers, 11));
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(numbers, 100));
    }

    private static void assertSearchHandlesEmptyArrays() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(new int[0], 10));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
