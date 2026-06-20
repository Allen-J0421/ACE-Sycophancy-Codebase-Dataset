import java.util.Arrays;
import java.util.Random;

public final class CountingSortTest {
    private static final long RANDOM_SEED = 37L;
    private static final int RANDOM_CASES = 100;

    private CountingSortTest() {
    }

    public static void main(String[] args) {
        testSortedCopyHandlesEdgeCases();
        testSortedCopyDoesNotMutateInput();
        testCopyIntoWritesExpectedValues();
        testCopyIntoSupportsAliasedInputAndOutput();
        testCopyIntoDoesNotMutateSeparateInput();
        testSortInPlaceMutatesTheInput();
        testCompatibilityWrappers();
        testInvalidInputs();
        testRandomizedAgreementWithArraysSort();
    }

    private static void testSortedCopyHandlesEdgeCases() {
        assertSorted(new int[] {});
        assertSorted(new int[] {2, 5, 3, 0, 2, 3, 0, 3, -4, -1});
        assertSorted(new int[] {7});
        assertSorted(new int[] {4, 4, 4, 4});
    }

    private static void testCopyIntoWritesExpectedValues() {
        int[] input = {3, -1, 3, 0};
        int[] output = new int[input.length];

        CountingSort.copyInto(input, output);

        assertArrayEquals(new int[] {-1, 0, 3, 3}, output);
    }

    private static void testSortedCopyDoesNotMutateInput() {
        int[] values = {8, -3, 8, 1};
        int[] original = values.clone();

        CountingSort.sortedCopy(values);

        assertArrayEquals(original, values);
    }

    private static void testCopyIntoSupportsAliasedInputAndOutput() {
        int[] values = {9, -2, 9, 1, 0};

        CountingSort.copyInto(values, values);

        assertArrayEquals(new int[] {-2, 0, 1, 9, 9}, values);
    }

    private static void testCopyIntoDoesNotMutateSeparateInput() {
        int[] input = {6, -4, 2, 6};
        int[] original = input.clone();
        int[] output = new int[input.length];

        CountingSort.copyInto(input, output);

        assertArrayEquals(original, input);
        assertArrayEquals(new int[] {-4, 2, 6, 6}, output);
    }

    private static void testSortInPlaceMutatesTheInput() {
        int[] values = {3, -1, 3, 0};

        CountingSort.sortInPlace(values);

        assertArrayEquals(new int[] {-1, 0, 3, 3}, values);
    }

    @SuppressWarnings("deprecation")
    private static void testCompatibilityWrappers() {
        int[] input = {5, -3, 1, 5, 0};
        int[] expected = CountingSort.sortedCopy(input);

        assertArrayEquals(expected, CountingSort.countSort(input));
        assertArrayEquals(expected, CountingSort.sort(input));
    }

    private static void testInvalidInputs() {
        assertThrows(
            IllegalArgumentException.class,
            () -> CountingSort.copyInto(new int[] {1, 2}, new int[1])
        );
        assertThrows(
            NullPointerException.class,
            () -> CountingSort.sortedCopy(null)
        );
        assertThrows(
            NullPointerException.class,
            () -> CountingSort.copyInto(new int[] {1, 2}, null)
        );
    }

    private static void testRandomizedAgreementWithArraysSort() {
        Random random = new Random(RANDOM_SEED);

        for (int i = 0; i < RANDOM_CASES; i++) {
            int length = random.nextInt(20);
            int[] values = new int[length];

            for (int j = 0; j < values.length; j++) {
                values[j] = random.nextInt(41) - 20;
            }

            assertSorted(values);
        }
    }

    private static void assertSorted(int[] values) {
        int[] expected = sortedWithArraysSort(values);
        assertArrayEquals(expected, CountingSort.sortedCopy(values));
    }

    private static int[] sortedWithArraysSort(int[] values) {
        int[] expected = values.clone();
        Arrays.sort(expected);
        return expected;
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual)
            );
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expectedType,
        ThrowingRunnable action
    ) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(
                "Expected " + expectedType.getSimpleName()
                    + " but caught " + throwable.getClass().getSimpleName(),
                throwable
            );
        }

        throw new AssertionError(
            "Expected " + expectedType.getSimpleName() + " to be thrown."
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
