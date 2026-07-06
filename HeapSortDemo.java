import java.util.Arrays;
import java.util.Comparator;

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

        String[] words = { "banana", "apple", "cherry", "date" };
        System.out.println("Before: " + Arrays.toString(words));
        HeapSort.heapSort(words, Comparator.naturalOrder());
        System.out.println("After:  " + Arrays.toString(words));

        String[] words2 = { "z", "c", "a", "m", "q" };
        System.out.println("Before (range [1,4)): " + Arrays.toString(words2));
        HeapSort.heapSort(words2, 1, 4, Comparator.naturalOrder());
        System.out.println("After  (range [1,4)): " + Arrays.toString(words2));
    }
}
