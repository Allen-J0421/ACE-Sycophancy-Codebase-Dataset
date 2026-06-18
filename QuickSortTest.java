import java.util.Arrays;

public class QuickSortTest {

    public static void main(String[] args) {
        boolean ok = true;
        ok &= runSuite("last-element pivot",    new QuickSort());
        ok &= runSuite("median-of-three pivot", new QuickSort(PivotSelector.MEDIAN_OF_THREE));
        if (!ok) System.exit(1);
    }

    private static boolean runSuite(String label, Sorter sorter) {
        System.out.println("=== " + label + " ===");
        int passed = 0, failed = 0;

        failed += test(sorter, "typical case",    new int[]{10, 7, 8, 9, 1, 5},  new int[]{1, 5, 7, 8, 9, 10});
        failed += test(sorter, "already sorted",  new int[]{1, 2, 3, 4, 5},      new int[]{1, 2, 3, 4, 5});
        failed += test(sorter, "reverse sorted",  new int[]{5, 4, 3, 2, 1},      new int[]{1, 2, 3, 4, 5});
        failed += test(sorter, "single element",  new int[]{42},                  new int[]{42});
        failed += test(sorter, "empty array",     new int[]{},                    new int[]{});
        failed += test(sorter, "duplicates",      new int[]{3, 1, 2, 1, 3},      new int[]{1, 1, 2, 3, 3});
        failed += test(sorter, "all equal",       new int[]{7, 7, 7},             new int[]{7, 7, 7});
        failed += test(sorter, "two elements",    new int[]{2, 1},                new int[]{1, 2});
        failed += test(sorter, "negative numbers",new int[]{-3, 1, -7, 0, 5},    new int[]{-7, -3, 0, 1, 5});

        passed = 9 - failed;
        System.out.printf("Results: %d passed, %d failed%n%n", passed, failed);
        return failed == 0;
    }

    private static int test(Sorter sorter, String name, int[] input, int[] expected) {
        sorter.sort(input);
        if (Arrays.equals(input, expected)) {
            System.out.println("PASS: " + name);
            return 0;
        }
        System.out.printf("FAIL: %s — expected %s, got %s%n",
            name, Arrays.toString(expected), Arrays.toString(input));
        return 1;
    }
}
