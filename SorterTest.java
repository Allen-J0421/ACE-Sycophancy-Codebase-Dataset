import java.util.Arrays;
import java.util.Comparator;

/**
 * Dependency-free test runner for {@link Sorter} implementations.
 *
 * <p>The same battery of cases is run against every implementation through the
 * {@link Sorter} interface, so new algorithms are verified simply by adding them
 * to {@link #IMPLEMENTATIONS}. Uses no external framework so it runs with plain
 * {@code javac}/{@code java}:
 * <pre>
 *   javac *.java
 *   java SorterTest
 * </pre>
 * Exits with a non-zero status if any case fails, so it is usable in CI.
 */
public final class SorterTest {

    /** Every implementation here is exercised by the full battery below. */
    private static final Sorter[] IMPLEMENTATIONS = {
        new InsertionSorter(),
        new BubbleSorter(),
    };

    private static int failures = 0;

    public static void main(String[] args) {
        for (Sorter sorter : IMPLEMENTATIONS) {
            runBattery(sorter);
        }

        if (failures > 0) {
            System.out.println("\n" + failures + " test(s) FAILED");
            System.exit(1);
        }
        System.out.println("\nAll tests passed");
    }

    private static void runBattery(Sorter sorter) {
        String impl = sorter.getClass().getSimpleName();
        System.out.println("--- " + impl + " ---");

        emptyArray(impl, sorter);
        singleElement(impl, sorter);
        alreadySorted(impl, sorter);
        reverseSorted(impl, sorter);
        duplicates(impl, sorter);
        negativesAndZero(impl, sorter);
        genericNaturalOrder(impl, sorter);
        comparatorReverseOrder(impl, sorter);
        nullArrayThrows(impl, sorter);
        nullComparatorThrows(impl, sorter);
    }

    private static void emptyArray(String impl, Sorter sorter) {
        int[] a = {};
        sorter.sort(a);
        check(impl, "emptyArray", new int[] {}, a);
    }

    private static void singleElement(String impl, Sorter sorter) {
        int[] a = { 42 };
        sorter.sort(a);
        check(impl, "singleElement", new int[] { 42 }, a);
    }

    private static void alreadySorted(String impl, Sorter sorter) {
        int[] a = { 1, 2, 3, 4, 5 };
        sorter.sort(a);
        check(impl, "alreadySorted", new int[] { 1, 2, 3, 4, 5 }, a);
    }

    private static void reverseSorted(String impl, Sorter sorter) {
        int[] a = { 5, 4, 3, 2, 1 };
        sorter.sort(a);
        check(impl, "reverseSorted", new int[] { 1, 2, 3, 4, 5 }, a);
    }

    private static void duplicates(String impl, Sorter sorter) {
        int[] a = { 3, 1, 2, 3, 1, 2 };
        sorter.sort(a);
        check(impl, "duplicates", new int[] { 1, 1, 2, 2, 3, 3 }, a);
    }

    private static void negativesAndZero(String impl, Sorter sorter) {
        int[] a = { 0, -3, 5, -1, 2 };
        sorter.sort(a);
        check(impl, "negativesAndZero", new int[] { -3, -1, 0, 2, 5 }, a);
    }

    private static void genericNaturalOrder(String impl, Sorter sorter) {
        String[] a = { "pear", "apple", "fig", "banana" };
        sorter.sort(a);
        check(impl, "genericNaturalOrder",
                new String[] { "apple", "banana", "fig", "pear" }, a);
    }

    private static void comparatorReverseOrder(String impl, Sorter sorter) {
        Integer[] a = { 12, 11, 13, 5, 6 };
        sorter.sort(a, Comparator.reverseOrder());
        check(impl, "comparatorReverseOrder",
                new Integer[] { 13, 12, 11, 6, 5 }, a);
    }

    private static void nullArrayThrows(String impl, Sorter sorter) {
        checkThrows(impl, "nullArrayThrows",
                () -> sorter.sort((int[]) null));
    }

    private static void nullComparatorThrows(String impl, Sorter sorter) {
        checkThrows(impl, "nullComparatorThrows",
                () -> sorter.sort(new Integer[] { 1 }, null));
    }

    // --- assertion helpers ---------------------------------------------------

    private static void check(String impl, String name, int[] expected, int[] actual) {
        report(impl, name, Arrays.equals(expected, actual),
                Arrays.toString(expected), Arrays.toString(actual));
    }

    private static void check(String impl, String name, Object[] expected, Object[] actual) {
        report(impl, name, Arrays.equals(expected, actual),
                Arrays.toString(expected), Arrays.toString(actual));
    }

    private static void checkThrows(String impl, String name, Runnable action) {
        try {
            action.run();
            report(impl, name, false, "NullPointerException", "no exception");
        } catch (NullPointerException expected) {
            report(impl, name, true, "NullPointerException", "NullPointerException");
        }
    }

    private static void report(String impl, String name, boolean passed,
                               String expected, String actual) {
        if (passed) {
            System.out.println("PASS  " + impl + "." + name);
        } else {
            failures++;
            System.out.println("FAIL  " + impl + "." + name
                    + "  expected=" + expected + "  actual=" + actual);
        }
    }
}
