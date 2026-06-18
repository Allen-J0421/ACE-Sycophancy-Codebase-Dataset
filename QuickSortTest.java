import java.util.Arrays;

public class QuickSortTest {

    private final Sorter sorter = new QuickSort();
    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        new QuickSortTest().runAll();
    }

    private void runAll() {
        test("typical case",       new int[]{10, 7, 8, 9, 1, 5},  new int[]{1, 5, 7, 8, 9, 10});
        test("already sorted",     new int[]{1, 2, 3, 4, 5},       new int[]{1, 2, 3, 4, 5});
        test("reverse sorted",     new int[]{5, 4, 3, 2, 1},       new int[]{1, 2, 3, 4, 5});
        test("single element",     new int[]{42},                   new int[]{42});
        test("empty array",        new int[]{},                     new int[]{});
        test("duplicates",         new int[]{3, 1, 2, 1, 3},       new int[]{1, 1, 2, 3, 3});
        test("all equal",          new int[]{7, 7, 7},              new int[]{7, 7, 7});
        test("two elements",       new int[]{2, 1},                 new int[]{1, 2});

        System.out.printf("%nResults: %d passed, %d failed%n", passed, failed);
        if (failed > 0) System.exit(1);
    }

    private void test(String name, int[] input, int[] expected) {
        sorter.sort(input);
        if (Arrays.equals(input, expected)) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.printf("FAIL: %s — expected %s, got %s%n",
                name, Arrays.toString(expected), Arrays.toString(input));
            failed++;
        }
    }
}
