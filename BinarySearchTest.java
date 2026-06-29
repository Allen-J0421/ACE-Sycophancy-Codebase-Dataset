final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertSearch(sortedValues(), 10, 3);
        assertSearch(sortedValues(), 2, 0);
        assertSearch(sortedValues(), 40, 4);
        assertSearch(sortedValues(), 5, -1);
        assertSearch(new int[] {}, 10, -1);
    }

    private static int[] sortedValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static void assertSearch(int[] sortedValues, int target, int expectedIndex) {
        int actualIndex = BinarySearch.binarySearch(sortedValues, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target + ", but got " + actualIndex);
        }
    }
}
