import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A binary min-heap over arbitrary element types whose backing array grows on
 * demand and whose elements are addressed by stable {@linkplain Handle handles}.
 *
 * <p>The smallest element (per the heap's ordering) is always kept at the root,
 * readable in O(1) via {@link #getMin()} and removable in O(log n) via
 * {@link #extractMin()}. {@link #insertKey(Object)} returns a {@link Handle}
 * that tracks its element as the element moves through the array, so
 * {@link #decreaseKey(Handle, Object)}, {@link #increaseKey(Handle, Object)},
 * {@link #delete(Handle)} and {@link #changeValue(Handle, Object)} always act on
 * the intended element &mdash; never on whatever happens to occupy a stale
 * position. A handle is invalidated as soon as its element leaves the heap.
 *
 * <p>Ordering is either the elements' {@linkplain Comparable natural ordering}
 * (when no comparator is supplied) or a {@link Comparator} passed to the
 * constructor. {@code null} elements are not permitted.
 *
 * @param <T> the type of elements held in the heap
 */
class MinHeap<T> {

    /**
     * An opaque, stable reference to an element while it lives in the heap.
     *
     * <p>The handle remains valid as its element is reordered by heap
     * operations; it is invalidated (see {@link #isActive()}) once the element
     * is removed via {@link #extractMin()} or {@link #delete(Handle)}.
     *
     * <p>The only implementations are produced by {@link MinHeap#insertKey(Object)};
     * objects implementing this interface from elsewhere are rejected by the
     * methods that consume handles.
     *
     * @param <T> the element type
     */
    public interface Handle<T> {

        /** Returns the element this handle refers to. */
        T value();

        /** Returns {@code true} while the element is still in the heap. */
        boolean isActive();
    }

    /** The sole {@link Handle} implementation; its mutable state is heap-private. */
    private static final class HandleImpl<T> implements Handle<T> {
        private final MinHeap<T> owner;
        private T value;
        private int index; // current position in the heap; -1 once removed

        HandleImpl(MinHeap<T> owner, T value) {
            this.owner = owner;
            this.value = value;
            this.index = -1;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public boolean isActive() {
            return index >= 0;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;

    private Object[] heap; // Handle<T>[]; raw to allow generic array storage
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

    /**
     * Builds a heap from {@code elements} in O(n) time, using natural ordering.
     *
     * @throws NullPointerException if {@code elements} or any element is {@code null}
     */
    public MinHeap(Collection<? extends T> elements) {
        this(elements, null);
    }

    /**
     * Builds a heap from {@code elements} in O(n) time, ordered by
     * {@code comparator} (or natural ordering when it is {@code null}).
     *
     * <p>This is asymptotically faster than inserting the elements one by one
     * (O(n) vs. O(n log n)). Elements added this way are not individually
     * handle-addressable; use {@link #insertKey(Object)} when you need a
     * {@link Handle} for later updates.
     *
     * @throws NullPointerException if {@code elements} or any element is {@code null}
     */
    public MinHeap(Collection<? extends T> elements, Comparator<? super T> comparator) {
        Objects.requireNonNull(elements, "elements");
        this.comparator = comparator;
        this.heap = new Object[elements.size()];
        int n = 0;
        for (T element : elements) {
            Objects.requireNonNull(element, "elements must not contain null");
            place(new HandleImpl<>(this, element), n);
            n++;
        }
        this.size = n;
        buildHeap();
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
    private HandleImpl<T> handleAt(int i) {
        return (HandleImpl<T>) heap[i];
    }

    @SuppressWarnings("unchecked")
    private int compareValues(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super T>) a).compareTo(b);
    }

    private int compareAt(int i, int j) {
        return compareValues(handleAt(i).value, handleAt(j).value);
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

    /** Stores {@code h} at position {@code i}, keeping the handle's index in sync. */
    private void place(HandleImpl<T> h, int i) {
        heap[i] = h;
        h.index = i;
    }

    private void swap(int i, int j) {
        HandleImpl<T> hi = handleAt(i);
        HandleImpl<T> hj = handleAt(j);
        place(hj, i);
        place(hi, j);
    }

    private void growIfFull() {
        if (size < heap.length) {
            return;
        }
        int newCapacity = heap.length == 0 ? DEFAULT_CAPACITY : heap.length * 2;
        heap = Arrays.copyOf(heap, newCapacity);
    }

    @SuppressWarnings("unchecked")
    private HandleImpl<T> checkHandle(Handle<T> handle) {
        Objects.requireNonNull(handle, "handle");
        if (!(handle instanceof HandleImpl<?> impl) || impl.owner != this) {
            throw new IllegalArgumentException("handle was not created by this heap");
        }
        if (impl.index < 0) {
            throw new IllegalStateException("handle refers to an element no longer in the heap");
        }
        return (HandleImpl<T>) handle;
    }

    /** Restores the heap property by moving {@code i} toward the root. */
    private void siftUp(int i) {
        while (i > 0 && compareAt(i, parent(i)) < 0) {
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
            if (l < size && compareAt(l, smallest) < 0) {
                smallest = l;
            }
            if (r < size && compareAt(r, smallest) < 0) {
                smallest = r;
            }
            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }
    }

    /** Bottom-up heap construction: sift down every internal node, O(n) overall. */
    private void buildHeap() {
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    /** Removes whatever element currently sits at position {@code i}. */
    private void removeAt(int i) {
        HandleImpl<T> removed = handleAt(i);
        int last = size - 1;
        HandleImpl<T> lastHandle = handleAt(last);
        heap[last] = null; // avoid loitering reference
        size--;
        removed.index = -1; // invalidate the caller's handle
        if (i != last) {
            place(lastHandle, i);
            siftDown(i);
            siftUp(i);
        }
    }

    /**
     * Inserts {@code key} into the heap, growing the backing array if needed.
     *
     * @return a {@link Handle} that tracks this element for later updates/removal
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Handle<T> insertKey(T key) {
        Objects.requireNonNull(key, "key");
        growIfFull();
        HandleImpl<T> handle = new HandleImpl<>(this, key);
        place(handle, size);
        size++;
        siftUp(size - 1);
        return handle;
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
        return handleAt(0).value;
    }

    /**
     * Removes and returns the smallest element. Any handle to it is invalidated.
     *
     * @throws NoSuchElementException if the heap is empty
     */
    public T extractMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("heap is empty");
        }
        T min = handleAt(0).value;
        removeAt(0);
        return min;
    }

    /**
     * Lowers the handle's element to {@code newVal}.
     *
     * @throws NullPointerException     if {@code handle} or {@code newVal} is {@code null}
     * @throws IllegalArgumentException if {@code handle} was not created by this heap
     * @throws IllegalStateException    if the element has already been removed
     * @throws IllegalArgumentException if {@code newVal} orders after the current
     *                                  value (use {@link #increaseKey} instead)
     */
    public void decreaseKey(Handle<T> handle, T newVal) {
        HandleImpl<T> impl = checkHandle(handle);
        Objects.requireNonNull(newVal, "newVal");
        if (compareValues(newVal, impl.value) > 0) {
            throw new IllegalArgumentException("newVal orders after the current value");
        }
        impl.value = newVal;
        siftUp(impl.index);
    }

    /**
     * Raises the handle's element to {@code newVal}.
     *
     * @throws NullPointerException     if {@code handle} or {@code newVal} is {@code null}
     * @throws IllegalArgumentException if {@code handle} was not created by this heap
     * @throws IllegalStateException    if the element has already been removed
     * @throws IllegalArgumentException if {@code newVal} orders before the current
     *                                  value (use {@link #decreaseKey} instead)
     */
    public void increaseKey(Handle<T> handle, T newVal) {
        HandleImpl<T> impl = checkHandle(handle);
        Objects.requireNonNull(newVal, "newVal");
        if (compareValues(newVal, impl.value) < 0) {
            throw new IllegalArgumentException("newVal orders before the current value");
        }
        impl.value = newVal;
        siftDown(impl.index);
    }

    /**
     * Removes the handle's element from the heap and invalidates the handle.
     *
     * @throws NullPointerException     if {@code handle} is {@code null}
     * @throws IllegalArgumentException if {@code handle} was not created by this heap
     * @throws IllegalStateException    if the element has already been removed
     */
    public void delete(Handle<T> handle) {
        HandleImpl<T> impl = checkHandle(handle);
        removeAt(impl.index);
    }

    /**
     * Sets the handle's element to {@code newVal}, sifting in whichever direction
     * is needed to keep the heap valid.
     *
     * @throws NullPointerException     if {@code handle} or {@code newVal} is {@code null}
     * @throws IllegalArgumentException if {@code handle} was not created by this heap
     * @throws IllegalStateException    if the element has already been removed
     */
    public void changeValue(Handle<T> handle, T newVal) {
        HandleImpl<T> impl = checkHandle(handle);
        Objects.requireNonNull(newVal, "newVal");
        int cmp = compareValues(newVal, impl.value);
        if (cmp < 0) {
            impl.value = newVal;
            siftUp(impl.index);
        } else if (cmp > 0) {
            impl.value = newVal;
            siftDown(impl.index);
        }
        // equal: nothing to do
    }
}

class MinHeapTest {

    private static int checks = 0;
    private static int failures = 0;

    public static void main(String[] args) {
        testNaturalOrdering();
        testHeapifyFromCollection();
        testHeapifyWithComparator();
        testHeapifyEdgeCases();
        testHeapifyThenMutate();
        testAutoGrowsBeyondInitialCapacity();
        testCustomComparator();
        testHandleTracksElementAcrossMoves();
        testHandleDeleteMiddle();
        testHandleInvalidatedAfterRemoval();
        testForeignHandleRejected();
        testForeignImplementationRejected();
        testEmptyHeapThrows();
        testInvariantGuards();
        testChangeValue();
        testNullRejected();

        System.out.println();
        System.out.println(failures == 0
            ? "All " + checks + " checks passed."
            : failures + " of " + checks + " checks FAILED.");
        if (failures != 0) {
            System.exit(1);
        }
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

    private static void testHeapifyFromCollection() {
        // A deliberately unsorted collection must come out in sorted order.
        MinHeap<Integer> h = new MinHeap<>(Arrays.asList(9, 4, 7, 1, 8, 3, 6, 2, 5));
        assertEquals(9, h.size(), "size after heapify");
        assertEquals(1, h.getMin(), "min after heapify");
        for (int e = 1; e <= 9; e++) {
            assertEquals(e, h.extractMin(), "heapified order extraction");
        }
        assertTrue(h.isEmpty(), "empty after draining heapified heap");
    }

    private static void testHeapifyWithComparator() {
        MinHeap<Integer> h = new MinHeap<>(Arrays.asList(5, 1, 9, 3), Comparator.reverseOrder());
        for (int e : new int[] {9, 5, 3, 1}) {
            assertEquals(e, h.extractMin(), "heapify honours the comparator");
        }
    }

    private static void testHeapifyEdgeCases() {
        MinHeap<Integer> empty = new MinHeap<>(Arrays.asList());
        assertTrue(empty.isEmpty(), "heapify of empty collection is empty");
        assertThrows(NoSuchElementException.class, empty::getMin, "empty heapified getMin");

        MinHeap<Integer> single = new MinHeap<>(Arrays.asList(42));
        assertEquals(42, single.getMin(), "heapify of singleton");

        assertThrows(NullPointerException.class,
            () -> new MinHeap<Integer>((Collection<Integer>) null), "null collection");
        assertThrows(NullPointerException.class,
            () -> new MinHeap<>(Arrays.asList(1, null, 3)), "null element in collection");
    }

    private static void testHeapifyThenMutate() {
        // A heapified heap must behave like any other: support inserts (with
        // handles), growth, and handle-based updates afterwards.
        MinHeap<Integer> h = new MinHeap<>(Arrays.asList(10, 20, 30));
        MinHeap.Handle<Integer> handle = h.insertKey(25);
        h.insertKey(5);
        h.decreaseKey(handle, 1); // 25 -> 1 via a handle obtained after heapify
        assertEquals(1, h.extractMin(), "handle update works on a heapified heap");
        for (int e : new int[] {5, 10, 20, 30}) {
            assertEquals(e, h.extractMin(), "post-heapify order");
        }
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

        MinHeap<String> byLength = new MinHeap<>(Comparator.comparingInt(String::length));
        byLength.insertKey("ccc");
        byLength.insertKey("a");
        byLength.insertKey("bb");
        assertEquals("a", byLength.extractMin(), "shortest string first");
    }

    /**
     * The core guarantee: a handle keeps pointing at its own element even after
     * the element has been shuffled to a different array position. An index would
     * have gone stale and corrupted whichever element now sits there.
     */
    private static void testHandleTracksElementAcrossMoves() {
        MinHeap<Integer> h = new MinHeap<>();
        MinHeap.Handle<Integer> fifty = h.insertKey(50); // starts at the root (index 0)
        for (int v : new int[] {40, 30, 20, 10, 5, 1}) {
            h.insertKey(v); // each smaller insert pushes 50 deeper; its index changes
        }
        assertTrue(fifty.isActive(), "handle active before update");

        // Lower *that* element (50 -> 25). Only 50 should change; the min stays 1.
        h.decreaseKey(fifty, 25);
        assertEquals(25, fifty.value(), "handle value reflects the update");

        // Multiset must be exactly {1,5,10,20,25,30,40} -- 50 became 25, nothing else moved.
        for (int e : new int[] {1, 5, 10, 20, 25, 30, 40}) {
            assertEquals(e, h.extractMin(), "only the handle's element changed");
        }
        assertTrue(h.isEmpty(), "drained");
    }

    private static void testHandleDeleteMiddle() {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertKey(1);
        MinHeap.Handle<Integer> three = h.insertKey(3);
        for (int v : new int[] {2, 7, 8, 5}) {
            h.insertKey(v);
        }
        h.delete(three); // remove a non-root element by handle
        assertEquals(5, h.size(), "size after handle delete");
        assertTrue(!three.isActive(), "deleted handle is inactive");
        for (int e : new int[] {1, 2, 5, 7, 8}) {
            assertEquals(e, h.extractMin(), "remaining order after handle delete");
        }
    }

    private static void testHandleInvalidatedAfterRemoval() {
        MinHeap<Integer> h = new MinHeap<>();
        MinHeap.Handle<Integer> one = h.insertKey(1);
        h.insertKey(2);
        assertEquals(1, h.extractMin(), "extract the element behind the handle");
        assertTrue(!one.isActive(), "handle inactive after extractMin");
        assertThrows(IllegalStateException.class,
            () -> h.decreaseKey(one, 0), "operating on a removed element throws");
        assertThrows(IllegalStateException.class,
            () -> h.delete(one), "deleting an already-removed element throws");
    }

    private static void testForeignHandleRejected() {
        MinHeap<Integer> a = new MinHeap<>();
        MinHeap<Integer> b = new MinHeap<>();
        MinHeap.Handle<Integer> fromA = a.insertKey(1);
        assertThrows(IllegalArgumentException.class,
            () -> b.delete(fromA), "handle from another heap is rejected");
    }

    private static void testForeignImplementationRejected() {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertKey(1);
        // An outside object implementing the Handle interface is not a real
        // handle into this heap and must be refused.
        MinHeap.Handle<Integer> fake = new MinHeap.Handle<>() {
            public Integer value() { return 1; }
            public boolean isActive() { return true; }
        };
        assertThrows(IllegalArgumentException.class,
            () -> h.delete(fake), "foreign Handle implementation is rejected");
    }

    private static void testEmptyHeapThrows() {
        MinHeap<Integer> h = new MinHeap<>();
        assertThrows(NoSuchElementException.class, h::getMin, "getMin on empty");
        assertThrows(NoSuchElementException.class, h::extractMin, "extractMin on empty");
    }

    private static void testInvariantGuards() {
        MinHeap<Integer> h = new MinHeap<>();
        MinHeap.Handle<Integer> ten = h.insertKey(10);
        assertThrows(IllegalArgumentException.class,
            () -> h.decreaseKey(ten, 20), "decreaseKey cannot increase");
        assertThrows(IllegalArgumentException.class,
            () -> h.increaseKey(ten, 5), "increaseKey cannot decrease");
    }

    private static void testChangeValue() {
        MinHeap<Integer> h = new MinHeap<>();
        MinHeap.Handle<Integer> a = h.insertKey(10);
        h.insertKey(20);
        h.insertKey(30);
        h.changeValue(a, 25); // raise the current minimum
        assertEquals(20, h.getMin(), "min updates after raising an element");
        MinHeap.Handle<Integer> low = h.insertKey(100);
        h.changeValue(low, 1); // lower it below the root
        assertEquals(1, h.getMin(), "min updates after lowering an element");
    }

    private static void testNullRejected() {
        MinHeap<Integer> h = new MinHeap<>();
        assertThrows(NullPointerException.class, () -> h.insertKey(null), "insert null");
        MinHeap.Handle<Integer> one = h.insertKey(1);
        assertThrows(NullPointerException.class,
            () -> h.changeValue(one, null), "change to null");
        assertThrows(NullPointerException.class,
            () -> h.decreaseKey(null, 0), "null handle");
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
