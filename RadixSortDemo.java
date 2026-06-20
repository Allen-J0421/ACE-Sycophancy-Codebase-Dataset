import java.util.Arrays;

final class RadixSortDemo {
    private static final int[] SAMPLE_VALUES = { 170, -45, 75, 90, Integer.MIN_VALUE, 24, 2, 66 };

    private RadixSortDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = SAMPLE_VALUES.clone();

        RadixSort.sort(values);
        System.out.println(Arrays.toString(values));
    }
}
