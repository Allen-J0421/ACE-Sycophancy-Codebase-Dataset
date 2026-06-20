import java.util.Arrays;

public final class InsertionSortDemo {

    private static final int[] DEMO_INPUT = {12, 11, 13, 5, 6};

    private InsertionSortDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] sorted = InsertionSort.sortedCopy(DEMO_INPUT);
        System.out.println(Arrays.toString(sorted));
    }
}
