package sorting;

import java.util.Arrays;

public class BubbleSortDemo {

    public static void main(String[] args) {
        int[] arr;
        try {
            arr = args.length > 0 ? parseArgs(args) : new int[]{ 64, 34, 25, 12, 22, 11, 90 };
        } catch (NumberFormatException e) {
            System.err.println("Usage: BubbleSortDemo [integers...]");
            System.exit(1);
            return;
        }
        BubbleSort.sort(arr);
        System.out.println("Sorted array: " + IntArrayFormatter.format(arr));
    }

    private static int[] parseArgs(String[] args) {
        return Arrays.stream(args).mapToInt(Integer::parseInt).toArray();
    }
}
