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

        // Primitive int[] path.
        checkInts("already sorted", new int[]{1, 2, 3}, new int[]{1, 2, 3});
        checkInts("reverse order", new int[]{3, 2, 1}, new int[]{1, 2, 3});
        checkInts("with duplicates", new int[]{2, 1, 2, 1}, new int[]{1, 1, 2, 2});
        checkInts("empty array", new int[]{}, new int[]{});
        checkInts("single element", new int[]{42}, new int[]{42});
        checkInts("negatives", new int[]{0, -1, 5, -10}, new int[]{-10, -1, 0, 5});
        checkInts("original demo data",
                new int[]{64, 34, 25, 12, 22, 11, 90},
                new int[]{11, 12, 22, 25, 34, 64, 90});

        // Generic comparator path.
        Integer[] natural = {5, 3, 4, 1, 2};
        sorter.sort(natural);
        checkObjects("generic natural order", natural, new Integer[]{1, 2, 3, 4, 5});

        Integer[] reversed = {1, 2, 3};
        sorter.sort(reversed, Comparator.reverseOrder());
        checkObjects("generic reverse order", reversed, new Integer[]{3, 2, 1});

        String[] words = {"banana", "apple", "cherry"};
        sorter.sort(words);
        checkObjects("strings", words, new String[]{"apple", "banana", "cherry"});

        System.out.printf("%nTests: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void checkInts(String name, int[] input, int[] expected) {
        BubbleSort.sort(input);
        if (Arrays.equals(input, expected)) {
            pass(name);
        } else {
            fail(name, Arrays.toString(expected), Arrays.toString(input));
        }
    }

    private static <T> void checkObjects(String name, T[] actual, T[] expected) {
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
