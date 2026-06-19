import java.util.Arrays;
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
        testRemoveAt();
        testChangeKey();
        testAutoResize();
        testEmptyHeap();
        testFromArray();
        testWithStrings();
        testMaxHeap();
        testCustomComparator();
        testToString();
        testToArray();
        testToSortedArray();
        testSort();
        testAddAll();
        testContains();
        testRemove();
        testClear();
        testIterable();
        testEmptyArray();
        testIndexBoundsChecking();
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
        h.insert(5);
        h.insert(3);
        h.insert(8);
        h.insert(1);
        assertEqual(1, h.extractMin(), "extractMin returns smallest");
        assertEqual(3, h.extractMin(), "extractMin returns next smallest");
        assertEqual(5, h.extractMin(), "extractMin returns next");
        assertEqual(8, h.extractMin(), "extractMin returns last");
        assertTrue(h.isEmpty(), "heap is empty after all extractions");
    }

    private static void testRemoveAt() {
        MinHeap<Integer> h = MinHeap.naturalOrder(11);
        h.insert(3);
        h.insert(2);
        h.removeAt(1);
        h.insert(15);
        h.insert(5);
        h.insert(4);
        h.insert(45);
        assertEqual(2, h.extractMin(), "extractMin after removeAt");
        assertEqual(4, h.getMin(), "getMin after extract");

        MinHeap<Integer> h2 = MinHeap.naturalOrder();
        h2.insert(1);
        h2.insert(3);
        h2.insert(5);
        h2.removeAt(2);
        assertEqual(2, h2.size(), "size after removing last element");
        assertEqual(1, h2.extractMin(), "min unchanged after removing last");

        MinHeap<Integer> h3 = MinHeap.naturalOrder();
        h3.insert(1);
        h3.insert(3);
        h3.insert(5);
        h3.removeAt(0);
        assertEqual(3, h3.extractMin(), "new root after removing old root");
    }

    private static void testChangeKey() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.insert(5);
        h.insert(10);
        h.changeKey(1, 1);
        assertEqual(1, h.extractMin(), "changeKey decrease works");

        MinHeap<Integer> h2 = MinHeap.naturalOrder();
        h2.insert(3);
        h2.insert(8);
        h2.changeKey(0, 20);
        assertEqual(8, h2.extractMin(), "changeKey increase works");

        MinHeap<Integer> h3 = MinHeap.naturalOrder();
        h3.insert(7);
        h3.changeKey(0, 7);
        assertEqual(7, h3.getMin(), "changeKey no-op when value unchanged");
    }

    private static void testAutoResize() {
        MinHeap<Integer> h = MinHeap.naturalOrder(2);
        for (int i = 10; i >= 1; i--) {
            h.insert(i);
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
        h.insert("banana");
        h.insert("apple");
        h.insert("cherry");
        assertEqual("apple", h.extractMin(), "string heap extracts lexicographic minimum");
        assertEqual("banana", h.extractMin(), "string heap extracts next");
    }

    private static void testMaxHeap() {
        MinHeap<Integer> h = MinHeap.reverseOrder();
        h.insert(5);
        h.insert(3);
        h.insert(8);
        h.insert(1);
        assertEqual(8, h.extractMin(), "max-heap: extracts largest first");
        assertEqual(5, h.extractMin(), "max-heap: extracts next largest");
    }

    private static void testCustomComparator() {
        MinHeap<String> byLength = MinHeap.withComparator(Comparator.comparingInt(String::length));
        byLength.insert("banana");
        byLength.insert("fig");
        byLength.insert("kiwi");
        assertEqual("fig", byLength.extractMin(), "custom comparator: shortest string first");
        assertEqual("kiwi", byLength.extractMin(), "custom comparator: next shortest");
    }

    private static void testToString() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertEqual("[]", h.toString(), "toString on empty heap");
        h.insert(3);
        h.insert(1);
        h.insert(2);
        assertTrue(h.toString().startsWith("[1"), "toString: root is minimum");
        assertTrue(h.toString().contains("2"), "toString: contains 2");
        assertTrue(h.toString().contains("3"), "toString: contains 3");
    }

    private static void testToArray() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertEqual(0, h.toArray().length, "toArray on empty heap");

        h.insert(3);
        h.insert(1);
        h.insert(2);
        Object[] arr = h.toArray();
        assertEqual(3, arr.length, "toArray: correct length");
        assertEqual(1, arr[0], "toArray: root is minimum");
        int sum = (Integer) arr[0] + (Integer) arr[1] + (Integer) arr[2];
        assertEqual(6, sum, "toArray: all elements present");
        assertEqual(3, h.size(), "toArray is non-destructive");
    }

    private static void testToSortedArray() {
        Integer[] input = {5, 3, 8, 1, 9, 2};
        MinHeap<Integer> h = MinHeap.from(input);
        Object[] sorted = h.toSortedArray();

        assertEqual(6, sorted.length, "toSortedArray: correct length");
        Integer[] expected = {1, 2, 3, 5, 8, 9};
        for (int i = 0; i < expected.length; i++) {
            assertEqual(expected[i], sorted[i], "toSortedArray: element at index " + i);
        }
        assertEqual(6, h.size(), "toSortedArray: non-destructive");

        MinHeap<Integer> maxH = MinHeap.from(input, Comparator.reverseOrder());
        Object[] desc = maxH.toSortedArray();
        assertEqual(9, desc[0], "toSortedArray reverse: first is maximum");
        assertEqual(1, desc[5], "toSortedArray reverse: last is minimum");
    }

    private static void testSort() {
        Integer[] arr = {5, 3, 8, 1, 9, 2};
        MinHeap.sort(arr);
        Integer[] ascending = {1, 2, 3, 5, 8, 9};
        for (int i = 0; i < ascending.length; i++) {
            assertEqual(ascending[i], arr[i], "sort ascending: index " + i);
        }

        MinHeap.sort(arr, Comparator.reverseOrder());
        Integer[] descending = {9, 8, 5, 3, 2, 1};
        for (int i = 0; i < descending.length; i++) {
            assertEqual(descending[i], arr[i], "sort descending: index " + i);
        }

        Integer[] single = {42};
        MinHeap.sort(single);
        assertEqual(42, single[0], "sort single element");

        Integer[] alreadySorted = {1, 2, 3, 4, 5};
        MinHeap.sort(alreadySorted);
        assertEqual(1, alreadySorted[0], "sort already-sorted: first");
        assertEqual(5, alreadySorted[4], "sort already-sorted: last");

        String[] strings = {"banana", "fig", "kiwi", "apple"};
        MinHeap.sort(strings, Comparator.comparingInt(String::length));
        assertEqual("fig", strings[0], "sort strings by length: shortest first");
        assertEqual("banana", strings[3], "sort strings by length: longest last");
    }

    private static void testAddAll() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.addAll(Arrays.asList(5, 3, 8, 1, 9, 2));
        assertEqual(6, h.size(), "addAll: correct size");
        assertEqual(1, h.extractMin(), "addAll: extracts minimum");
        assertEqual(2, h.extractMin(), "addAll: extracts next");

        MinHeap<Integer> h2 = MinHeap.naturalOrder();
        h2.insert(10);
        h2.addAll(Arrays.asList(4, 7));
        assertEqual(3, h2.size(), "addAll to non-empty: correct size");
        assertEqual(4, h2.extractMin(), "addAll to non-empty: minimum from added elements");

        MinHeap<Integer> h3 = MinHeap.naturalOrder();
        h3.addAll(Arrays.asList());
        assertTrue(h3.isEmpty(), "addAll empty iterable: heap unchanged");
    }

    private static void testContains() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertTrue(!h.contains(5), "contains: empty heap returns false");

        h.insert(3);
        h.insert(1);
        h.insert(5);
        assertTrue(h.contains(1), "contains: finds minimum");
        assertTrue(h.contains(5), "contains: finds non-root element");
        assertTrue(!h.contains(7), "contains: absent element returns false");
    }

    private static void testRemove() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        assertTrue(!h.remove(5), "remove: empty heap returns false");

        h.insert(3);
        h.insert(1);
        h.insert(5);
        h.insert(2);
        assertTrue(h.remove(1), "remove: can remove minimum");
        assertEqual(3, h.size(), "remove: size decremented");
        assertEqual(2, h.getMin(), "remove: heap property maintained after removing min");

        assertTrue(h.remove(5), "remove: can remove non-root element");
        assertEqual(2, h.size(), "remove: size decremented again");
        assertTrue(!h.contains(5), "remove: element no longer present");

        assertTrue(!h.remove(99), "remove: absent element returns false");
    }

    private static void testClear() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.insert(3);
        h.insert(1);
        h.insert(2);
        h.clear();
        assertTrue(h.isEmpty(), "clear: heap is empty");
        assertEqual(0, h.size(), "clear: size is 0");
        assertThrows(() -> h.getMin(), NoSuchElementException.class, "clear: getMin throws after clear");

        h.insert(10);
        assertEqual(10, h.getMin(), "clear: can insert after clear");
    }

    private static void testIterable() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        int count = 0;
        for (int x : h) count++;
        assertEqual(0, count, "iterable: empty heap yields no elements");

        h.insert(3);
        h.insert(1);
        h.insert(2);
        int sum = 0;
        for (int x : h) sum += x;
        assertEqual(6, sum, "iterable: visits all elements");
        assertEqual(3, h.size(), "iterable: non-destructive");

        MinHeap<String> sh = MinHeap.naturalOrder();
        sh.insert("b");
        sh.insert("a");
        StringBuilder sb = new StringBuilder();
        for (String s : sh) sb.append(s);
        assertTrue(sb.toString().contains("a") && sb.toString().contains("b"),
            "iterable: works with String heap");
    }

    private static void testEmptyArray() {
        Integer[] empty = {};
        MinHeap<Integer> h = MinHeap.from(empty);
        assertTrue(h.isEmpty(), "from empty array: heap is empty");
        assertEqual(0, h.toSortedArray().length, "toSortedArray of empty heap");

        MinHeap.sort(empty);
        assertEqual(0, empty.length, "sort empty array: no-op");
    }

    private static void testIndexBoundsChecking() {
        MinHeap<Integer> h = MinHeap.naturalOrder();
        h.insert(10);
        h.insert(20);

        assertThrows(() -> h.removeAt(-1),    IndexOutOfBoundsException.class, "removeAt: negative index");
        assertThrows(() -> h.removeAt(2),     IndexOutOfBoundsException.class, "removeAt: index >= size");
        assertThrows(() -> h.changeKey(-1, 5),    IndexOutOfBoundsException.class, "changeKey: negative index");
        assertThrows(() -> h.changeKey(2, 5),     IndexOutOfBoundsException.class, "changeKey: index >= size");

        assertEqual(2, h.size(), "bounds checks leave heap unmodified");
    }

    private static void testOriginalScenario() {
        MinHeap<Integer> h = MinHeap.naturalOrder(11);
        h.insert(3);
        h.insert(2);
        h.removeAt(1);
        h.insert(15);
        h.insert(5);
        h.insert(4);
        h.insert(45);
        assertEqual(2, h.extractMin(), "original scenario: extractMin");
        assertEqual(4, h.getMin(), "original scenario: getMin");
        h.changeKey(2, 1);
        assertEqual(1, h.getMin(), "original scenario: getMin after changeKey");
    }
}
