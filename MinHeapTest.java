import java.util.NoSuchElementException;

public class MinHeapTest {
    public static void main(String[] args) {
        testDefaultConstructorProvidesUsableHeap();
        testExampleFlow();
        testQueueStyleApi();
        testHeapGrowsBeyondInitialCapacity();
        testBulkConstructionHeapifiesInput();
        testBulkConstructionDefensivelyCopiesInput();
        testRemoveAtRestoresHeapOrder();
        testChangeValueReordersBothDirections();
        testDirectionalUpdatesRejectInvalidValues();
        testInvalidIndexFailsFast();
        testClearResetsHeapState();
        testToArrayReturnsDefensiveCopy();
        testEmptyHeapBehavior();
        System.out.println("All MinHeap tests passed.");
    }

    private static void testDefaultConstructorProvidesUsableHeap() {
        MinHeap heap = new MinHeap();
        heap.offer(6);
        heap.offer(2);

        assertEquals("default constructor should create a working heap", 2, heap.peek());
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

    private static void testHeapGrowsBeyondInitialCapacity() {
        MinHeap heap = new MinHeap(1);
        heap.offer(10);
        assertTrue("single-slot heap should be full after one insert", heap.isFull());

        heap.offer(4);
        heap.offer(7);

        assertEquals("size should include all inserted values", 3, heap.size());
        assertTrue("capacity should grow to fit additional values", heap.capacity() >= 3);
        assertRemovalOrder("growth should preserve heap ordering", heap, 4, 7, 10);
    }

    private static void testBulkConstructionHeapifiesInput() {
        MinHeap heap = new MinHeap(new int[] {8, 3, 9, 1, 4});
        assertEquals("bulk constructor should heapify input", 1, heap.peek());
        assertRemovalOrder("bulk constructor should produce sorted removals", heap, 1, 3, 4, 8, 9);
    }

    private static void testBulkConstructionDefensivelyCopiesInput() {
        int[] values = {8, 3, 9, 1, 4};
        MinHeap heap = new MinHeap(values);
        values[0] = -10;

        assertEquals("constructor should copy input values", 1, heap.peek());
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
        assertRemovalOrder("heap order should remain valid after removeAt", heap, 1, 2, 7, 9);
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

    private static void testClearResetsHeapState() {
        MinHeap heap = new MinHeap(new int[] {5, 2, 8});
        heap.clear();

        assertTrue("clear should empty the heap", heap.isEmpty());
        assertEquals("clear should reset size", 0, heap.size());
        assertEquals("clear should keep current capacity available", 3, heap.capacity());
        assertTrue("toArray should be empty after clear", heap.toArray().length == 0);
    }

    private static void testToArrayReturnsDefensiveCopy() {
        MinHeap heap = new MinHeap(new int[] {6, 1, 3});
        int[] snapshot = heap.toArray();
        snapshot[0] = 99;

        assertEquals("mutating a snapshot should not affect the heap", 1, heap.peek());
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

    private static void assertRemovalOrder(String message, MinHeap heap, int... expectedValues) {
        for (int expectedValue : expectedValues) {
            assertEquals(message, expectedValue, heap.removeMin());
        }
        assertTrue(message + ": heap should be empty after draining", heap.isEmpty());
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
