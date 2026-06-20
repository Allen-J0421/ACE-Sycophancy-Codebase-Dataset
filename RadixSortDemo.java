import java.util.Arrays;

public final class RadixSortDemo {
    private RadixSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = {170, 45, 75, 90, 802, 24, 2, 66};
        int[] sortedValues = RadixSort.sortedCopy(values);

        System.out.println("Original: " + Arrays.toString(values));
        System.out.println("Sorted:   " + Arrays.toString(sortedValues));
    }
}
