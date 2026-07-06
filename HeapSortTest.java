import java.util.Arrays;
import java.util.function.Consumer;

public class HeapSortTest {

    private static Runnable arraySortCase(String name, int[] input, int[] expected, Consumer<int[]> sortFn) {
        return () -> {
            int[] copy = input.clone();
            sortFn.accept(copy);
            if (!Arrays.equals(expected, copy))
                throw new AssertionError(name + ": expected " + Arrays.toString(expected)
                    + " but got " + Arrays.toString(copy));
            System.out.println("PASS: " + name);
        };
    }

    private static Runnable invalidRangeCase(String name, int[] arr, int from, int to) {
        return () -> {
            try {
                HeapSort.heapSort(arr, from, to);
                throw new AssertionError(name + ": expected IllegalArgumentException but no exception was thrown");
            } catch (IllegalArgumentException e) {
                System.out.println("PASS: " + name);
            }
        };
    }

    public static void main(String[] args) {
        Runnable[] cases = {
            arraySortCase("typical unsorted array",
                new int[]{ 9, 4, 3, 8, 10, 2, 5 },
                new int[]{ 2, 3, 4, 5, 8, 9, 10 },
                HeapSort::heapSort),
            arraySortCase("already sorted",
                new int[]{ 1, 2, 3, 4, 5 },
                new int[]{ 1, 2, 3, 4, 5 },
                HeapSort::heapSort),
            arraySortCase("reverse sorted",
                new int[]{ 5, 4, 3, 2, 1 },
                new int[]{ 1, 2, 3, 4, 5 },
                HeapSort::heapSort),
            arraySortCase("single element",
                new int[]{ 42 },
                new int[]{ 42 },
                HeapSort::heapSort),
            arraySortCase("empty array",
                new int[]{},
                new int[]{},
                HeapSort::heapSort),
            arraySortCase("duplicates",
                new int[]{ 3, 1, 4, 1, 5, 9, 2, 6, 5 },
                new int[]{ 1, 1, 2, 3, 4, 5, 5, 6, 9 },
                HeapSort::heapSort),
            arraySortCase("sort middle subrange",
                new int[]{ 7, 1, 6, 2, 9, 3, 8 },
                new int[]{ 7, 1, 2, 6, 9, 3, 8 },
                arr -> HeapSort.heapSort(arr, 2, 5)),
            arraySortCase("sort entire array via range",
                new int[]{ 5, 3, 1, 4, 2 },
                new int[]{ 1, 2, 3, 4, 5 },
                arr -> HeapSort.heapSort(arr, 0, 5)),
            arraySortCase("empty range is no-op",
                new int[]{ 3, 1, 2 },
                new int[]{ 3, 1, 2 },
                arr -> HeapSort.heapSort(arr, 1, 1)),
            invalidRangeCase("negative from",       new int[]{ 1, 2, 3 }, -1, 2),
            invalidRangeCase("to beyond length",    new int[]{ 1, 2, 3 },  0, 4),
            invalidRangeCase("from greater than to", new int[]{ 1, 2, 3 }, 2, 1),
        };

        for (Runnable tc : cases)
            tc.run();

        System.out.println("All tests passed.");
    }
}
