import java.util.Arrays;
import java.util.StringJoiner;

public final class BubbleSort {

    private BubbleSort() {
        // Utility class.
    }

    public static void sortInPlace(int[] values) {
        validate(values);
        sortValuesInPlace(values);
    }

    public static void bubbleSort(int[] values) {
        sortInPlace(values);
    }

    public static int[] sortedCopy(int[] values) {
        validate(values);

        int[] copy = Arrays.copyOf(values, values.length);
        sortValuesInPlace(copy);
        return copy;
    }

    private static int[] createDemoValues() {
        return new int[] { 64, 34, 25, 12, 22, 11, 90 };
    }

    private static void validate(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static void sortValuesInPlace(int[] values) {
        int end = values.length - 1;
        while (end > 0) {
            int lastSwapIndex = bubblePass(values, end);
            if (lastSwapIndex == 0) {
                return;
            }
            end = lastSwapIndex - 1;
        }
    }

    private static int bubblePass(int[] values, int endExclusive) {
        int lastSwapIndex = 0;

        for (int index = 0; index < endExclusive; index++) {
            if (isOutOfOrder(values, index)) {
                swap(values, index, index + 1);
                lastSwapIndex = index + 1;
            }
        }

        return lastSwapIndex;
    }

    private static boolean isOutOfOrder(int[] values, int index) {
        return values[index] > values[index + 1];
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temp = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temp;
    }

    private static String formatArray(int[] values) {
        StringJoiner joiner = new StringJoiner(" ");

        for (int value : values) {
            joiner.add(Integer.toString(value));
        }

        return joiner.toString();
    }

    private static void runDemo() {
        int[] values = createDemoValues();
        sortValuesInPlace(values);
        System.out.println("Sorted array: ");
        System.out.println(formatArray(values));
    }

    public static void main(String[] args) {
        runDemo();
    }
}
