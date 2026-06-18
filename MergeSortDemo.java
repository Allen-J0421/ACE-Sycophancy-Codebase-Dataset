import java.util.Arrays;

public final class MergeSortDemo {

    private MergeSortDemo() {
        // Demo class.
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        MergeSort.sort(values);

        System.out.println(Arrays.toString(values));
    }
}
