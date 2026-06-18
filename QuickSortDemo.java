import java.util.Arrays;

public final class QuickSortDemo {

    private QuickSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = {10, 7, 8, 9, 1, 5};
        QuickSort.sort(values);
        System.out.println(Arrays.toString(values));
    }
}
