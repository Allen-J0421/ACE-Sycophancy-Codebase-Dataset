import java.util.Arrays;
import java.util.List;

/**
 * Minimal, dependency-free assertion helpers for the hand-rolled test runners
 * in this project. An instance tallies pass/fail counts as assertions run and
 * can print a summary and report overall success.
 *
 * <p>Usage:
 * <pre>{@code
 *   TestAssertions t = new TestAssertions();
 *   t.equal("name", expected, actual);
 *   t.report();
 *   if (!t.allPassed()) System.exit(1);
 * }</pre>
 */
public final class TestAssertions {

    private int passed = 0;
    private int failed = 0;

    /** Asserts two matrices are deeply equal. */
    public void equal(String name, int[][] expected, int[][] actual) {
        if (Arrays.deepEquals(expected, actual)) {
            pass(name);
        } else {
            fail(name, Arrays.deepToString(expected), Arrays.deepToString(actual));
        }
    }

    /** Asserts two lists are equal. */
    public void equal(String name, List<?> expected, List<?> actual) {
        if (expected.equals(actual)) {
            pass(name);
        } else {
            fail(name, String.valueOf(expected), String.valueOf(actual));
        }
    }

    /** Asserts a condition holds. */
    public void isTrue(String name, boolean condition) {
        if (condition) {
            pass(name);
        } else {
            fail(name, "true", "false");
        }
    }

    /** Asserts that running {@code action} throws {@link IllegalArgumentException}. */
    public void throwsIllegalArgument(String name, Runnable action) {
        try {
            action.run();
            fail(name, "IllegalArgumentException", "no exception");
        } catch (IllegalArgumentException expected) {
            pass(name);
        }
    }

    /** Asserts that running {@code action} throws {@link IndexOutOfBoundsException}. */
    public void throwsIndexOutOfBounds(String name, Runnable action) {
        try {
            action.run();
            fail(name, "IndexOutOfBoundsException", "no exception");
        } catch (IndexOutOfBoundsException expected) {
            pass(name);
        }
    }

    /** Whether every assertion so far has passed. */
    public boolean allPassed() {
        return failed == 0;
    }

    /** Number of failed assertions. */
    public int failures() {
        return failed;
    }

    /** Prints a one-line summary of results. */
    public void report() {
        int total = passed + failed;
        if (failed == 0) {
            System.out.println("All tests passed (" + total + ").");
        } else {
            System.out.println(failed + " of " + total + " test(s) failed.");
        }
    }

    private void pass(String name) {
        passed++;
        System.out.println("PASS: " + name);
    }

    private void fail(String name, String expected, String actual) {
        failed++;
        System.out.println("FAIL: " + name);
        System.out.println("  expected: " + expected);
        System.out.println("  actual:   " + actual);
    }
}
