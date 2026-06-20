package sorting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * Self-contained test harness. Runs a shared correctness battery against every
 * {@link Sorter} implementation polymorphically through the interface, verifies
 * stability where it is guaranteed, and covers BubbleSort's primitive
 * {@code int[]} paths. No external test framework; non-zero exit on failure.
 */
public final class SortingTest {

    private static int passed = 0;
    private static int failed = 0;

    private static final Sorter[] SORTERS = {
        new BubbleSort(), new InsertionSort(), new MergeSort(), new QuickSort()
    };

    /** Algorithms that guarantee a stable ordering of equal keys. */
    private static final Set<String> STABLE =
        Set.of("BubbleSort", "InsertionSort", "MergeSort");

    public static void main(String[] args) {
        for (Sorter sorter : SORTERS) {
            runBattery(sorter.getClass().getSimpleName(), sorter);
        }
        primitivePaths();

        System.out.printf("%nTests: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runBattery(String name, Sorter sorter) {
        checkNatural(name + " / already sorted", sorter,
                new Integer[]{1, 2, 3}, new Integer[]{1, 2, 3});
        checkNatural(name + " / reverse", sorter,
                new Integer[]{3, 2, 1}, new Integer[]{1, 2, 3});
        checkNatural(name + " / duplicates", sorter,
                new Integer[]{2, 1, 2, 1}, new Integer[]{1, 1, 2, 2});
        checkNatural(name + " / empty", sorter,
                new Integer[]{}, new Integer[]{});
        checkNatural(name + " / single", sorter,
                new Integer[]{42}, new Integer[]{42});
        checkNatural(name + " / negatives", sorter,
                new Integer[]{0, -1, 5, -10}, new Integer[]{-10, -1, 0, 5});
        checkNatural(name + " / demo data", sorter,
                new Integer[]{64, 34, 25, 12, 22, 11, 90},
                new Integer[]{11, 12, 22, 25, 34, 64, 90});
        checkNatural(name + " / larger", sorter,
                new Integer[]{9, 7, 8, 3, 1, 6, 5, 2, 4, 0},
                new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

        // Comparator strategy: reverse ordering, same algorithm.
        Integer[] desc = {1, 2, 3, 4, 5};
        sorter.sort(desc, Comparator.reverseOrder());
        assertObjects(name + " / reverse comparator", desc, new Integer[]{5, 4, 3, 2, 1});

        // Works on any Comparable type.
        String[] words = {"banana", "apple", "cherry"};
        sorter.sort(words);
        assertObjects(name + " / strings", words, new String[]{"apple", "banana", "cherry"});

        if (STABLE.contains(name)) {
            checkStable(name, sorter);
        }
    }

    private static void checkNatural(String name, Sorter sorter,
                                     Integer[] input, Integer[] expected) {
        Integer[] copy = input.clone();
        sorter.sort(copy);
        assertObjects(name, copy, expected);
    }

    private static void checkStable(String name, Sorter sorter) {
        // Equal keys must retain their input order after a stable sort.
        Item[] items = {
            new Item(1, 0), new Item(2, 1), new Item(1, 2), new Item(2, 3), new Item(1, 4)
        };
        sorter.sort(items, Comparator.comparingInt(Item::key));
        Item[] expected = {
            new Item(1, 0), new Item(1, 2), new Item(1, 4), new Item(2, 1), new Item(2, 3)
        };
        assertObjects(name + " / stable", items, expected);
    }

    private static void primitivePaths() {
        int[] asc = {64, 34, 25, 12, 22, 11, 90};
        BubbleSort.sort(asc);
        assertInts("BubbleSort / primitive ascending", asc,
                new int[]{11, 12, 22, 25, 34, 64, 90});

        int[] desc = {1, 4, 2, 3};
        BubbleSort.sort(desc, IntComparator.DESCENDING);
        assertInts("BubbleSort / primitive descending", desc, new int[]{4, 3, 2, 1});
    }

    private static void assertInts(String name, int[] actual, int[] expected) {
        if (Arrays.equals(actual, expected)) {
            pass(name);
        } else {
            fail(name, Arrays.toString(expected), Arrays.toString(actual));
        }
    }

    private static <T> void assertObjects(String name, T[] actual, T[] expected) {
        if (Arrays.equals(actual, expected)) {
            pass(name);
        } else {
            fail(name, Arrays.toString(expected), Arrays.toString(actual));
        }
    }

    private static void pass(String name) {
        passed++;
        System.out.println("[PASS] " + name);
    }

    private static void fail(String name, String expected, String actual) {
        failed++;
        System.out.println("[FAIL] " + name + " — expected " + expected + " but got " + actual);
    }

    /** A keyed element used to verify stable ordering. */
    private record Item(int key, int id) {
        @Override
        public String toString() {
            return "(" + key + "," + id + ")";
        }
    }
}
