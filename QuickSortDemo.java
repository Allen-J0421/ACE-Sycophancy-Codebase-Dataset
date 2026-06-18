import java.util.Arrays;

public class QuickSortDemo {

    public static void main(String[] args) {
        int[] arr = args.length > 0 ? parseArgs(args) : new int[]{10, 7, 8, 9, 1, 5};

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
                System.err.println("Error: '" + args[i] + "' is not a valid integer.");
                System.exit(1);
            }
        }
        return arr;
    }
}
