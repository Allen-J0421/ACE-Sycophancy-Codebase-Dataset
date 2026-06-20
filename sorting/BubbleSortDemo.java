package sorting;

public class BubbleSortDemo {

    public static void main(String[] args) {
        int[] arr = { 64, 34, 25, 12, 22, 11, 90 };
        BubbleSort.sort(arr);
        System.out.println("Sorted array: ");
        System.out.println(IntArrayFormatter.format(arr));
    }
}
