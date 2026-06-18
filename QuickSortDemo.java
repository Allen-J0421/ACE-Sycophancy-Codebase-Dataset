import java.util.Arrays;

public final class QuickSortDemo {

    private static final int[] DEFAULT_VALUES = {10, 7, 8, 9, 1, 5};

    private QuickSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = args.length == 0 ? DEFAULT_VALUES : parseValues(args);
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
