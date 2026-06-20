import java.util.Arrays;

public final class HeapSortDemo {
    private static final int[] SAMPLE_VALUES = {9, 4, 3, 8, 10, 2, 5};

    private HeapSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = Arrays.copyOf(SAMPLE_VALUES, SAMPLE_VALUES.length);

        HeapSort.sort(values);

        System.out.println(Arrays.toString(values));
    }
}
