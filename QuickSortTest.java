import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Dependency-free test harness for {@link QuickSort} (no JUnit, since the
 * project builds with plain {@code javac}). Run with:
 *
 * <pre>{@code javac QuickSort.java QuickSortTest.java && java QuickSortTest}</pre>
 *
 * Exits with a non-zero status if any case fails, so it can gate a build.
 */
public final class QuickSortTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        edgeCases();
        smallKnownCase();
        duplicatesAndNegatives();
        adversarialInputsDoNotOverflow();
        randomizedAgainstReference();
        heapSortFallbackPath();
        longOverload();
        doubleOverload();
        floatOverload();
        genericNaturalOrder();
        genericWithComparator();
        nullArgumentsRejected();

        System.out.printf("%n%d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void edgeCases() {
        assertSorted(new int[] {});
        assertSorted(new int[] {42});
        assertSorted(new int[] {2, 1});
        assertSorted(new int[] {1, 2});
    }

    private static void smallKnownCase() {
        int[] arr = {10, 7, 8, 9, 1, 5};
        QuickSort.sort(arr);
        check("known case", Arrays.equals(arr, new int[] {1, 5, 7, 8, 9, 10}));
    }

    private static void duplicatesAndNegatives() {
        assertSorted(new int[] {5, 5, 5, 5, 5});
        assertSorted(new int[] {3, -1, 4, -1, 5, -9, 2, -6, 5, 3, -5});
        assertSorted(new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1});
    }

    /**
     * Sorted and reverse-sorted inputs are the worst case for a last-element
     * pivot and would blow the stack in a naive recursive quicksort. These must
     * complete without {@link StackOverflowError}.
     */
    private static void adversarialInputsDoNotOverflow() {
        int n = 1_000_000;

        int[] ascending = new int[n];
        for (int i = 0; i < n; i++) {
            ascending[i] = i;
        }
        assertSorted(ascending);

        int[] descending = new int[n];
        for (int i = 0; i < n; i++) {
            descending[i] = n - i;
        }
        assertSorted(descending);

        int[] allEqual = new int[n];
        Arrays.fill(allEqual, 7);
        assertSorted(allEqual);
    }

    private static void randomizedAgainstReference() {
        Random rng = new Random(20260618L);
        for (int trial = 0; trial < 500; trial++) {
            int[] data = new int[rng.nextInt(300)];
            for (int i = 0; i < data.length; i++) {
                data[i] = rng.nextInt(50) - 25; // small range -> many duplicates
            }
            int[] expected = data.clone();
            Arrays.sort(expected);
            QuickSort.sort(data);
            if (!Arrays.equals(data, expected)) {
                check("randomized trial " + trial, false);
                return;
            }
        }
        check("randomized against Arrays.sort (500 trials)", true);
    }

    /**
     * Exercises the introsort fallback directly. {@code QuickSort.heapSort} is
     * package-private, so we can verify the fallback sorter independently of the
     * adversarial input that would normally trigger it — including that it
     * confines its work to {@code [low, high]} and leaves the rest untouched.
     */
    private static void heapSortFallbackPath() {
        Random rng = new Random(12345L);
        for (int trial = 0; trial < 200; trial++) {
            int[] data = new int[1 + rng.nextInt(200)];
            for (int i = 0; i < data.length; i++) {
                data[i] = rng.nextInt(100) - 50;
            }
            // Sort only an interior window; the surrounding sentinels must not move.
            int low = data.length > 4 ? 1 : 0;
            int high = data.length > 4 ? data.length - 2 : data.length - 1;

            int[] expected = data.clone();
            Arrays.sort(expected, low, high + 1);
            QuickSort.heapSort(data, low, high);

            if (!Arrays.equals(data, expected)) {
                check("heapSort fallback trial " + trial, false);
                return;
            }
        }
        check("heapSort fallback (int, 200 windowed trials)", true);

        Integer[] boxed = {9, 3, 7, 1, 8, 2, 6, 4, 5, 0};
        Integer[] boxedExpected = boxed.clone();
        Arrays.sort(boxedExpected, 2, 8); // indices [2, 7] inclusive
        QuickSort.heapSort(boxed, 2, 7, Comparator.naturalOrder());
        check("heapSort fallback (generic, windowed)", Arrays.equals(boxed, boxedExpected));
    }

    private static void longOverload() {
        long[] empty = {};
        QuickSort.sort(empty);
        check("long empty", empty.length == 0);

        long[] known = {3L, -7L, Long.MAX_VALUE, 0L, Long.MIN_VALUE, 42L};
        long[] knownExpected = known.clone();
        Arrays.sort(knownExpected);
        QuickSort.sort(known);
        check("long known case (incl. MIN/MAX)", Arrays.equals(known, knownExpected));

        Random rng = new Random(7L);
        for (int trial = 0; trial < 300; trial++) {
            long[] data = new long[rng.nextInt(400)];
            for (int i = 0; i < data.length; i++) {
                data[i] = rng.nextLong();
            }
            long[] expected = data.clone();
            Arrays.sort(expected);
            QuickSort.sort(data);
            if (!Arrays.equals(data, expected)) {
                check("long randomized trial " + trial, false);
                return;
            }
        }
        check("long randomized vs Arrays.sort (300 trials)", true);

        // Exercise the heapsort fallback directly on a windowed range.
        long[] fb = {9, 3, 7, 1, 8, 2, 6, 4, 5, 0};
        long[] fbExpected = fb.clone();
        Arrays.sort(fbExpected, 2, 8);
        QuickSort.heapSort(fb, 2, 7);
        check("long heapSort fallback (windowed)", Arrays.equals(fb, fbExpected));
    }

    private static void doubleOverload() {
        // NaN, both signed zeros, and infinities must match Arrays.sort semantics.
        double[] special = {Double.NaN, 0.0, -0.0, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, 1.5, -1.5, Double.NaN, 0.0, -0.0};
        double[] specialExpected = special.clone();
        Arrays.sort(specialExpected);
        QuickSort.sort(special);
        // Use deep equality so NaN==NaN and -0.0 != 0.0 are compared bit-wise, as Arrays.equals does.
        check("double NaN / signed-zero / infinity ordering", Arrays.equals(special, specialExpected));

        Random rng = new Random(99L);
        for (int trial = 0; trial < 300; trial++) {
            double[] data = new double[rng.nextInt(400)];
            for (int i = 0; i < data.length; i++) {
                data[i] = sometimesSpecial(rng, rng.nextGaussian() * 1000);
            }
            double[] expected = data.clone();
            Arrays.sort(expected);
            QuickSort.sort(data);
            if (!Arrays.equals(data, expected)) {
                check("double randomized trial " + trial, false);
                return;
            }
        }
        check("double randomized vs Arrays.sort (300 trials, incl. NaN)", true);

        double[] fb = {9, 3, 7, 1, 8, 2, 6, 4, 5, 0};
        double[] fbExpected = fb.clone();
        Arrays.sort(fbExpected, 2, 8);
        QuickSort.heapSort(fb, 2, 7);
        check("double heapSort fallback (windowed)", Arrays.equals(fb, fbExpected));
    }

    private static void floatOverload() {
        float[] special = {Float.NaN, 0.0f, -0.0f, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, 1.5f, -1.5f, Float.NaN, 0.0f, -0.0f};
        float[] specialExpected = special.clone();
        Arrays.sort(specialExpected);
        QuickSort.sort(special);
        check("float NaN / signed-zero / infinity ordering", Arrays.equals(special, specialExpected));

        Random rng = new Random(123L);
        for (int trial = 0; trial < 300; trial++) {
            float[] data = new float[rng.nextInt(400)];
            for (int i = 0; i < data.length; i++) {
                data[i] = (float) sometimesSpecial(rng, rng.nextGaussian() * 1000);
            }
            float[] expected = data.clone();
            Arrays.sort(expected);
            QuickSort.sort(data);
            if (!Arrays.equals(data, expected)) {
                check("float randomized trial " + trial, false);
                return;
            }
        }
        check("float randomized vs Arrays.sort (300 trials, incl. NaN)", true);

        float[] fb = {9, 3, 7, 1, 8, 2, 6, 4, 5, 0};
        float[] fbExpected = fb.clone();
        Arrays.sort(fbExpected, 2, 8);
        QuickSort.heapSort(fb, 2, 7);
        check("float heapSort fallback (windowed)", Arrays.equals(fb, fbExpected));
    }

    /** Occasionally returns NaN / +-0.0 / +-Infinity so the floating-point paths see edge values. */
    private static double sometimesSpecial(Random rng, double normal) {
        switch (rng.nextInt(12)) {
            case 0: return Double.NaN;
            case 1: return 0.0;
            case 2: return -0.0;
            case 3: return Double.POSITIVE_INFINITY;
            case 4: return Double.NEGATIVE_INFINITY;
            default: return normal;
        }
    }

    private static void genericNaturalOrder() {
        String[] words = {"pear", "apple", "fig", "banana", "apple"};
        QuickSort.sort(words);
        check("generic natural order",
                Arrays.equals(words, new String[] {"apple", "apple", "banana", "fig", "pear"}));
    }

    private static void genericWithComparator() {
        Integer[] nums = {3, 1, 4, 1, 5, 9, 2, 6};
        QuickSort.sort(nums, Comparator.reverseOrder());
        Integer[] expected = {9, 6, 5, 4, 3, 2, 1, 1};
        check("generic with comparator", Arrays.equals(nums, expected));
    }

    private static void nullArgumentsRejected() {
        check("null int[] rejected", throwsNpe(() -> QuickSort.sort((int[]) null)));
        check("null T[] rejected", throwsNpe(() -> QuickSort.sort((String[]) null)));
        check("null comparator rejected",
                throwsNpe(() -> QuickSort.sort(new Integer[] {1}, null)));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static void assertSorted(int[] data) {
        int[] expected = data.clone();
        Arrays.sort(expected);
        QuickSort.sort(data);
        check("sorted [" + describe(expected) + "]", Arrays.equals(data, expected));
    }

    private static String describe(int[] sorted) {
        return sorted.length == 0 ? "empty" : "n=" + sorted.length;
    }

    private static boolean throwsNpe(Runnable r) {
        try {
            r.run();
            return false;
        } catch (NullPointerException expected) {
            return true;
        }
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name);
        }
    }
}
