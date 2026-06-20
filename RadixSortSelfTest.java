import java.util.Arrays;

final class RadixSortSelfTest {
    private RadixSortSelfTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        assertSorted(new int[] { 170, 45, 75, 90, 802, 24, 2, 66 });
        assertSorted(new int[] { -3, 0, -1, 8, -10, 5 });
        assertSorted(new int[] { Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE });
        System.out.println("RadixSortSelfTest passed");
    }

    private static void assertSorted(int[] input) {
        int[] actual = input.clone();
        int[] expected = input.clone();

        RadixSort.sort(actual);
        Arrays.sort(expected);

        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual));
        }
    }
}
