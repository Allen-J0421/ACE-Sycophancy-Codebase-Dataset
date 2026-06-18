import java.util.Arrays;

public final class QuickSortDemo {
    private QuickSortDemo() {
        // Demo class.
    }

    public static void main(String[] args) {
        int[] values = {10, 7, 8, 9, 1, 5};
        int[] sorted = QuickSort.sortedCopy(values);
        System.out.println(Arrays.toString(sorted));
    }
}
