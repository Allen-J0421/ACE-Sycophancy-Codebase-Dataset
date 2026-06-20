import java.util.Arrays;

public final class HeapSortDemo {
    private HeapSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = sampleValues();

        HeapSort.sort(values);

        System.out.println(Arrays.toString(values));
    }

    private static int[] sampleValues() {
        return new int[] {9, 4, 3, 8, 10, 2, 5};
    }
}
