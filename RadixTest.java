import java.util.Arrays;
import java.util.Random;

class RadixTest {
    public static void main(String[] args) {
        sortsFullArray();
        sortsSignedValues();
        sortsLikeArraysSortForDeterministicSamples();
        sortsPrefixOnly();
        keepsEmptyAndSingleElementArraysValid();
        keepsMaximumLookupIndependentFromSortValidation();
        rejectsUnsupportedSortInputs();
        rejectsInvalidRanges();
        sortsSingleDigitPass();
    }

    private static void sortsFullArray() {
        int[] values = {5, 1, 3, 0, Integer.MAX_VALUE, 42};

        Radix.radixSort(values);

        assertArrayEquals(new int[] {0, 1, 3, 5, 42, Integer.MAX_VALUE}, values);
    }

    private static void sortsSignedValues() {
        int[] values = {0, -10, 5, Integer.MIN_VALUE, -1, Integer.MAX_VALUE, 42};

        Radix.radixSort(values);

        assertArrayEquals(new int[] {Integer.MIN_VALUE, -10, -1, 0, 5, 42, Integer.MAX_VALUE}, values);
    }

    private static void sortsLikeArraysSortForDeterministicSamples() {
        Random random = new Random(8675309L);

        for (int length = 0; length < 64; length++) {
            int[] values = new int[length];
            for (int i = 0; i < length; i++) {
                values[i] = random.nextInt();
            }

            int[] expected = values.clone();
            Arrays.sort(expected);
            Radix.radixSort(values);

            assertArrayEquals(expected, values);
        }
    }

    @SuppressWarnings("deprecation")
    private static void sortsPrefixOnly() {
        int[] values = {4, -3, 2, -1, 99};

        Radix.radixsort(values, 4);

        assertArrayEquals(new int[] {-3, -1, 2, 4, 99}, values);
    }

    private static void keepsEmptyAndSingleElementArraysValid() {
        int[] empty = {};
        Radix.radixSort(empty);
        assertArrayEquals(new int[] {}, empty);

        int[] single = {7};
        Radix.radixSort(single);
        assertArrayEquals(new int[] {7}, single);
    }

    private static void keepsMaximumLookupIndependentFromSortValidation() {
        int maximum = Radix.getMax(new int[] {-3, -1, -7}, 3);

        if (maximum != -1) {
            throw new AssertionError("Expected maximum -1 but got " + maximum);
        }
    }

    private static void rejectsUnsupportedSortInputs() {
        assertThrows(() -> Radix.radixSort(null));
        assertThrows(() -> Radix.countSort(new int[] {2, -1}, 2, 1));
        assertThrows(() -> Radix.countSort(new int[] {1, 2}, 2, 0));
        assertThrows(() -> Radix.getMax(new int[] {}, 0));
    }

    private static void rejectsInvalidRanges() {
        assertThrows(() -> Radix.radixSort(new int[] {1}, -1));
        assertThrows(() -> Radix.radixSort(new int[] {1}, 2));
        assertThrows(() -> Radix.print(new int[] {1}, 2));
    }

    private static void sortsSingleDigitPass() {
        int[] values = {21, 13, 12, 34};

        Radix.countSort(values, values.length, 1);

        assertArrayEquals(new int[] {21, 12, 13, 34}, values);
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual));
        }
    }

    private static void assertThrows(Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException");
    }
}
