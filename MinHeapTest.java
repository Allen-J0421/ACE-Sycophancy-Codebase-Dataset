import java.util.Comparator;
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
        testAutoResize();
        testEmptyHeap();
        testFromArray();
        testWithStrings();
        testMaxHeap();
        testCustomComparator();
        testToString();
        testOriginalScenario();

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        if (failed > 0) System.exit(1);
    }

    private static void assertEqual(Object expected, Object actual, String msg) {
        if (expected.equals(actual)) {
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
        MinHeap<Integer> h = MinHeap.naturalOrder();
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
        MinHeap<Integer> h = MinHeap.naturalOrder(11);
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        assertEqual(2, h.extractMin(), "extractMin after deleteKey");
        assertEqual(4, h.getMin(), "getMin after extract");

        MinHeap<Integer> h2 = MinHeap.naturalOrder();
        h2.insertKey(1);
        h2.insertKey(3);
        h2.insertKey(5);
        h2.deleteKey(2);
        assertEqual(2, h2.size(), "size after deleting last element");
        assertEqual(1, h2.extractMin(), "min unchanged after deleting last");

        MinHeap<Integer> h3 = MinHeap.naturalOrder();
        h3.insertKey(1);
        h3.insertKey(3);
        h3.insertKey(5);
        h3.deleteKey(0);
        assertEqual(3, h3.extractMin(), "new root after deleting old root");
    }

    private static void testDecreaseKey() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.insertKey(10);
        h.insertKey(20);
        h.insertKey(30);
        h.decreaseKey(2, 5);
        assertEqual(5, h.extractMin(), "decreaseKey moves element up correctly");
    }

    private static void testIncreaseKey() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.insertKey(1);
        h.insertKey(5);
        h.insertKey(10);
        h.increaseKey(0, 15);
        assertEqual(5, h.extractMin(), "increaseKey on root restores heap property");
    }

    private static void testChangeKey() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.insertKey(5);
        h.insertKey(10);
        h.changeKey(1, 1);
        assertEqual(1, h.extractMin(), "changeKey decrease works");

        MinHeap<Integer> h2 = MinHeap.naturalOrder();
        h2.insertKey(3);
        h2.insertKey(8);
        h2.changeKey(0, 20);
        assertEqual(8, h2.extractMin(), "changeKey increase works");

        MinHeap<Integer> h3 = MinHeap.naturalOrder();
        h3.insertKey(7);
        h3.changeKey(0, 7);
        assertEqual(7, h3.getMin(), "changeKey no-op when value unchanged");
    }

    private static void testAutoResize() {
        MinHeap<Integer> h = MinHeap.naturalOrder(2);
        for (int i = 10; i >= 1; i--) {
            h.insertKey(i);
        }
        assertEqual(10, h.size(), "auto-resize: correct size after growing beyond initial capacity");
        assertEqual(1, h.extractMin(), "auto-resize: extracts minimum correctly");
        assertEqual(2, h.extractMin(), "auto-resize: maintains order after resize");
    }

    private static void testEmptyHeap() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertTrue(h.isEmpty(), "new heap is empty");
        assertEqual(0, h.size(), "new heap has size 0");
        assertThrows(() -> h.extractMin(), NoSuchElementException.class, "extractMin on empty throws");
        assertThrows(() -> h.getMin(), NoSuchElementException.class, "getMin on empty throws");
    }

    private static void testFromArray() {
        Integer[] arr = {5, 3, 8, 1, 9, 2};

        MinHeap<Integer> h = MinHeap.from(arr);
        assertEqual(6, h.size(), "from: correct size");
        assertEqual(1, h.extractMin(), "from: extracts minimum");
        assertEqual(2, h.extractMin(), "from: extracts next");
        assertEqual(3, h.extractMin(), "from: extracts in sorted order");

        Integer[] single = {42};
        MinHeap<Integer> h2 = MinHeap.from(single);
        assertEqual(42, h2.extractMin(), "from single-element array");
        assertTrue(h2.isEmpty(), "empty after extracting sole element");

        MinHeap<Integer> h3 = MinHeap.from(arr, Comparator.reverseOrder());
        assertEqual(9, h3.extractMin(), "from with reverse comparator extracts maximum");
    }

    private static void testWithStrings() {
        MinHeap<String> h = MinHeap.naturalOrder();
        h.insertKey("banana");
        h.insertKey("apple");
        h.insertKey("cherry");
        assertEqual("apple", h.extractMin(), "string heap extracts lexicographic minimum");
        assertEqual("banana", h.extractMin(), "string heap extracts next");
    }

    private static void testMaxHeap() {
        MinHeap<Integer> h = MinHeap.reverseOrder();
        h.insertKey(5);
        h.insertKey(3);
        h.insertKey(8);
        h.insertKey(1);
        assertEqual(8, h.extractMin(), "max-heap: extracts largest first");
        assertEqual(5, h.extractMin(), "max-heap: extracts next largest");
    }

    private static void testCustomComparator() {
        MinHeap<String> byLength = MinHeap.withComparator(Comparator.comparingInt(String::length));
        byLength.insertKey("banana");
        byLength.insertKey("fig");
        byLength.insertKey("kiwi");
        assertEqual("fig", byLength.extractMin(), "custom comparator: shortest string first");
        assertEqual("kiwi", byLength.extractMin(), "custom comparator: next shortest");
    }

    private static void testToString() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertEqual("[]", h.toString(), "toString on empty heap");
        h.insertKey(3);
        h.insertKey(1);
        h.insertKey(2);
        // The root (index 0) is always the minimum; other positions reflect heap structure.
        assertTrue(h.toString().startsWith("[1"), "toString: root is minimum");
        assertTrue(h.toString().contains("2"), "toString: contains 2");
        assertTrue(h.toString().contains("3"), "toString: contains 3");
    }

    private static void testOriginalScenario() {
        MinHeap<Integer> h = MinHeap.naturalOrder(11);
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
