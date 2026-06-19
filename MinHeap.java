import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

class MinHeap<T> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private final Comparator<T> comparator;
    private Object[] heap;
    private int size;

    private MinHeap(Comparator<T> comparator, int initialCapacity) {
        this.comparator = comparator;
        this.heap = new Object[Math.max(1, initialCapacity)];
        this.size = 0;
    }

    // --- Factory methods ---

    public static <T extends Comparable<T>> MinHeap<T> naturalOrder() {
        return new MinHeap<>(Comparator.<T>naturalOrder(), DEFAULT_CAPACITY);
    }

    public static <T extends Comparable<T>> MinHeap<T> naturalOrder(int initialCapacity) {
        return new MinHeap<>(Comparator.<T>naturalOrder(), initialCapacity);
    }

    public static <T extends Comparable<T>> MinHeap<T> reverseOrder() {
        return new MinHeap<>(Comparator.<T>reverseOrder(), DEFAULT_CAPACITY);
    }

    public static <T> MinHeap<T> withComparator(Comparator<T> comparator) {
        return new MinHeap<>(comparator, DEFAULT_CAPACITY);
    }

    public static <T extends Comparable<T>> MinHeap<T> from(T[] arr) {
        return from(arr, Comparator.<T>naturalOrder());
    }

    // O(n) construction via Floyd's bottom-up heapification.
    public static <T> MinHeap<T> from(T[] arr, Comparator<T> comparator) {
        MinHeap<T> h = new MinHeap<>(comparator, arr.length);
        System.arraycopy(arr, 0, h.heap, 0, arr.length);
        h.size = arr.length;
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            h.heapify(i);
        }
        return h;
    }

    // --- Static sort ---

    public static <T extends Comparable<T>> void sort(T[] arr) {
        sort(arr, Comparator.<T>naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<T> comparator) {
        MinHeap<T> h = from(arr, comparator);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = h.extractMin();
        }
    }

    // --- Internal helpers ---

    @SuppressWarnings("unchecked")
    private T at(int i) {
        return (T) heap[i];
    }

    private int compare(int a, int b) {
        return comparator.compare(at(a), at(b));
    }

    private void swap(int a, int b) {
        Object temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i)   { return 2 * i + 1; }
    private int right(int i)  { return 2 * i + 2; }

    private void grow() {
        heap = Arrays.copyOf(heap, heap.length * 2);
    }

    private void requireValidIndex(int i) {
        if (i < 0 || i >= size)
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds for size " + size);
    }

    private void siftUp(int i) {
        while (i != 0 && compare(i, parent(i)) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    // Iterative sift-down — equivalent to the tail-recursive form but avoids call-stack growth.
    private void heapify(int i) {
        while (true) {
            int l = left(i);
            int r = right(i);
            int smallest = i;
            if (l < size && compare(l, smallest) < 0) smallest = l;
            if (r < size && compare(r, smallest) < 0) smallest = r;
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    // --- Mutation ---

    public void insert(T value) {
        if (size == heap.length) grow();
        heap[size] = value;
        siftUp(size++);
    }

    public void decreaseKey(int i, T newVal) {
        requireValidIndex(i);
        heap[i] = newVal;
        siftUp(i);
    }

    public void increaseKey(int i, T newVal) {
        requireValidIndex(i);
        heap[i] = newVal;
        heapify(i);
    }

    public void changeKey(int i, T newVal) {
        requireValidIndex(i);
        int cmp = comparator.compare(at(i), newVal);
        if (cmp == 0) return;
        heap[i] = newVal;
        if (cmp < 0) heapify(i);
        else siftUp(i);
    }

    public boolean remove(T value) {
        for (int i = 0; i < size; i++) {
            if (comparator.compare(at(i), value) == 0) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    public void clear() {
        Arrays.fill(heap, 0, size, null);
        size = 0;
    }

    // A displaced parent is always >= its new children (heap invariant), so calling
    // siftUp then heapify unconditionally is correct: the inapplicable direction exits in O(1).
    public void removeAt(int i) {
        requireValidIndex(i);
        heap[i] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0 && i < size) {
            siftUp(i);
            heapify(i);
        }
    }

    // --- Access ---

    public T getMin() {
        if (size == 0) throw new NoSuchElementException("Heap is empty");
        return at(0);
    }

    public T extractMin() {
        if (size == 0) throw new NoSuchElementException("Heap is empty");
        T root = at(0);
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        heapify(0);
        return root;
    }

    public boolean contains(T value) {
        for (int i = 0; i < size; i++) {
            if (comparator.compare(at(i), value) == 0) return true;
        }
        return false;
    }

    // Returns the heap elements in heap order (not sorted).
    public Object[] toArray() {
        return Arrays.copyOf(heap, size);
    }

    // Returns the heap elements in sorted order without modifying this heap.
    @SuppressWarnings("unchecked")
    public Object[] toSortedArray() {
        Object[] result = Arrays.copyOf(heap, size);
        Arrays.sort(result, 0, size, (a, b) -> comparator.compare((T) a, (T) b));
        return result;
    }

    // --- Utility ---

    public boolean isEmpty() { return size == 0; }
    public int size()        { return size; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return at(cursor++);
            }
        };
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(at(i));
        }
        return sb.append("]").toString();
    }
}
