/**
 * Demonstrates the two-pointer technique for the "two sum" problem.
 *
 * <p>The two-pointer scan runs in O(n) time and O(1) extra space, but it relies
 * on the input being sorted in ascending order: shrinking the window from either
 * end is only meaningful when moving a pointer monotonically changes the sum.
 */
class TwoPointers {

    /**
     * Reports whether two distinct elements of {@code sortedArr} sum to {@code target}.
     *
     * @param sortedArr array of ints sorted in ascending order; must not be null
     * @param target    the sum to search for
     * @return {@code true} if some pair of distinct elements sums to {@code target}
     * @throws NullPointerException if {@code sortedArr} is null
     */
    static boolean hasPairWithSum(int[] sortedArr, int target) {
        int left = 0;
        int right = sortedArr.length - 1;

        while (left < right) {
            // widen to long so values near Integer.MAX/MIN_VALUE can't overflow
            long sum = (long) sortedArr[left] + sortedArr[right];

            if (sum == target) {
                return true;
            } else if (sum < target) {
                left++;   // need a larger sum: advance the lower end
            } else {
                right--;  // need a smaller sum: retreat the upper end
            }
        }

        return false;
    }

    public static void main(String[] args) {
        // Original demo: -3 + 1 == -2  ->  true
        System.out.println(hasPairWithSum(new int[]{-3, -1, 0, 1, 2}, -2));

        runTests();
    }

    // --- Minimal dependency-free test harness ------------------------------

    private static void runTests() {
        int passed = 0;
        int failed = 0;

        int[] outcomes = {
            assertEquals("finds a pair",       hasPairWithSum(new int[]{-3, -1, 0, 1, 2}, -2), true),
            assertEquals("no matching pair",   hasPairWithSum(new int[]{1, 2, 3, 4}, 100),     false),
            assertEquals("uses the two ends",  hasPairWithSum(new int[]{2, 7, 11, 15}, 17),    true),
            assertEquals("empty array",        hasPairWithSum(new int[]{}, 0),                 false),
            assertEquals("single element",     hasPairWithSum(new int[]{5}, 10),               false),
            assertEquals("negatives only",     hasPairWithSum(new int[]{-8, -3, -1}, -4),      true),
            // With int arithmetic, 1 + Integer.MAX_VALUE wraps to Integer.MIN_VALUE, which would
            // falsely match a MIN_VALUE target. The long widening makes the real sum (2147483648)
            // not equal MIN_VALUE, so the correct answer is false.
            assertEquals("no int overflow",
                hasPairWithSum(new int[]{1, Integer.MAX_VALUE}, Integer.MIN_VALUE),            false),
        };

        for (int outcome : outcomes) {
            if (outcome == 1) {
                passed++;
            } else {
                failed++;
            }
        }

        System.out.printf("Tests: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            throw new AssertionError(failed + " test(s) failed");
        }
    }

    /** Prints a PASS/FAIL line for one case and returns 1 on pass, 0 on fail. */
    private static int assertEquals(String name, boolean actual, boolean expected) {
        boolean ok = actual == expected;
        System.out.printf("  [%s] %s (expected %b, got %b)%n", ok ? "PASS" : "FAIL", name, expected, actual);
        return ok ? 1 : 0;
    }
}
