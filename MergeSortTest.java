import java.util.Arrays;

final class MergeSortTest {

    private MergeSortTest() {
    }

    public static void main(String[] args) {
        int[][] cases = {
                {},
                {1},
                {2, 1},
                {3, -1, 3, 0},
                {1, 2, 3, 4},
                {1, 3, 2, 4},
                {9, 7, 5, 3, 1},
        };

        for (int[] values : cases) {
            assertMatchesJavaSort(values);
        }

        MergeSort.sort(null);
    }

    private static void assertMatchesJavaSort(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);

        MergeSort.sort(input);
        if (!Arrays.equals(input, expected)) {
            throw new AssertionError("Expected " + Arrays.toString(expected)
                    + " but got " + Arrays.toString(input));
        }
    }
}
