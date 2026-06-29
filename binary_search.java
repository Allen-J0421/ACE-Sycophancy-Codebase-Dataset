class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int candidate = values[mid];

            if (candidate == target) {
                return mid;
            }

            if (candidate < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static void printSearchResult(int[] values, int target) {
        int result = binarySearch(values, target);

        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }

    private static void assertSearchResult(int[] values, int target, int expectedIndex) {
        int actualIndex = binarySearch(values, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target + ", but got " + actualIndex);
        }
    }

    private static void runSelfTests() {
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 10, 3);
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 2, 0);
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 40, 4);
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 5, NOT_FOUND);
        assertSearchResult(new int[] {}, 5, NOT_FOUND);
    }

    public static void main(String[] args) {
        runSelfTests();
        printSearchResult(new int[] { 2, 3, 4, 10, 40 }, 10);
    }
}
