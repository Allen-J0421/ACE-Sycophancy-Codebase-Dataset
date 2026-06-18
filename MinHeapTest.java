import java.util.NoSuchElementException;

public class MinHeapTest {
    public static void main(String[] args) {
        testExampleFlow();
        testQueueStyleApi();
        testInsertIntoFullHeap();
        testRemoveAtRestoresHeapOrder();
        testChangeValueReordersBothDirections();
        testDirectionalUpdatesRejectInvalidValues();
        testInvalidIndexFailsFast();
        testEmptyHeapBehavior();
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

    private static void testQueueStyleApi() {
        MinHeap heap = new MinHeap(4);
        assertTrue("offer should insert first value", heap.offer(9));
        assertTrue("offer should insert second value", heap.offer(3));
        assertTrue("offer should insert third value", heap.offer(7));
        assertEquals("peek should expose the minimum", 3, heap.peek());
        assertEquals("removeMin should return the minimum", 3, heap.removeMin());
        assertEquals("peek should update after removal", 7, heap.peek());
    }

    private static void testInsertIntoFullHeap() {
        MinHeap heap = new MinHeap(2);
        assertTrue("first insert should succeed", heap.insertKey(10));
        assertTrue("second insert should succeed", heap.insertKey(20));
        assertFalse("insert on full heap should fail", heap.insertKey(30));
        assertTrue("heap should report full capacity", heap.isFull());
        assertEquals("size should remain capped at capacity", 2, heap.size());
        assertEquals("capacity should reflect constructor value", 2, heap.capacity());
    }

    private static void testRemoveAtRestoresHeapOrder() {
        MinHeap heap = new MinHeap(6);
        heap.offer(1);
        heap.offer(4);
        heap.offer(2);
        heap.offer(9);
        heap.offer(7);

        assertEquals("removeAt should return the removed value", 4, heap.removeAt(1));
        assertEquals("removal should preserve the heap minimum", 1, heap.peek());
        assertEquals("next removal should return remaining minimum", 1, heap.removeMin());
        assertEquals("heap order should remain valid after removeAt", 2, heap.peek());
    }

    private static void testChangeValueReordersBothDirections() {
        MinHeap heap = new MinHeap(5);
        heap.insertKey(8);
        heap.insertKey(4);
        heap.insertKey(6);
        heap.insertKey(10);

        heap.updateValue(3, 1);
        assertEquals("lowering a value should bubble it up", 1, heap.getMin());

        heap.updateValue(0, 12);
        assertEquals("raising the root should restore heap order", 4, heap.getMin());
    }

    private static void testDirectionalUpdatesRejectInvalidValues() {
        MinHeap heap = new MinHeap(3);
        heap.offer(5);
        heap.offer(9);

        assertThrows(
            "decreaseKey should reject larger values",
            IllegalArgumentException.class,
            () -> heap.decreaseKey(1, 12)
        );

        assertThrows(
            "increaseKey should reject smaller values",
            IllegalArgumentException.class,
            () -> heap.increaseKey(1, 1)
        );
    }

    private static void testInvalidIndexFailsFast() {
        MinHeap heap = new MinHeap(3);
        heap.insertKey(5);

        assertThrows(
            "deleteKey should reject invalid indexes",
            IndexOutOfBoundsException.class,
            () -> heap.deleteKey(2)
        );

        assertThrows(
            "valueAt should reject invalid indexes",
            IndexOutOfBoundsException.class,
            () -> heap.valueAt(-1)
        );
    }

    private static void testEmptyHeapBehavior() {
        MinHeap heap = new MinHeap(1);

        assertThrows(
            "peek should reject an empty heap",
            NoSuchElementException.class,
            heap::peek
        );

        assertThrows(
            "removeMin should reject an empty heap",
            NoSuchElementException.class,
            heap::removeMin
        );

        assertEquals(
            "extractMin should preserve legacy empty-heap behavior",
            Integer.MAX_VALUE,
            heap.extractMin()
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
