import java.util.Arrays;

public final class BubbleSort {

    private BubbleSort() {
        // Utility class.
    }

    public static void bubbleSort(int[] values) {
        validate(values);
        sortInPlace(values);
    }

    public static int[] sortedCopy(int[] values) {
        validate(values);

        int[] copy = Arrays.copyOf(values, values.length);
        sortInPlace(copy);
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

    private static void sortInPlace(int[] values) {
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
        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(' ');
            }
            builder.append(values[index]);
        }

        return builder.toString();
    }

    private static void runDemo() {
        int[] values = createDemoValues();
        bubbleSort(values);
        System.out.println("Sorted array: ");
        System.out.println(formatArray(values));
    }

    public static void main(String[] args) {
        runDemo();
    }
}
