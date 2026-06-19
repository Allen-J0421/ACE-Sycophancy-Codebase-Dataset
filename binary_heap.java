import java.util.NoSuchElementException;

/**
 * A fixed-capacity binary min-heap over primitive {@code int}s.
 *
 * <p>The smallest value is always kept at the root, readable in O(1) via
 * {@link #getMin()} and removable in O(log n) via {@link #extractMin()}.
 * Insertion is O(log n).
 *
 * <p>The index-based operations ({@link #decreaseKey(int, int)},
 * {@link #increaseKey(int, int)}, {@link #deleteKey(int)} and
 * {@link #changeValueOnAKey(int, int)}) address an element by its current
 * position in the backing array, not by its value. Because heap operations
 * move elements around, a position is only meaningful immediately after it is
 * observed.
 */
class MinHeap {

    private final int[] heap;
    private int size;

    /**
     * Creates an empty heap that can hold up to {@code capacity} elements.
     *
     * @throws IllegalArgumentException if {@code capacity} is negative
     */
    public MinHeap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative: " + capacity);
        }
        heap = new int[capacity];
        size = 0;
    }

    /** Returns the number of elements currently stored. */
    public int size() {
        return size;
    }

    /** Returns {@code true} if the heap holds no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns {@code true} if the heap has reached its capacity. */
    public boolean isFull() {
        return size == heap.length;
    }

    private static int parent(int i) {
        return (i - 1) / 2;
    }

    private static int left(int i) {
        return 2 * i + 1;
    }

    private static int right(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j) {
        int tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "index " + index + " out of bounds for heap size " + size);
        }
    }

    /** Restores the heap property by moving {@code i} toward the root. */
    private void siftUp(int i) {
        while (i > 0 && heap[i] < heap[parent(i)]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /** Restores the heap property by moving {@code i} toward the leaves. */
    private void siftDown(int i) {
        while (true) {
            int smallest = i;
            int l = left(i);
            int r = right(i);
            if (l < size && heap[l] < heap[smallest]) {
                smallest = l;
            }
            if (r < size && heap[r] < heap[smallest]) {
                smallest = r;
            }
            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }
    }

    /**
     * Inserts {@code key} into the heap.
     *
     * @return {@code true} if it was stored, {@code false} if the heap is full
     */
    public boolean insertKey(int key) {
        if (isFull()) {
            return false;
        }
        heap[size] = key;
        size++;
        siftUp(size - 1);
        return true;
    }

    /**
     * Returns the smallest value without removing it.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    public int getMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        return heap[0];
    }

    /**
     * Removes and returns the smallest value.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    public int extractMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        int min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    /**
     * Lowers the value at {@code index} to {@code newVal}.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     * @throws IllegalArgumentException  if {@code newVal} is larger than the
     *                                   current value (which would break the
     *                                   heap; use {@link #increaseKey} instead)
     */
    public void decreaseKey(int index, int newVal) {
        checkIndex(index);
        if (newVal > heap[index]) {
            throw new IllegalArgumentException(
                "newVal " + newVal + " is greater than current value " + heap[index]);
        }
        heap[index] = newVal;
        siftUp(index);
    }

    /**
     * Raises the value at {@code index} to {@code newVal}.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     * @throws IllegalArgumentException  if {@code newVal} is smaller than the
     *                                   current value (which would break the
     *                                   heap; use {@link #decreaseKey} instead)
     */
    public void increaseKey(int index, int newVal) {
        checkIndex(index);
        if (newVal < heap[index]) {
            throw new IllegalArgumentException(
                "newVal " + newVal + " is less than current value " + heap[index]);
        }
        heap[index] = newVal;
        siftDown(index);
    }

    /**
     * Removes the element at {@code index}.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     */
    public void deleteKey(int index) {
        checkIndex(index);
        int last = size - 1;
        size--;
        if (index != last) {
            // Move the last element into the hole and restore the invariant.
            // It can only ever need to travel in one direction; the other
            // sift is a no-op, so running both is correct and simplest.
            heap[index] = heap[last];
            siftDown(index);
            siftUp(index);
        }
    }

    /**
     * Sets the value at {@code index} to {@code newVal}, sifting in whichever
     * direction is needed to keep the heap valid.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     */
    public void changeValueOnAKey(int index, int newVal) {
        checkIndex(index);
        if (newVal < heap[index]) {
            decreaseKey(index, newVal);
        } else if (newVal > heap[index]) {
            increaseKey(index, newVal);
        }
        // equal: nothing to do
    }
}

class MinHeapTest {

    private static int checks = 0;
    private static int failures = 0;

    public static void main(String[] args) {
        testOriginalScenario();
        testInsertAndExtractOrder();
        testFullHeapRejectsInsert();
        testEmptyHeapThrows();
        testIndexValidation();
        testInvariantGuards();
        testChangeValueOnAKey();
        testDeleteMiddleElement();

        System.out.println();
        System.out.println(failures == 0
            ? "All " + checks + " checks passed."
            : failures + " of " + checks + " checks FAILED.");
        if (failures != 0) {
            System.exit(1);
        }
    }

    /** Reproduces the behaviour of the legacy demo: expected output was "2 4 1". */
    private static void testOriginalScenario() {
        MinHeap h = new MinHeap(11);
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        assertEquals(2, h.extractMin(), "legacy: extractMin");
        assertEquals(4, h.getMin(), "legacy: getMin after extract");
        h.decreaseKey(2, 1);
        assertEquals(1, h.getMin(), "legacy: getMin after decreaseKey");
    }

    private static void testInsertAndExtractOrder() {
        MinHeap h = new MinHeap(6);
        int[] input = {9, 4, 7, 1, 8, 3};
        for (int v : input) {
            assertTrue(h.insertKey(v), "insert " + v);
        }
        assertEquals(6, h.size(), "size after inserts");
        int[] expected = {1, 3, 4, 7, 8, 9};
        for (int e : expected) {
            assertEquals(e, h.extractMin(), "extract should yield sorted order");
        }
        assertTrue(h.isEmpty(), "heap empty after draining");
    }

    private static void testFullHeapRejectsInsert() {
        MinHeap h = new MinHeap(2);
        assertTrue(h.insertKey(1), "first insert");
        assertTrue(h.insertKey(2), "second insert");
        assertTrue(h.isFull(), "heap reports full");
        assertFalse(h.insertKey(3), "insert past capacity rejected");
        assertEquals(2, h.size(), "size unchanged after rejected insert");
    }

    private static void testEmptyHeapThrows() {
        MinHeap h = new MinHeap(4);
        assertThrows(NoSuchElementException.class, h::getMin, "getMin on empty");
        assertThrows(NoSuchElementException.class, h::extractMin, "extractMin on empty");
    }

    private static void testIndexValidation() {
        MinHeap h = new MinHeap(4);
        h.insertKey(10);
        assertThrows(IndexOutOfBoundsException.class,
            () -> h.decreaseKey(5, 1), "decreaseKey out of range");
        assertThrows(IndexOutOfBoundsException.class,
            () -> h.deleteKey(-1), "deleteKey negative index");
    }

    private static void testInvariantGuards() {
        MinHeap h = new MinHeap(4);
        h.insertKey(10);
        assertThrows(IllegalArgumentException.class,
            () -> h.decreaseKey(0, 20), "decreaseKey cannot increase");
        assertThrows(IllegalArgumentException.class,
            () -> h.increaseKey(0, 5), "increaseKey cannot decrease");
    }

    private static void testChangeValueOnAKey() {
        MinHeap h = new MinHeap(4);
        h.insertKey(10);
        h.insertKey(20);
        h.insertKey(30);
        h.changeValueOnAKey(0, 25); // raise the current minimum
        assertEquals(20, h.getMin(), "min updates after raising root");
        h.changeValueOnAKey(h.size() - 1, 1); // lower some leaf below the root
        assertEquals(1, h.getMin(), "min updates after lowering a leaf");
    }

    private static void testDeleteMiddleElement() {
        MinHeap h = new MinHeap(6);
        for (int v : new int[] {1, 3, 2, 7, 8, 5}) {
            h.insertKey(v);
        }
        // Heap layout is [1, 3, 2, 7, 8, 5]; index 1 holds 3. Removing it pulls
        // the last element (5) into the hole, which must then sift down.
        h.deleteKey(1);
        assertEquals(5, h.size(), "size after delete");
        int[] expected = {1, 2, 5, 7, 8};
        for (int e : expected) {
            assertEquals(e, h.extractMin(), "remaining order after middle delete");
        }
        assertTrue(h.isEmpty(), "empty after draining post-delete");
    }

    // --- tiny assertion harness ------------------------------------------

    private interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static void assertEquals(int expected, int actual, String label) {
        record(expected == actual, label + " (expected " + expected + ", got " + actual + ")");
    }

    private static void assertTrue(boolean cond, String label) {
        record(cond, label + " (expected true)");
    }

    private static void assertFalse(boolean cond, String label) {
        record(!cond, label + " (expected false)");
    }

    private static void assertThrows(Class<? extends Throwable> expected,
                                     ThrowingRunnable action, String label) {
        try {
            action.run();
            record(false, label + " (expected " + expected.getSimpleName() + ", nothing thrown)");
        } catch (Throwable t) {
            record(expected.isInstance(t),
                label + " (expected " + expected.getSimpleName()
                    + ", got " + t.getClass().getSimpleName() + ")");
        }
    }

    private static void record(boolean passed, String detail) {
        checks++;
        if (!passed) {
            failures++;
            System.out.println("FAIL: " + detail);
        }
    }
}
