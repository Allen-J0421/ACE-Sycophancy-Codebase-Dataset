package sorting;

public class BubbleSortDemo {

    public static void main(String[] args) {
        int[] arr = args.length > 0 ? parseArgs(args) : new int[]{ 64, 34, 25, 12, 22, 11, 90 };
        BubbleSort.sort(arr);
        System.out.println("Sorted array: " + IntArrayFormatter.format(arr));
    }

    private static int[] parseArgs(String[] args) {
        int[] arr = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            arr[i] = Integer.parseInt(args[i]);
        }
        return arr;
    }
}
