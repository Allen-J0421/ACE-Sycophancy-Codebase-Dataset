import java.util.Arrays;

public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        SearchCase[] searchCases = {
                new SearchCase(new int[] {}, 10, BinarySearch.NOT_FOUND),
                new SearchCase(new int[] { 10 }, 10, 0),
                new SearchCase(new int[] { 10 }, 5, BinarySearch.NOT_FOUND),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 10, 3),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 2, 0),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 40, 4),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 5, BinarySearch.NOT_FOUND),
                new SearchCase(new int[] { -9, -3, 0, 7, 12 }, -3, 1),
        };

        for (SearchCase searchCase : searchCases) {
            assertSearch(searchCase);
        }
    }

    private static void assertSearch(SearchCase searchCase) {
        int actualIndex = BinarySearch.binarySearch(searchCase.values, searchCase.target);

        if (actualIndex != searchCase.expectedIndex) {
            throw new AssertionError(
                    "Expected target " + searchCase.target
                            + " in " + Arrays.toString(searchCase.values)
                            + " at index " + searchCase.expectedIndex
                            + ", but was " + actualIndex);
        }
    }

    private static final class SearchCase {
        private final int[] values;
        private final int target;
        private final int expectedIndex;

        private SearchCase(int[] values, int target, int expectedIndex) {
            this.values = values;
            this.target = target;
            this.expectedIndex = expectedIndex;
        }
    }
}
