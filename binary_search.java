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

    public static void main(String[] args) {
        SearchCase[] cases = {
            new SearchCase("finds middle element", new int[] {2, 3, 4, 10, 40}, 10, 3),
            new SearchCase("finds first element", new int[] {2, 3, 4, 10, 40}, 2, 0),
            new SearchCase("finds last element", new int[] {2, 3, 4, 10, 40}, 40, 4),
            new SearchCase("handles missing lower bound", new int[] {2, 3, 4, 10, 40}, 1, NOT_FOUND),
            new SearchCase("handles missing upper bound", new int[] {2, 3, 4, 10, 40}, 50, NOT_FOUND),
            new SearchCase("handles empty array", new int[] {}, 10, NOT_FOUND),
            new SearchCase("handles single matching element", new int[] {10}, 10, 0),
            new SearchCase("handles single missing element", new int[] {10}, 7, NOT_FOUND)
        };

        for (SearchCase testCase : cases) {
            assertSearch(testCase);
        }

        System.out.println("All binary search tests passed");
    }

    private static void assertSearch(SearchCase testCase) {
        int actual = BinarySearch.binarySearch(testCase.values(), testCase.target());

        if (actual != testCase.expected()) {
            throw new AssertionError(
                    testCase.name() + ": expected " + testCase.expected() + " but got " + actual);
        }
    }

    private record SearchCase(String name, int[] values, int target, int expected) {
    }
}
