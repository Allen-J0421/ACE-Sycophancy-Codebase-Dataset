import java.util.Arrays;

public class HeapSortTest {

    interface ThrowingRunnable {
        void run() throws Exception;
    }

    interface TestCase {
        void run();
    }

    static void assertEquals(int[] expected, int[] actual, String label) {
        if (!Arrays.equals(expected, actual))
            throw new AssertionError(label + ": expected " + Arrays.toString(expected)
                    + " but got " + Arrays.toString(actual));
        System.out.println("PASS: " + label);
    }

    static void assertThrows(Class<? extends Exception> type, ThrowingRunnable r, String label) {
        try {
            r.run();
            throw new AssertionError(label + ": expected " + type.getSimpleName() + " but no exception was thrown");
        } catch (Exception e) {
            if (!type.isInstance(e))
                throw new AssertionError(label + ": expected " + type.getSimpleName()
                        + " but got " + e.getClass().getSimpleName());
            System.out.println("PASS: " + label);
        }
    }

    static class SortCase implements TestCase {
        private final String label;
        private final int[] input;
        private final int[] expected;

        SortCase(String label, int[] input, int[] expected) {
            this.label = label;
            this.input = input;
            this.expected = expected;
        }

        public void run() {
            int[] arr = Arrays.copyOf(input, input.length);
            HeapSort.heapSort(arr);
            assertEquals(expected, arr, label);
        }
    }

    static class RangeCase implements TestCase {
        private final String label;
        private final int[] input;
        private final int from;
        private final int to;
        private final int[] expected;

        RangeCase(String label, int[] input, int from, int to, int[] expected) {
            this.label = label;
            this.input = input;
            this.from = from;
            this.to = to;
            this.expected = expected;
        }

        public void run() {
            int[] arr = Arrays.copyOf(input, input.length);
            HeapSort.heapSort(arr, from, to);
            assertEquals(expected, arr, label);
        }
    }

    static class InvalidRangeCase implements TestCase {
        private final String label;
        private final int[] input;
        private final int from;
        private final int to;

        InvalidRangeCase(String label, int[] input, int from, int to) {
            this.label = label;
            this.input = input;
            this.from = from;
            this.to = to;
        }

        public void run() {
            assertThrows(IllegalArgumentException.class,
                    () -> HeapSort.heapSort(input, from, to), label);
        }
    }

    public static void main(String[] args) {
        TestCase[] cases = {
            new SortCase("typical unsorted array",
                    new int[]{ 9, 4, 3, 8, 10, 2, 5 },
                    new int[]{ 2, 3, 4, 5, 8, 9, 10 }),
            new SortCase("already sorted",
                    new int[]{ 1, 2, 3, 4, 5 },
                    new int[]{ 1, 2, 3, 4, 5 }),
            new SortCase("reverse sorted",
                    new int[]{ 5, 4, 3, 2, 1 },
                    new int[]{ 1, 2, 3, 4, 5 }),
            new SortCase("single element",
                    new int[]{ 42 },
                    new int[]{ 42 }),
            new SortCase("empty array",
                    new int[]{},
                    new int[]{}),
            new SortCase("duplicates",
                    new int[]{ 3, 1, 4, 1, 5, 9, 2, 6, 5 },
                    new int[]{ 1, 1, 2, 3, 4, 5, 5, 6, 9 }),
            new RangeCase("sort middle subrange",
                    new int[]{ 7, 1, 6, 2, 9, 3, 8 }, 2, 5,
                    new int[]{ 7, 1, 2, 6, 9, 3, 8 }),
            new RangeCase("sort entire array via range",
                    new int[]{ 5, 3, 1, 4, 2 }, 0, 5,
                    new int[]{ 1, 2, 3, 4, 5 }),
            new RangeCase("empty range is no-op",
                    new int[]{ 3, 1, 2 }, 1, 1,
                    new int[]{ 3, 1, 2 }),
            new InvalidRangeCase("negative from",        new int[]{ 1, 2, 3 }, -1, 2),
            new InvalidRangeCase("to beyond length",     new int[]{ 1, 2, 3 },  0, 4),
            new InvalidRangeCase("from greater than to", new int[]{ 1, 2, 3 },  2, 1),
        };

        for (TestCase c : cases) c.run();
        System.out.println("All tests passed.");
    }
}
