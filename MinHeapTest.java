public class MinHeapTest {
    private static void runSanityChecks() {
        MinHeap empty = new MinHeap(0);
        if (empty.peek() != MinHeap.EMPTY_VALUE) {
            throw new AssertionError("Empty heap should report Integer.MAX_VALUE");
        }
        if (empty.poll() != MinHeap.EMPTY_VALUE) {
            throw new AssertionError("Empty heap extraction should report Integer.MAX_VALUE");
        }

        MinHeap heap = new MinHeap(4);
        if (heap.capacity() != 4) {
            throw new AssertionError("Capacity accessor should match constructor input");
        }
        if (!heap.offer(8) || !heap.offer(3) || !heap.offer(5)) {
            throw new AssertionError("Expected inserts to succeed");
        }
        heap.setKey(0, 1);
        if (heap.peek() != 1) {
            throw new AssertionError("Key update should restore heap order");
        }
        if (heap.size() != 3) {
            throw new AssertionError("Heap size should reflect inserted elements");
        }
        if (!heap.offer(9)) {
            throw new AssertionError("Expected final insert to succeed before reaching capacity");
        }
        if (heap.offer(11)) {
            throw new AssertionError("Insert should fail once the heap reaches capacity");
        }

        heap.removeAt(1);
        if (heap.size() != 3) {
            throw new AssertionError("Delete should reduce heap size");
        }
        if (heap.peek() != 1) {
            throw new AssertionError("Delete should preserve the minimum element");
        }

        boolean rejected = false;
        try {
            heap.decreaseKey(0, 99);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        if (!rejected) {
            throw new AssertionError("decreaseKey should reject larger values");
        }

        rejected = false;
        try {
            heap.increaseKey(0, 0);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        if (!rejected) {
            throw new AssertionError("increaseKey should reject smaller values");
        }
    }

    public static void main(String[] args) {
        runSanityChecks();

        MinHeap h = new MinHeap(11);
        h.offer(3);
        h.offer(2);
        h.removeAt(1);
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
