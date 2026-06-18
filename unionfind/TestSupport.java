package unionfind;

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

    public void assertThrows(String label, ThrowingRunnable runnable) {
        try {
            runnable.run();
            System.out.println("FAIL: " + label + " (expected exception, none thrown)");
            failed++;
        } catch (Exception e) {
            System.out.println("PASS: " + label);
            passed++;
        }
    }

    public void printSummary() {
        System.out.println("\nResults: " + passed + " passed, " + failed + " failed.");
    }

    public boolean allPassed() {
        return failed == 0;
    }
}
