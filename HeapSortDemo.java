import java.util.Arrays;

public final class HeapSortDemo {

    private HeapSortDemo() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        int[] values = {9, 4, 3, 8, 10, 2, 5};
        HeapSort.sort(values);
        System.out.println(Arrays.toString(values));
    }
}
