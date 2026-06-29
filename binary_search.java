class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int middle = midpoint(low, high);
            int current = values[middle];

            if (current == target) {
                return middle;
            }

            if (current < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int result = binarySearch(values, target);

        System.out.println(formatResult(result));
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatResult(int result) {
        if (!isFound(result)) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static boolean isFound(int result) {
        return result != NOT_FOUND;
    }
}

class BinarySearchTest {
    private static final int NOT_FOUND = -1;
    private static final int[] SORTED_VALUES = {2, 3, 4, 10, 40};

    public static void main(String[] args) {
        for (SearchCase testCase : testCases()) {
            assertSearch(testCase);
        }

        System.out.println("All binary search tests passed");
    }

    private static SearchCase[] testCases() {
        return new SearchCase[] {
            new SearchCase("finds middle element", SORTED_VALUES, 10, 3),
            new SearchCase("finds first element", SORTED_VALUES, 2, 0),
            new SearchCase("finds last element", SORTED_VALUES, 40, 4),
            new SearchCase("handles missing lower bound", SORTED_VALUES, 1, NOT_FOUND),
            new SearchCase("handles missing upper bound", SORTED_VALUES, 50, NOT_FOUND),
            new SearchCase("handles empty array", new int[] {}, 10, NOT_FOUND),
            new SearchCase("handles single matching element", new int[] {10}, 10, 0),
            new SearchCase("handles single missing element", new int[] {10}, 7, NOT_FOUND)
        };
    }

    private static void assertSearch(SearchCase testCase) {
        int actual = BinarySearch.binarySearch(testCase.values, testCase.target);

        if (actual != testCase.expected) {
            throw new AssertionError(
                    testCase.name + ": expected " + testCase.expected + " but got " + actual);
        }
    }

    private static final class SearchCase {
        private final String name;
        private final int[] values;
        private final int target;
        private final int expected;

        private SearchCase(String name, int[] values, int target, int expected) {
            this.name = name;
            this.values = values;
            this.target = target;
            this.expected = expected;
        }
    }
}
