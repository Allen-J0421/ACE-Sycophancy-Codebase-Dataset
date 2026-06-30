class BinarySearchTest {
    public static void main(String[] args) {
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 10, 3);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 5, -1);
        assertSearch(new int[] {}, 10, -1);
        assertSearch(new int[] { 10 }, 10, 0);
        assertSearch(new int[] { 10 }, 4, -1);

        System.out.println("All BinarySearch tests passed");
    }

    private static void assertSearch(int[] numbers, int target, int expectedIndex) {
        int actualIndex = BinarySearch.binarySearch(numbers, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target
                            + ", but got " + actualIndex);
        }
    }
}
