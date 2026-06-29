class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };

    private BinarySearch() {
    }

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
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static void assertSearchResult(int[] values, int target, int expectedIndex) {
        int actualIndex = binarySearch(values, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target + ", but got " + actualIndex);
        }
    }

    private static void runSelfTests() {
        SearchCase[] testCases = {
                new SearchCase(SAMPLE_VALUES, 10, 3),
                new SearchCase(SAMPLE_VALUES, 2, 0),
                new SearchCase(SAMPLE_VALUES, 40, 4),
                new SearchCase(SAMPLE_VALUES, 5, NOT_FOUND),
                new SearchCase(new int[] {}, 5, NOT_FOUND)
        };

        for (SearchCase testCase : testCases) {
            assertSearchResult(testCase.values(), testCase.target(), testCase.expectedIndex());
        }
    }

    public static void main(String[] args) {
        runSelfTests();
        printSearchResult(SAMPLE_VALUES, 10);
    }

    private record SearchCase(int[] values, int target, int expectedIndex) {
    }
}
