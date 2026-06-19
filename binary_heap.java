import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A binary min-heap over arbitrary element types whose backing array grows on
 * demand.
 *
 * <p>The smallest element (per the heap's ordering) is always kept at the root,
 * readable in O(1) via {@link #getMin()} and removable in O(log n) via
 * {@link #extractMin()}. Insertion is amortised O(log n); when the backing
 * array is full it doubles, so {@link #insertKey(Object)} never fails.
 *
 * <p>Ordering is either the elements' {@linkplain Comparable natural ordering}
 * (when no comparator is supplied) or a {@link Comparator} passed to the
 * constructor. {@code null} elements are not permitted.
 *
 * <p>The index-based operations ({@link #decreaseKey(int, Object)},
 * {@link #increaseKey(int, Object)}, {@link #deleteKey(int)} and
 * {@link #changeValueOnAKey(int, Object)}) address an element by its current
 * position in the backing array, not by its value. Because heap operations move
 * elements around, a position is only meaningful immediately after it is
 * observed.
 *
 * @param <T> the type of elements held in the heap
 */
class MinHeap<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private Object[] heap;
    private int size;
    private final Comparator<? super T> comparator;

    /** Creates an empty heap ordered by its elements' natural ordering. */
    public MinHeap() {
        this(DEFAULT_CAPACITY, null);
    }

    /**
     * Creates an empty heap ordered by {@code comparator}.
     *
     * @param comparator the ordering to use, or {@code null} for natural ordering
     */
    public MinHeap(Comparator<? super T> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    /**
     * Creates an empty heap with a hint for the initial backing-array size.
     *
     * @param initialCapacity the initial backing-array length (it still grows as needed)
     * @param comparator      the ordering to use, or {@code null} for natural ordering
     * @throws IllegalArgumentException if {@code initialCapacity} is negative
     */
    public MinHeap(int initialCapacity, Comparator<? super T> comparator) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be non-negative: " + initialCapacity);
        }
        this.heap = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    /** Returns the number of elements currently stored. */
    public int size() {
        return size;
    }

    /** Returns {@code true} if the heap holds no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int i) {
        return (T) heap[i];
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super T>) a).compareTo(b);
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
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "index " + index + " out of bounds for heap size " + size);
        }
    }

    private void growIfFull() {
        if (size < heap.length) {
            return;
        }
        int newCapacity = heap.length == 0 ? DEFAULT_CAPACITY : heap.length * 2;
        heap = Arrays.copyOf(heap, newCapacity);
    }

    /** Restores the heap property by moving {@code i} toward the root. */
    private void siftUp(int i) {
        while (i > 0 && compare(elementAt(i), elementAt(parent(i))) < 0) {
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
            if (l < size && compare(elementAt(l), elementAt(smallest)) < 0) {
                smallest = l;
            }
            if (r < size && compare(elementAt(r), elementAt(smallest)) < 0) {
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
     * Inserts {@code key} into the heap, growing the backing array if needed.
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void insertKey(T key) {
        Objects.requireNonNull(key, "key");
        growIfFull();
        heap[size] = key;
        size++;
        siftUp(size - 1);
    }

    /**
     * Returns the smallest element without removing it.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    public T getMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        return elementAt(0);
    }

    /**
     * Removes and returns the smallest element.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    public T extractMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        T min = elementAt(0);
        int last = size - 1;
        heap[0] = heap[last];
        heap[last] = null; // avoid loitering reference
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    /**
     * Lowers the element at {@code index} to {@code newVal}.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     * @throws NullPointerException      if {@code newVal} is {@code null}
     * @throws IllegalArgumentException  if {@code newVal} orders after the current
     *                                   value (use {@link #increaseKey} instead)
     */
    public void decreaseKey(int index, T newVal) {
        checkIndex(index);
        Objects.requireNonNull(newVal, "newVal");
        if (compare(newVal, elementAt(index)) > 0) {
            throw new IllegalArgumentException("newVal orders after the current value");
        }
        heap[index] = newVal;
        siftUp(index);
    }

    /**
     * Raises the element at {@code index} to {@code newVal}.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     * @throws NullPointerException      if {@code newVal} is {@code null}
     * @throws IllegalArgumentException  if {@code newVal} orders before the current
     *                                   value (use {@link #decreaseKey} instead)
     */
    public void increaseKey(int index, T newVal) {
        checkIndex(index);
        Objects.requireNonNull(newVal, "newVal");
        if (compare(newVal, elementAt(index)) < 0) {
            throw new IllegalArgumentException("newVal orders before the current value");
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
        T moved = elementAt(last);
        heap[last] = null; // avoid loitering reference
        size--;
        if (index != last) {
            // Move the last element into the hole and restore the invariant.
            // It can only ever need to travel in one direction; the other sift
            // is a no-op, so running both is correct and simplest.
            heap[index] = moved;
            siftDown(index);
            siftUp(index);
        }
    }

    /**
     * Sets the element at {@code index} to {@code newVal}, sifting in whichever
     * direction is needed to keep the heap valid.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not a live position
     * @throws NullPointerException      if {@code newVal} is {@code null}
     */
    public void changeValueOnAKey(int index, T newVal) {
        checkIndex(index);
        Objects.requireNonNull(newVal, "newVal");
        int cmp = compare(newVal, elementAt(index));
        if (cmp < 0) {
            decreaseKey(index, newVal);
        } else if (cmp > 0) {
            increaseKey(index, newVal);
        }
        // equal: nothing to do
    }
}

class MinHeapTest {

    private static int checks = 0;
    private static int failures = 0;

    public static void main(String[] args) {
        testLegacyScenario();
        testNaturalOrdering();
        testAutoGrowsBeyondInitialCapacity();
        testCustomComparator();
        testEmptyHeapThrows();
        testIndexValidation();
        testInvariantGuards();
        testChangeValueOnAKey();
        testDeleteMiddleElement();
        testNullRejected();

        System.out.println();
        System.out.println(failures == 0
            ? "All " + checks + " checks passed."
            : failures + " of " + checks + " checks FAILED.");
        if (failures != 0) {
            System.exit(1);
        }
    }

    /** Reproduces the original demo (now on Integer); expected output was "2 4 1". */
    private static void testLegacyScenario() {
        MinHeap<Integer> h = new MinHeap<>();
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

    private static void testNaturalOrdering() {
        MinHeap<Integer> h = new MinHeap<>();
        for (int v : new int[] {9, 4, 7, 1, 8, 3}) {
            h.insertKey(v);
        }
        assertEquals(6, h.size(), "size after inserts");
        for (int e : new int[] {1, 3, 4, 7, 8, 9}) {
            assertEquals(e, h.extractMin(), "natural order extraction");
        }
        assertTrue(h.isEmpty(), "empty after draining");
    }

    private static void testAutoGrowsBeyondInitialCapacity() {
        MinHeap<Integer> h = new MinHeap<>(2, null); // tiny initial capacity
        int n = 1000;
        for (int v = n; v >= 1; v--) {
            h.insertKey(v); // never rejected, array grows as needed
        }
        assertEquals(n, h.size(), "size after growth");
        for (int e = 1; e <= n; e++) {
            assertEquals(e, h.extractMin(), "ordering preserved across growth");
        }
        assertTrue(h.isEmpty(), "empty after draining grown heap");
    }

    private static void testCustomComparator() {
        // Reverse ordering turns the min-heap into a max-heap.
        MinHeap<Integer> h = new MinHeap<>(Comparator.reverseOrder());
        for (int v : new int[] {5, 1, 9, 3}) {
            h.insertKey(v);
        }
        assertEquals(9, h.getMin(), "max-heap root is the largest value");
        assertEquals(9, h.extractMin(), "extract largest first");
        assertEquals(5, h.extractMin(), "then next largest");

        // Works for non-Comparable usage patterns too: order Strings by length.
        MinHeap<String> byLength = new MinHeap<>(Comparator.comparingInt(String::length));
        byLength.insertKey("ccc");
        byLength.insertKey("a");
        byLength.insertKey("bb");
        assertEquals("a", byLength.extractMin(), "shortest string first");
    }

    private static void testEmptyHeapThrows() {
        MinHeap<Integer> h = new MinHeap<>();
        assertThrows(NoSuchElementException.class, h::getMin, "getMin on empty");
        assertThrows(NoSuchElementException.class, h::extractMin, "extractMin on empty");
    }

    private static void testIndexValidation() {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertKey(10);
        assertThrows(IndexOutOfBoundsException.class,
            () -> h.decreaseKey(5, 1), "decreaseKey out of range");
        assertThrows(IndexOutOfBoundsException.class,
            () -> h.deleteKey(-1), "deleteKey negative index");
    }

    private static void testInvariantGuards() {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertKey(10);
        assertThrows(IllegalArgumentException.class,
            () -> h.decreaseKey(0, 20), "decreaseKey cannot increase");
        assertThrows(IllegalArgumentException.class,
            () -> h.increaseKey(0, 5), "increaseKey cannot decrease");
    }

    private static void testChangeValueOnAKey() {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertKey(10);
        h.insertKey(20);
        h.insertKey(30);
        h.changeValueOnAKey(0, 25); // raise the current minimum
        assertEquals(20, h.getMin(), "min updates after raising root");
        h.changeValueOnAKey(h.size() - 1, 1); // lower some leaf below the root
        assertEquals(1, h.getMin(), "min updates after lowering a leaf");
    }

    private static void testDeleteMiddleElement() {
        MinHeap<Integer> h = new MinHeap<>();
        for (int v : new int[] {1, 3, 2, 7, 8, 5}) {
            h.insertKey(v);
        }
        // Heap layout is [1, 3, 2, 7, 8, 5]; index 1 holds 3. Removing it pulls
        // the last element (5) into the hole, which must then sift down.
        h.deleteKey(1);
        assertEquals(5, h.size(), "size after delete");
        for (int e : new int[] {1, 2, 5, 7, 8}) {
            assertEquals(e, h.extractMin(), "remaining order after middle delete");
        }
        assertTrue(h.isEmpty(), "empty after draining post-delete");
    }

    private static void testNullRejected() {
        MinHeap<Integer> h = new MinHeap<>();
        assertThrows(NullPointerException.class, () -> h.insertKey(null), "insert null");
        h.insertKey(1);
        assertThrows(NullPointerException.class,
            () -> h.changeValueOnAKey(0, null), "change to null");
    }

    // --- tiny assertion harness ------------------------------------------

    private interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        record(Objects.equals(expected, actual),
            label + " (expected " + expected + ", got " + actual + ")");
    }

    private static void assertTrue(boolean cond, String label) {
        record(cond, label + " (expected true)");
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
