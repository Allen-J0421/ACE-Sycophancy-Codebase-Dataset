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

        MinHeap heap = new MinHeap(4);
        assertEquals(heap.capacity(), 4, "Capacity accessor should match constructor input");
        assertTrue(heap.offer(8), "Expected insert to succeed");
        assertTrue(heap.offer(3), "Expected insert to succeed");
        assertTrue(heap.offer(5), "Expected insert to succeed");
        heap.updateKey(0, 1);
        assertEquals(heap.peek(), 1, "Key update should restore heap order");
        assertEquals(heap.size(), 3, "Heap size should reflect inserted elements");
        assertTrue(heap.offer(9), "Expected final insert to succeed before reaching capacity");
        assertFalse(heap.offer(11), "Insert should fail once the heap reaches capacity");

        heap.removeAtIndex(1);
        assertEquals(heap.size(), 3, "Delete should reduce heap size");
        assertEquals(heap.peek(), 1, "Delete should preserve the minimum element");

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

        MinHeap h = new MinHeap(11);
        h.offer(3);
        h.offer(2);
        h.removeAtIndex(1);
        h.offer(15);
        h.offer(5);
        h.offer(4);
        h.offer(45);
        System.out.print(h.poll() + " ");
        System.out.print(h.peek() + " ");

        h.decreaseKey(2, 1);
        System.out.print(h.peek());
    }
}
