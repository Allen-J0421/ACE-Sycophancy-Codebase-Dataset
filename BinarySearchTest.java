import java.util.Arrays;

public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertSearch(new int[] {}, 10, BinarySearch.NOT_FOUND);
        assertSearch(new int[] { 10 }, 10, 0);
        assertSearch(new int[] { 10 }, 5, BinarySearch.NOT_FOUND);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 10, 3);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 2, 0);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 40, 4);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 5, BinarySearch.NOT_FOUND);
        assertSearch(new int[] { -9, -3, 0, 7, 12 }, -3, 1);
    }

    private static void assertSearch(int[] values, int target, int expectedIndex) {
        int actualIndex = BinarySearch.binarySearch(values, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected target " + target
                            + " in " + Arrays.toString(values)
                            + " at index " + expectedIndex
                            + ", but was " + actualIndex);
        }
    }
}
