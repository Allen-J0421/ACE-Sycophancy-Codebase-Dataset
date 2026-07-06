import java.util.Arrays;

public class HeapSortDemo {

    public static void main(String[] args) {
        int[] arr = { 9, 4, 3, 8, 10, 2, 5 };
        System.out.println("Before: " + Arrays.toString(arr));
        HeapSort.heapSort(arr);
        System.out.println("After:  " + Arrays.toString(arr));

        int[] arr2 = { 7, 1, 6, 2, 9, 3, 8 };
        System.out.println("Before (range [2,5)): " + Arrays.toString(arr2));
        HeapSort.heapSort(arr2, 2, 5);
        System.out.println("After  (range [2,5)): " + Arrays.toString(arr2));
    }
}
