import java.util.Arrays;
import java.util.function.Consumer;

public class HeapSortTest {

    interface TestCase {
        void run();
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

        public void run() {
            int[] arr = Arrays.copyOf(input, input.length);
            sortAction.accept(arr);
            if (!Arrays.equals(expected, arr))
                throw new AssertionError(label + ": expected " + Arrays.toString(expected)
                        + " but got " + Arrays.toString(arr));
            System.out.println("PASS: " + label);
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
            try {
                HeapSort.heapSort(input, from, to);
                throw new AssertionError(label + ": expected IllegalArgumentException but no exception was thrown");
            } catch (IllegalArgumentException e) {
                System.out.println("PASS: " + label);
            }
        }
    }

    public static void main(String[] args) {
        TestCase[] cases = {
            new ArraySortCase("typical unsorted array",
                    new int[]{ 9, 4, 3, 8, 10, 2, 5 },
                    new int[]{ 2, 3, 4, 5, 8, 9, 10 },
                    HeapSort::heapSort),
            new ArraySortCase("already sorted",
                    new int[]{ 1, 2, 3, 4, 5 },
                    new int[]{ 1, 2, 3, 4, 5 },
                    HeapSort::heapSort),
            new ArraySortCase("reverse sorted",
                    new int[]{ 5, 4, 3, 2, 1 },
                    new int[]{ 1, 2, 3, 4, 5 },
                    HeapSort::heapSort),
            new ArraySortCase("single element",
                    new int[]{ 42 },
                    new int[]{ 42 },
                    HeapSort::heapSort),
            new ArraySortCase("empty array",
                    new int[]{},
                    new int[]{},
                    HeapSort::heapSort),
            new ArraySortCase("duplicates",
                    new int[]{ 3, 1, 4, 1, 5, 9, 2, 6, 5 },
                    new int[]{ 1, 1, 2, 3, 4, 5, 5, 6, 9 },
                    HeapSort::heapSort),
            new ArraySortCase("sort middle subrange",
                    new int[]{ 7, 1, 6, 2, 9, 3, 8 },
                    new int[]{ 7, 1, 2, 6, 9, 3, 8 },
                    arr -> HeapSort.heapSort(arr, 2, 5)),
            new ArraySortCase("sort entire array via range",
                    new int[]{ 5, 3, 1, 4, 2 },
                    new int[]{ 1, 2, 3, 4, 5 },
                    arr -> HeapSort.heapSort(arr, 0, 5)),
            new ArraySortCase("empty range is no-op",
                    new int[]{ 3, 1, 2 },
                    new int[]{ 3, 1, 2 },
                    arr -> HeapSort.heapSort(arr, 1, 1)),
            new InvalidRangeCase("negative from",        new int[]{ 1, 2, 3 }, -1, 2),
            new InvalidRangeCase("to beyond length",     new int[]{ 1, 2, 3 },  0, 4),
            new InvalidRangeCase("from greater than to", new int[]{ 1, 2, 3 },  2, 1),
        };

        for (TestCase c : cases) c.run();
        System.out.println("All tests passed.");
    }
}
