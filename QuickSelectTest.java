import java.util.Arrays;
import java.util.Random;

/**
 * Lightweight, dependency-free test runner for {@link QuickSelect}.
 *
 * <p>Run with: {@code java QuickSelectTest}. Exits with a non-zero status if any
 * assertion fails, so it can be used as a CI gate without a test framework.
 */
public final class QuickSelectTest {

    private static int failures = 0;

    public static void main(String[] args) {
        testBasicExample();
        testMinAndMax();
        testDuplicates();
        testSingleElement();
        testDoesNotMutateInput();
        testValidation();
        testAgainstSortRandomized();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void testBasicExample() {
        int[] arr = { 10, 4, 5, 8, 6, 11, 26 };
        check("basic example, k=3", QuickSelect.kthSmallest(arr, 3) == 6);
    }

    private static void testMinAndMax() {
        int[] arr = { 10, 4, 5, 8, 6, 11, 26 };
        check("k=1 returns min", QuickSelect.kthSmallest(arr, 1) == 4);
        check("k=length returns max", QuickSelect.kthSmallest(arr, arr.length) == 26);
    }

    private static void testDuplicates() {
        int[] arr = { 5, 5, 5, 1, 9, 5 };
        check("duplicates, k=1", QuickSelect.kthSmallest(arr, 1) == 1);
        check("duplicates, k=4", QuickSelect.kthSmallest(arr, 4) == 5);
        check("duplicates, k=6", QuickSelect.kthSmallest(arr, 6) == 9);
    }

    private static void testSingleElement() {
        check("single element", QuickSelect.kthSmallest(new int[] { 42 }, 1) == 42);
    }

    private static void testDoesNotMutateInput() {
        int[] arr = { 10, 4, 5, 8, 6, 11, 26 };
        int[] original = arr.clone();
        QuickSelect.kthSmallest(arr, 3);
        check("input array is not mutated", Arrays.equals(arr, original));
    }

    private static void testValidation() {
        check("null array throws", throwsNpe(() -> QuickSelect.kthSmallest(null, 1)));
        check("empty array throws", throwsIae(() -> QuickSelect.kthSmallest(new int[0], 1)));
        check("k=0 throws", throwsIae(() -> QuickSelect.kthSmallest(new int[] { 1 }, 0)));
        check("k>length throws", throwsIae(() -> QuickSelect.kthSmallest(new int[] { 1 }, 2)));
    }

    /** Cross-checks Quickselect against a sorted reference over many random inputs. */
    private static void testAgainstSortRandomized() {
        Random rnd = new Random(12345); // fixed seed for reproducibility
        boolean allMatch = true;
        for (int trial = 0; trial < 1000; trial++) {
            int n = 1 + rnd.nextInt(50);
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = rnd.nextInt(100) - 50;
            }
            int[] sorted = arr.clone();
            Arrays.sort(sorted);
            int k = 1 + rnd.nextInt(n);
            if (QuickSelect.kthSmallest(arr, k) != sorted[k - 1]) {
                allMatch = false;
                break;
            }
        }
        check("randomized cross-check vs Arrays.sort", allMatch);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + name);
        } else {
            System.out.println("  FAIL: " + name);
            failures++;
        }
    }

    private static boolean throwsNpe(Runnable r) {
        try {
            r.run();
            return false;
        } catch (NullPointerException e) {
            return true;
        }
    }

    private static boolean throwsIae(Runnable r) {
        try {
            r.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
