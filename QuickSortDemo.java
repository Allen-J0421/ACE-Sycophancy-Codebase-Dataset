import java.util.Arrays;

public class QuickSortDemo {

    public static void main(String[] args) {
        int[] arr;
        try {
            arr = args.length > 0 ? parseArgs(args) : new int[]{10, 7, 8, 9, 1, 5};
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
            return;
        }

        Sorter sorter = new QuickSort();
        sorter.sort(arr);
        System.out.println(Arrays.toString(arr));
    }

    private static int[] parseArgs(String[] args) {
        int[] arr = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                arr[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + args[i] + "' is not a valid integer");
            }
        }
        return arr;
    }
}
