final class BinarySearch {
    static final int NOT_FOUND = -1;

    private BinarySearch() {
        // Utility class.
    }

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midValue = sortedValues[mid];

            if (midValue == target) {
                return mid;
            }

            if (midValue < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }
}

final class BinarySearchDemo {
    private BinarySearchDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int result = BinarySearch.binarySearch(values, target);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        if (result == BinarySearch.NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}

final class BinarySearchTest {
    private BinarySearchTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        SearchCase[] cases = {
            new SearchCase(new int[] {}, 1, BinarySearch.NOT_FOUND),
            new SearchCase(new int[] {1}, 1, 0),
            new SearchCase(new int[] {1}, 2, BinarySearch.NOT_FOUND),
            new SearchCase(new int[] {1, 3, 5, 7}, 1, 0),
            new SearchCase(new int[] {1, 3, 5, 7}, 5, 2),
            new SearchCase(new int[] {1, 3, 5, 7}, 7, 3),
            new SearchCase(new int[] {1, 3, 5, 7}, 6, BinarySearch.NOT_FOUND)
        };

        for (SearchCase searchCase : cases) {
            assertSearchCase(searchCase);
        }

        System.out.println("All binary search tests passed.");
    }

    private static void assertSearchCase(SearchCase searchCase) {
        int actualIndex = BinarySearch.binarySearch(searchCase.values, searchCase.target);

        if (actualIndex != searchCase.expectedIndex) {
            throw new AssertionError(
                    "Expected index "
                            + searchCase.expectedIndex
                            + " for target "
                            + searchCase.target
                            + ", but got "
                            + actualIndex);
        }
    }

    private record SearchCase(int[] values, int target, int expectedIndex) {
    }
}
