package countingsort;

import java.util.Arrays;

public class CountingSortTest {

    private static int passed = 0;
    private static int failed = 0;

    static class SortCase {
        final String name;
        final int[] input;
        final int[] expected;

        SortCase(String name, int[] input, int[] expected) {
            this.name = name;
            this.input = input;
            this.expected = expected;
        }
    }

    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String label) {
        if (Arrays.equals(expected, actual)) {
            System.out.println("PASS: " + label);
            passed++;
        } else {
            System.out.println("FAIL: " + label
                    + " — expected " + Arrays.toString(expected)
                    + ", got " + Arrays.toString(actual));
            failed++;
        }
    }

    private static void assertThrows(Class<? extends Exception> type, ThrowingRunnable fn, String label) {
        try {
            fn.run();
            System.out.println("FAIL: " + label + " — expected " + type.getSimpleName() + " but no exception thrown");
            failed++;
        } catch (Exception e) {
            if (type.isInstance(e)) {
                System.out.println("PASS: " + label);
                passed++;
            } else {
                System.out.println("FAIL: " + label + " — expected " + type.getSimpleName()
                        + " but got " + e.getClass().getSimpleName());
                failed++;
            }
        }
    }

    public static void main(String[] args) {
        SortCase[] cases = {
            new SortCase("basic",          new int[]{2, 5, 3, 0, 2, 3, 0, 3}, new int[]{0, 0, 2, 2, 3, 3, 3, 5}),
            new SortCase("empty",          new int[]{},                        new int[]{}),
            new SortCase("single element", new int[]{7},                       new int[]{7}),
            new SortCase("already sorted", new int[]{1, 2, 3, 4, 5},          new int[]{1, 2, 3, 4, 5}),
            new SortCase("reverse sorted", new int[]{5, 4, 3, 2, 1},          new int[]{1, 2, 3, 4, 5}),
            new SortCase("all same",       new int[]{3, 3, 3},                 new int[]{3, 3, 3}),
            new SortCase("negatives",      new int[]{-3, 1, -1, 0, 2, -2},    new int[]{-3, -2, -1, 0, 1, 2}),
            new SortCase("mixed",          new int[]{-1, 0, 1},                new int[]{-1, 0, 1}),
        };

        for (SortCase c : cases) {
            assertArrayEquals(c.expected, CountingSort.sort(c.input), c.name);
        }

        assertThrows(IllegalArgumentException.class, () -> CountingSort.sort(null), "null input throws");
        assertThrows(IllegalArgumentException.class,
                () -> CountingSort.sort(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}),
                "extreme range throws");

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        if (failed > 0) System.exit(1);
    }
}
