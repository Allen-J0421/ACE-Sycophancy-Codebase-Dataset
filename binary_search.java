class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int search(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int candidate = values[mid];
            int comparison = Integer.compare(candidate, target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    private static void assertSearchResult(int[] values, int target, int expectedIndex) {
        int actualIndex = search(values, target);

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
        int index = search(SAMPLE_VALUES, SAMPLE_TARGET);
        printSearchResult(index);
    }

    private record SearchCase(int[] values, int target, int expectedIndex) {
    }
}
