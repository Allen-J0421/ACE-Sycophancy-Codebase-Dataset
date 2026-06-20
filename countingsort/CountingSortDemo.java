package countingsort;

import java.util.Arrays;

public class CountingSortDemo {

    public static void main(String[] args) {
        show(new int[]{2, 5, 3, 0, 2, 3, 0, 3});
        show(new int[]{-3, 1, -1, 0, 2, -2});
        show(new int[]{42});
    }

    private static void show(int[] arr) {
        System.out.println("Input:  " + Arrays.toString(arr));
        System.out.println("Sorted: " + Arrays.toString(CountingSort.sort(arr)));
    }
}
