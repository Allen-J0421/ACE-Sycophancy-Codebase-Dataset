public class MinHeapTest {
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(int actual, int expected, String message) {
        if (actual != expected) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrowsIllegalArgument(Runnable action, String message) {
        boolean rejected = false;
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        assertTrue(rejected, message);
    }

    private static void runSanityChecks() {
        MinHeap empty = new MinHeap(0);
        assertEquals(empty.peek(), MinHeap.EMPTY_VALUE, "Empty heap should report Integer.MAX_VALUE");
        assertEquals(empty.poll(), MinHeap.EMPTY_VALUE, "Empty heap extraction should report Integer.MAX_VALUE");

        MinHeap built = new MinHeap(new int[] { 7, 1, 9, 3, 5 });
        assertEquals(built.capacity(), 5, "Array constructor should size the heap to the input length");
        assertEquals(built.size(), 5, "Array constructor should populate the heap");
        assertEquals(built.peek(), 1, "Heapify should promote the smallest value");
        assertEquals(built.poll(), 1, "Polling should remove the heapified minimum");
        assertEquals(built.peek(), 3, "Heapify should preserve the next minimum after polling");

        MinHeap heap = new MinHeap(4);
        assertEquals(heap.capacity(), 4, "Capacity accessor should match constructor input");
        assertTrue(heap.offer(8), "Expected insert to succeed");
        assertTrue(heap.offer(3), "Expected insert to succeed");
        assertTrue(heap.offer(5), "Expected insert to succeed");
        heap.decreaseKey(0, 1);
        assertEquals(heap.peek(), 1, "Key update should restore heap order");
        assertEquals(heap.size(), 3, "Heap size should reflect inserted elements");
        assertTrue(heap.offer(9), "Expected final insert to succeed before reaching capacity");
        assertFalse(heap.offer(11), "Insert should fail once the heap reaches capacity");

        assertEquals(heap.poll(), 1, "Polling should remove the current minimum");
        assertEquals(heap.size(), 3, "Delete should reduce heap size");
        assertEquals(heap.peek(), 3, "Delete should preserve the next minimum element");

        assertThrowsIllegalArgument(
            () -> heap.decreaseKey(0, 99),
            "decreaseKey should reject larger values"
        );
        assertThrowsIllegalArgument(
            () -> heap.increaseKey(0, 0),
            "increaseKey should reject smaller values"
        );
    }

    public static void main(String[] args) {
        runSanityChecks();

        MinHeap h = new MinHeap(new int[] { 4, 2, 5, 15, 45 });
        System.out.print(h.poll() + " ");
        System.out.print(h.peek() + " ");

        h.decreaseKey(2, 1);
        System.out.print(h.peek());
    }
}
