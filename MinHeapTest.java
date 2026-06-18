public class MinHeapTest {
    public static void main(String[] args) {
        testExampleFlow();
        testInsertIntoFullHeap();
        testChangeValueReordersBothDirections();
        testInvalidIndexFailsFast();
        System.out.println("All MinHeap tests passed.");
    }

    private static void testExampleFlow() {
        MinHeap heap = new MinHeap(11);
        heap.insertKey(3);
        heap.insertKey(2);
        heap.deleteKey(1);
        heap.insertKey(15);
        heap.insertKey(5);
        heap.insertKey(4);
        heap.insertKey(45);

        assertEquals("extractMin should match original example", 2, heap.extractMin());
        assertEquals("getMin should match original example", 4, heap.getMin());

        heap.decreaseKey(2, 1);
        assertEquals("decreaseKey should promote the new minimum", 1, heap.getMin());
    }

    private static void testInsertIntoFullHeap() {
        MinHeap heap = new MinHeap(2);
        assertTrue("first insert should succeed", heap.insertKey(10));
        assertTrue("second insert should succeed", heap.insertKey(20));
        assertFalse("insert on full heap should fail", heap.insertKey(30));
        assertEquals("size should remain capped at capacity", 2, heap.size());
    }

    private static void testChangeValueReordersBothDirections() {
        MinHeap heap = new MinHeap(5);
        heap.insertKey(8);
        heap.insertKey(4);
        heap.insertKey(6);
        heap.insertKey(10);

        heap.changeValueOnAKey(3, 1);
        assertEquals("lowering a value should bubble it up", 1, heap.getMin());

        heap.changeValueOnAKey(0, 12);
        assertEquals("raising the root should restore heap order", 4, heap.getMin());
    }

    private static void testInvalidIndexFailsFast() {
        MinHeap heap = new MinHeap(3);
        heap.insertKey(5);

        assertThrows(
            "deleteKey should reject invalid indexes",
            IndexOutOfBoundsException.class,
            () -> heap.deleteKey(2)
        );
    }

    private static void assertEquals(String message, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    private static void assertThrows(
        String message,
        Class<? extends Throwable> expectedType,
        ThrowingRunnable action
    ) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                message + ": expected " + expectedType.getSimpleName()
                    + ", got " + thrown.getClass().getSimpleName(),
                thrown
            );
        }

        throw new AssertionError(
            message + ": expected " + expectedType.getSimpleName() + " to be thrown"
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
