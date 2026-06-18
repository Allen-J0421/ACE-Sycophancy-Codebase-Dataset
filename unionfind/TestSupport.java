package unionfind;

import java.util.Arrays;

public class TestSupport {
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private int passed;
    private int failed;

    public void assertTrue(String label, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + label);
            passed++;
        } else {
            System.out.println("FAIL: " + label);
            failed++;
        }
    }

    public void assertFalse(String label, boolean condition) {
        assertTrue(label, !condition);
    }

    public void assertEquals(String label, int expected, int actual) {
        if (expected == actual) {
            System.out.println("PASS: " + label);
            passed++;
        } else {
            System.out.println("FAIL: " + label + " (expected " + expected + ", got " + actual + ")");
            failed++;
        }
    }

    public void assertArrayEquals(String label, int[] expected, int[] actual) {
        if (Arrays.equals(expected, actual)) {
            System.out.println("PASS: " + label);
            passed++;
        } else {
            System.out.println("FAIL: " + label
                    + " (expected " + Arrays.toString(expected)
                    + ", got " + Arrays.toString(actual) + ")");
            failed++;
        }
    }

    public void assertThrows(String label, Class<? extends Exception> expectedType, ThrowingRunnable runnable) {
        try {
            runnable.run();
            System.out.println("FAIL: " + label + " (expected " + expectedType.getSimpleName() + ", none thrown)");
            failed++;
        } catch (Exception e) {
            if (expectedType.isInstance(e)) {
                System.out.println("PASS: " + label);
                passed++;
            } else {
                System.out.println("FAIL: " + label + " (expected " + expectedType.getSimpleName()
                        + ", got " + e.getClass().getSimpleName() + ")");
                failed++;
            }
        }
    }

    public void runSection(String name, Runnable body) {
        System.out.println("\n-- " + name + " --");
        try {
            body.run();
        } catch (Exception e) {
            System.out.println("ERROR: unexpected exception: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            failed++;
        }
    }

    public void printSummary() {
        System.out.println("\nResults: " + passed + " passed, " + failed + " failed"
                + " (" + (passed + failed) + " total).");
    }

    public boolean allPassed() {
        return failed == 0;
    }
}
