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
        assertSearch("finds middle element", new int[] {2, 3, 4, 10, 40}, 10, 3);
        assertSearch("finds first element", new int[] {2, 3, 4, 10, 40}, 2, 0);
        assertSearch("finds last element", new int[] {2, 3, 4, 10, 40}, 40, 4);
        assertSearch("handles missing lower bound", new int[] {2, 3, 4, 10, 40}, 1, NOT_FOUND);
        assertSearch("handles missing upper bound", new int[] {2, 3, 4, 10, 40}, 50, NOT_FOUND);
        assertSearch("handles empty array", new int[] {}, 10, NOT_FOUND);
        assertSearch("handles single matching element", new int[] {10}, 10, 0);
        assertSearch("handles single missing element", new int[] {10}, 7, NOT_FOUND);

        System.out.println("All binary search tests passed");
    }

    private static void assertSearch(String name, int[] values, int target, int expected) {
        int actual = BinarySearch.binarySearch(values, target);

        if (actual != expected) {
            throw new AssertionError(name + ": expected " + expected + " but got " + actual);
        }
    }
}
