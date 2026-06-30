class BinarySearchTest {
    public static void main(String[] args) {
        for (SearchCase searchCase : searchCases()) {
            assertSearch(searchCase);
        }

        System.out.println("All BinarySearch tests passed");
    }

    private static SearchCase[] searchCases() {
        return new SearchCase[] {
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 10, 3),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 5, -1),
                new SearchCase(new int[] {}, 10, -1),
                new SearchCase(new int[] { 10 }, 10, 0),
                new SearchCase(new int[] { 10 }, 4, -1),
        };
    }

    private static void assertSearch(SearchCase searchCase) {
        int actualIndex = BinarySearch.binarySearch(searchCase.numbers(), searchCase.target());

        if (actualIndex != searchCase.expectedIndex()) {
            throw new AssertionError(
                    "Expected index " + searchCase.expectedIndex()
                            + " for target " + searchCase.target()
                            + ", but got " + actualIndex);
        }
    }

    private record SearchCase(int[] numbers, int target, int expectedIndex) {
    }
}
