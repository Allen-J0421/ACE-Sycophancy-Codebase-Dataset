import java.util.Arrays;

public class QuickSortTest {

    private static final String[] NAMES = {
        "typical case", "already sorted", "reverse sorted", "single element",
        "empty array",  "duplicates",     "all equal",      "two elements",
        "negative numbers"
    };

    private static final int[][] INPUTS = {
        {10, 7, 8, 9, 1, 5}, {1, 2, 3, 4, 5}, {5, 4, 3, 2, 1}, {42},
        {},                   {3, 1, 2, 1, 3}, {7, 7, 7},        {2, 1},
        {-3, 1, -7, 0, 5}
    };

    private static final int[][] EXPECTED = {
        {1, 5, 7, 8, 9, 10}, {1, 2, 3, 4, 5}, {1, 2, 3, 4, 5}, {42},
        {},                   {1, 1, 2, 3, 3}, {7, 7, 7},        {1, 2},
        {-7, -3, 0, 1, 5}
    };

    public static void main(String[] args) {
        boolean ok = true;
        ok &= runSuite("recursive + last-element",    new QuickSort());
        ok &= runSuite("recursive + median-of-three", new QuickSort(PivotSelector.MEDIAN_OF_THREE));
        ok &= runSuite("iterative + last-element",    new IterativeQuickSort());
        if (!ok) System.exit(1);
    }

    private static boolean runSuite(String label, Sorter sorter) {
        System.out.println("=== " + label + " ===");
        int passed = 0, failed = 0;

        for (int i = 0; i < NAMES.length; i++) {
            int[] result = Arrays.copyOf(INPUTS[i], INPUTS[i].length);
            sorter.sort(result);
            if (Arrays.equals(result, EXPECTED[i])) {
                System.out.println("PASS: " + NAMES[i]);
                passed++;
            } else {
                System.out.printf("FAIL: %s — expected %s, got %s%n",
                    NAMES[i], Arrays.toString(EXPECTED[i]), Arrays.toString(result));
                failed++;
            }
        }

        System.out.printf("Results: %d passed, %d failed%n%n", passed, failed);
        return failed == 0;
    }
}
