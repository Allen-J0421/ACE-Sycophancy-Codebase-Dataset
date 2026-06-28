public final class BinarySearchTest {
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
            assertIndexOf(searchCase);
            assertCompatibilityMethod(searchCase);
        }

        System.out.println("All binary search tests passed.");
    }

    private static void assertIndexOf(SearchCase searchCase) {
        int actualIndex = BinarySearch.indexOf(searchCase.values, searchCase.target);

        if (actualIndex != searchCase.expectedIndex) {
            throw assertionError(searchCase, actualIndex, "indexOf");
        }
    }

    private static void assertCompatibilityMethod(SearchCase searchCase) {
        int actualIndex = BinarySearch.binarySearch(searchCase.values, searchCase.target);

        if (actualIndex != searchCase.expectedIndex) {
            throw assertionError(searchCase, actualIndex, "binarySearch");
        }
    }

    private static AssertionError assertionError(
            SearchCase searchCase,
            int actualIndex,
            String methodName) {
        return new AssertionError(
                methodName
                        + " expected index "
                        + searchCase.expectedIndex
                        + " for target "
                        + searchCase.target
                        + ", but got "
                        + actualIndex);
    }

    private record SearchCase(int[] values, int target, int expectedIndex) {
    }
}
