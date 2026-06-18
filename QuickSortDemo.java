import java.util.Arrays;

public final class QuickSortDemo {

    private static final int[] DEFAULT_VALUES = {10, 7, 8, 9, 1, 5};

    private QuickSortDemo() {
    }

    public static void main(String[] args) {
        printSortedValues(resolveInputValues(args));
    }

    private static int[] resolveInputValues(String[] args) {
        if (args.length == 0) {
            return Arrays.copyOf(DEFAULT_VALUES, DEFAULT_VALUES.length);
        }

        return parseValues(args);
    }

    private static void printSortedValues(int[] values) {
        System.out.println(Arrays.toString(QuickSort.sortedCopy(values)));
    }

    private static int[] parseValues(String[] args) {
        int[] values = new int[args.length];

        for (int index = 0; index < args.length; index++) {
            values[index] = Integer.parseInt(args[index]);
        }

        return values;
    }
}
