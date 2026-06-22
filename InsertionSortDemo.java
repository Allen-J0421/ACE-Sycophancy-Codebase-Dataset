import java.util.Arrays;

public final class InsertionSortDemo {

    private InsertionSortDemo() {
        // No instances.
    }

    public static void main(String[] args) {
        int[] values = {12, 11, 13, 5, 6};

        System.out.println("Original: " + Arrays.toString(values));
        System.out.println("Sorted:   " + Arrays.toString(InsertionSort.sortedCopy(values)));
    }
}
