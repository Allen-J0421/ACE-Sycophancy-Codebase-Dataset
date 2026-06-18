import java.util.Arrays;

public class QuickSortDemo {

    public static void main(String[] args) {
        int[] arr = {10, 7, 8, 9, 1, 5};

        Sorter sorter = new QuickSort();
        sorter.sort(arr);

        System.out.println(Arrays.toString(arr));
    }
}
