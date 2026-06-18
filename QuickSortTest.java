import java.util.Arrays;

public class QuickSortTest {

    private static class TestCase {
        final String name;
        final int[]  input;
        final int[]  expected;

        TestCase(String name, int[] input, int[] expected) {
            this.name     = name;
            this.input    = input;
            this.expected = expected;
        }
    }

    private static final TestCase[] CASES = {
        new TestCase("typical case",     new int[]{10, 7, 8, 9, 1, 5}, new int[]{1, 5, 7, 8, 9, 10}),
        new TestCase("already sorted",   new int[]{1, 2, 3, 4, 5},     new int[]{1, 2, 3, 4, 5}),
        new TestCase("reverse sorted",   new int[]{5, 4, 3, 2, 1},     new int[]{1, 2, 3, 4, 5}),
        new TestCase("single element",   new int[]{42},                 new int[]{42}),
        new TestCase("empty array",      new int[]{},                   new int[]{}),
        new TestCase("duplicates",       new int[]{3, 1, 2, 1, 3},     new int[]{1, 1, 2, 3, 3}),
        new TestCase("all equal",        new int[]{7, 7, 7},            new int[]{7, 7, 7}),
        new TestCase("two elements",     new int[]{2, 1},               new int[]{1, 2}),
        new TestCase("negative numbers", new int[]{-3, 1, -7, 0, 5},   new int[]{-7, -3, 0, 1, 5}),
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

        for (TestCase tc : CASES) {
            int[] result = Arrays.copyOf(tc.input, tc.input.length);
            sorter.sort(result);
            if (Arrays.equals(result, tc.expected)) {
                System.out.println("PASS: " + tc.name);
                passed++;
            } else {
                System.out.printf("FAIL: %s — expected %s, got %s%n",
                    tc.name, Arrays.toString(tc.expected), Arrays.toString(result));
                failed++;
            }
        }

        System.out.printf("Results: %d passed, %d failed%n%n", passed, failed);
        return failed == 0;
    }
}
