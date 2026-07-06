import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;

public class HeapSortTest {

    interface TestCase {
        void run();
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static class ArraySortCase implements TestCase {
        private final String label;
        private final int[] input;
        private final int[] expected;
        private final Consumer<int[]> sortAction;

        ArraySortCase(String label, int[] input, int[] expected, Consumer<int[]> sortAction) {
            this.label = label;
            this.input = input;
            this.expected = expected;
            this.sortAction = sortAction;
        }

        protected ArraySortCase(String label, int[] input, int[] expected) {
            this.label = label;
            this.input = input;
            this.expected = expected;
            this.sortAction = this::sort;
        }

        protected void sort(int[] arr) {}

        @Override
        public void run() {
            int[] copy = Arrays.copyOf(input, input.length);
            sortAction.accept(copy);
            if (!Arrays.equals(expected, copy))
                throw new AssertionError(label + ": expected " + Arrays.toString(expected)
                    + " but got " + Arrays.toString(copy));
            System.out.println("PASS: " + label);
        }
    }

    static class SortCase extends ArraySortCase {
        SortCase(String label, int[] input, int[] expected) {
            super(label, input, expected);
        }

        @Override
        protected void sort(int[] arr) {
            HeapSort.heapSort(arr);
        }
    }

    static class RangeCase extends ArraySortCase {
        private final int from;
        private final int to;

        RangeCase(String label, int[] input, int from, int to, int[] expected) {
            super(label, input, expected);
            this.from = from;
            this.to = to;
        }

        @Override
        protected void sort(int[] arr) {
            HeapSort.heapSort(arr, from, to);
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

        @Override
        public void run() {
            try {
                HeapSort.heapSort(input, from, to);
                throw new AssertionError(label + ": expected IllegalArgumentException but no exception was thrown");
            } catch (IllegalArgumentException e) {
                System.out.println("PASS: " + label);
            }
        }
    }

    static void assertThrows(String label, ThrowingRunnable action) {
        try {
            action.run();
            throw new AssertionError(label + ": expected IllegalArgumentException but no exception was thrown");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: " + label);
        } catch (Exception e) {
            throw new AssertionError(label + ": unexpected " + e.getClass().getSimpleName(), e);
        }
    }

    static <T> void assertArrayEquals(String label, T[] expected, T[] actual) {
        if (!Arrays.equals(expected, actual))
            throw new AssertionError(label + ": expected " + Arrays.toString(expected)
                + " but got " + Arrays.toString(actual));
        System.out.println("PASS: " + label);
    }

    public static void main(String[] args) {
        TestCase[] cases = {
            // --- int array sort ---
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
            // --- range sort ---
            new RangeCase("sort middle subrange",
                new int[]{ 7, 1, 6, 2, 9, 3, 8 }, 2, 5,
                new int[]{ 7, 1, 2, 6, 9, 3, 8 }),
            new RangeCase("sort entire array via range",
                new int[]{ 5, 3, 1, 4, 2 }, 0, 5,
                new int[]{ 1, 2, 3, 4, 5 }),
            new RangeCase("empty range is no-op",
                new int[]{ 3, 1, 2 }, 1, 1,
                new int[]{ 3, 1, 2 }),
            // --- invalid int range ---
            new InvalidRangeCase("negative from",
                new int[]{ 1, 2, 3 }, -1, 2),
            new InvalidRangeCase("to beyond length",
                new int[]{ 1, 2, 3 },  0, 4),
            new InvalidRangeCase("from greater than to",
                new int[]{ 1, 2, 3 },  2, 1),
        };

        for (TestCase tc : cases)
            tc.run();

        // --- generic object sort ---
        String[] words = { "banana", "apple", "cherry", "date" };
        HeapSort.heapSort(words, Comparator.naturalOrder());
        assertArrayEquals("generic: string sort",
            new String[]{ "apple", "banana", "cherry", "date" }, words);

        Integer[] nums = { 5, 3, 8, 1, 9, 2 };
        HeapSort.heapSort(nums, Comparator.naturalOrder());
        assertArrayEquals("generic: Integer sort",
            new Integer[]{ 1, 2, 3, 5, 8, 9 }, nums);

        Integer[] desc = { 1, 2, 3, 4, 5 };
        HeapSort.heapSort(desc, Comparator.reverseOrder());
        assertArrayEquals("generic: reverse-order sort",
            new Integer[]{ 5, 4, 3, 2, 1 }, desc);

        String[] partial = { "z", "c", "a", "m", "q" };
        HeapSort.heapSort(partial, 1, 4, Comparator.naturalOrder());
        assertArrayEquals("generic: middle subrange sort",
            new String[]{ "z", "a", "c", "m", "q" }, partial);

        // --- generic invalid range ---
        assertThrows("generic: negative from", () ->
            HeapSort.heapSort(new String[]{ "a" }, -1, 1, Comparator.naturalOrder()));
        assertThrows("generic: to beyond length", () ->
            HeapSort.heapSort(new String[]{ "a" }, 0, 2, Comparator.naturalOrder()));
        assertThrows("generic: from greater than to", () ->
            HeapSort.heapSort(new String[]{ "a", "b" }, 2, 1, Comparator.naturalOrder()));

        System.out.println("All tests passed.");
    }
}
