import java.util.Arrays;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        requireArray(values);
        sortRange(values, 0, values.length);
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        requireArray(values);
        requireRange(values.length, startInclusive, endExclusive);
        if (endExclusive - startInclusive < 2) {
            return;
        }

        for (int currentIndex = startInclusive + 1; currentIndex < endExclusive; currentIndex++) {
            insertValue(values, startInclusive, currentIndex);
        }
    }

    public static int[] sortedCopy(int[] values) {
        requireArray(values);

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    public static boolean isSorted(int[] values) {
        requireArray(values);

        for (int index = 1; index < values.length; index++) {
            if (values[index - 1] > values[index]) {
                return false;
            }
        }

        return true;
    }

    private static void insertValue(int[] values, int startInclusive, int currentIndex) {
        int valueToInsert = values[currentIndex];
        int insertionIndex = findInsertionIndex(values, startInclusive, currentIndex, valueToInsert);

        shiftRight(values, insertionIndex, currentIndex);
        values[insertionIndex] = valueToInsert;
    }

    private static int findInsertionIndex(
            int[] values,
            int startInclusive,
            int currentIndex,
            int valueToInsert) {
        int scanIndex = currentIndex - 1;

        while (scanIndex >= startInclusive && values[scanIndex] > valueToInsert) {
            scanIndex--;
        }

        return scanIndex + 1;
    }

    private static void shiftRight(int[] values, int startIndex, int endIndex) {
        int length = endIndex - startIndex;
        if (length > 0) {
            System.arraycopy(values, startIndex, values, startIndex + 1, length);
        }
    }

    private static void requireArray(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static void requireRange(int length, int startInclusive, int endExclusive) {
        if (startInclusive < 0 || endExclusive < startInclusive || endExclusive > length) {
            throw new IllegalArgumentException(
                    "invalid range: [" + startInclusive + ", " + endExclusive + ") for length " + length);
        }
    }
}
