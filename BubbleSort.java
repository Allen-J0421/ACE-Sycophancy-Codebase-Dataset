import java.util.Objects;

public final class BubbleSort {
    private static final char VALUE_SEPARATOR = ' ';

    private BubbleSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        for (int i = 0; i < values.length - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < values.length - i - 1; j++) {
                if (values[j] > values[j + 1]) {
                    swap(values, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) {
                return;
            }
        }
    }

    public static void printArray(int[] values) {
        System.out.println(toDisplayString(values));
    }

    public static String toDisplayString(int[] values) {
        Objects.requireNonNull(values, "values");

        StringBuilder output = new StringBuilder();

        for (int value : values) {
            if (!output.isEmpty()) {
                output.append(VALUE_SEPARATOR);
            }
            output.append(value);
        }

        return output.toString();
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temporary = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporary;
    }
}
