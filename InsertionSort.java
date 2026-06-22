import java.util.Arrays;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        requireArray(values);
        if (values.length < 2) {
            return;
        }

        for (int currentIndex = 1; currentIndex < values.length; currentIndex++) {
            insertValue(values, currentIndex);
        }
    }

    public static int[] sortedCopy(int[] values) {
        requireArray(values);

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void insertValue(int[] values, int currentIndex) {
        int valueToInsert = values[currentIndex];
        int insertionIndex = findInsertionIndex(values, currentIndex, valueToInsert);

        shiftRight(values, insertionIndex, currentIndex);
        values[insertionIndex] = valueToInsert;
    }

    private static int findInsertionIndex(int[] values, int currentIndex, int valueToInsert) {
        int scanIndex = currentIndex - 1;

        while (scanIndex >= 0 && values[scanIndex] > valueToInsert) {
            scanIndex--;
        }

        return scanIndex + 1;
    }

    private static void shiftRight(int[] values, int startIndex, int endIndex) {
        for (int index = endIndex; index > startIndex; index--) {
            values[index] = values[index - 1];
        }
    }

    public static String toDisplayString(int[] values) {
        requireArray(values);
        return Arrays.toString(values);
    }

    private static void requireArray(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    public static void main(String[] args) {
        int[] values = {12, 11, 13, 5, 6};

        System.out.println("Original: " + toDisplayString(values));
        System.out.println("Sorted:   " + toDisplayString(sortedCopy(values)));
    }
}
