package sorting;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Minimal self-contained test harness for {@link BubbleSort}. Avoids an
 * external test-framework dependency so the package stays buildable with a
 * bare {@code javac sorting/*.java}. Exits with a non-zero status if any
 * assertion fails, making it usable as a CI gate.
 */
public final class BubbleSortTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        BubbleSort sorter = new BubbleSort();

        // Primitive int[] ascending convenience path.
        sortAscAndCheck("already sorted", new int[]{1, 2, 3}, new int[]{1, 2, 3});
        sortAscAndCheck("reverse order", new int[]{3, 2, 1}, new int[]{1, 2, 3});
        sortAscAndCheck("with duplicates", new int[]{2, 1, 2, 1}, new int[]{1, 1, 2, 2});
        sortAscAndCheck("empty array", new int[]{}, new int[]{});
        sortAscAndCheck("single element", new int[]{42}, new int[]{42});
        sortAscAndCheck("negatives", new int[]{0, -1, 5, -10}, new int[]{-10, -1, 0, 5});
        sortAscAndCheck("original demo data",
                new int[]{64, 34, 25, 12, 22, 11, 90},
                new int[]{11, 12, 22, 25, 34, 64, 90});

        // Primitive int[] with a custom comparator (no boxing).
        int[] descending = {1, 4, 2, 3};
        BubbleSort.sort(descending, IntComparator.DESCENDING);
        assertInts("primitive descending", descending, new int[]{4, 3, 2, 1});

        // Generic comparator path.
        Integer[] natural = {5, 3, 4, 1, 2};
        sorter.sort(natural);
        assertObjects("generic natural order", natural, new Integer[]{1, 2, 3, 4, 5});

        Integer[] reversed = {1, 2, 3};
        sorter.sort(reversed, Comparator.reverseOrder());
        assertObjects("generic reverse order", reversed, new Integer[]{3, 2, 1});

        String[] words = {"banana", "apple", "cherry"};
        sorter.sort(words);
        assertObjects("strings", words, new String[]{"apple", "banana", "cherry"});

        System.out.printf("%nTests: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void sortAscAndCheck(String name, int[] input, int[] expected) {
        BubbleSort.sort(input);
        assertInts(name, input, expected);
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
}
