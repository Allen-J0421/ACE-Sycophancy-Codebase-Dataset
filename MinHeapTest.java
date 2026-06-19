import java.util.NoSuchElementException;

class MinHeapTest {

    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testInsertAndExtract();
        testDeleteKey();
        testDecreaseKey();
        testIncreaseKey();
        testChangeKey();
        testCapacityLimit();
        testEmptyHeap();
        testOriginalScenario();

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        if (failed > 0) System.exit(1);
    }

    private static void assertEqual(int expected, int actual, String msg) {
        if (expected == actual) {
            System.out.println("PASS: " + msg);
            passed++;
        } else {
            System.out.println("FAIL: " + msg + " (expected=" + expected + ", got=" + actual + ")");
            failed++;
        }
    }

    private static void assertTrue(boolean condition, String msg) {
        if (condition) {
            System.out.println("PASS: " + msg);
            passed++;
        } else {
            System.out.println("FAIL: " + msg);
            failed++;
        }
    }

    private static void assertThrows(ThrowingRunnable r, Class<? extends Exception> exClass, String msg) {
        try {
            r.run();
            System.out.println("FAIL: " + msg + " (no exception thrown)");
            failed++;
        } catch (Exception e) {
            if (exClass.isInstance(e)) {
                System.out.println("PASS: " + msg);
                passed++;
            } else {
                System.out.println("FAIL: " + msg + " (wrong exception: " + e.getClass().getSimpleName() + ")");
                failed++;
            }
        }
    }

    private static void testInsertAndExtract() {
        MinHeap h = new MinHeap(10);
        h.insertKey(5);
        h.insertKey(3);
        h.insertKey(8);
        h.insertKey(1);
        assertEqual(1, h.extractMin(), "extractMin returns smallest");
        assertEqual(3, h.extractMin(), "extractMin returns next smallest");
        assertEqual(5, h.extractMin(), "extractMin returns next");
        assertEqual(8, h.extractMin(), "extractMin returns last");
        assertTrue(h.isEmpty(), "heap is empty after all extractions");
    }

    private static void testDeleteKey() {
        MinHeap h = new MinHeap(11);
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        assertEqual(2, h.extractMin(), "extractMin after deleteKey");
        assertEqual(4, h.getMin(), "getMin after extract");
    }

    private static void testDecreaseKey() {
        MinHeap h = new MinHeap(10);
        h.insertKey(10);
        h.insertKey(20);
        h.insertKey(30);
        h.decreaseKey(2, 5);
        assertEqual(5, h.extractMin(), "decreaseKey moves element up correctly");
    }

    private static void testIncreaseKey() {
        MinHeap h = new MinHeap(10);
        h.insertKey(1);
        h.insertKey(5);
        h.insertKey(10);
        h.increaseKey(0, 15);
        assertEqual(5, h.extractMin(), "increaseKey on root restores heap property");
    }

    private static void testChangeKey() {
        MinHeap h = new MinHeap(10);
        h.insertKey(5);
        h.insertKey(10);
        h.changeKey(1, 1);
        assertEqual(1, h.extractMin(), "changeKey decrease works");

        MinHeap h2 = new MinHeap(10);
        h2.insertKey(3);
        h2.insertKey(8);
        h2.changeKey(0, 20);
        assertEqual(8, h2.extractMin(), "changeKey increase works");
    }

    private static void testCapacityLimit() {
        MinHeap h = new MinHeap(2);
        assertTrue(h.insertKey(1), "insert succeeds when not full");
        assertTrue(h.insertKey(2), "insert succeeds when not full");
        assertTrue(!h.insertKey(3), "insert fails when full");
        assertEqual(2, h.size(), "size correct when full");
    }

    private static void testEmptyHeap() {
        MinHeap h = new MinHeap(5);
        assertTrue(h.isEmpty(), "new heap is empty");
        assertEqual(0, h.size(), "new heap has size 0");
        assertEqual(Integer.MAX_VALUE, h.extractMin(), "extractMin on empty returns MAX_VALUE");
        assertThrows(() -> h.getMin(), NoSuchElementException.class, "getMin on empty heap throws");
    }

    private static void testOriginalScenario() {
        MinHeap h = new MinHeap(11);
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        assertEqual(2, h.extractMin(), "original scenario: extractMin");
        assertEqual(4, h.getMin(), "original scenario: getMin");
        h.decreaseKey(2, 1);
        assertEqual(1, h.getMin(), "original scenario: getMin after decreaseKey");
    }
}
