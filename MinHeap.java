import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

class MinHeap<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private final Comparator<T> comparator;
    private Object[] heap;
    private int size;

    private MinHeap(Comparator<T> comparator, int initialCapacity) {
        this.comparator = comparator;
        this.heap = new Object[Math.max(1, initialCapacity)];
        this.size = 0;
    }

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

    public static <T> MinHeap<T> from(T[] arr, Comparator<T> comparator) {
        MinHeap<T> h = new MinHeap<>(comparator, arr.length);
        System.arraycopy(arr, 0, h.heap, 0, arr.length);
        h.size = arr.length;
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            h.heapify(i);
        }
        return h;
    }

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

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private void grow() {
        heap = Arrays.copyOf(heap, heap.length * 2);
    }

    public void insertKey(T key) {
        if (size == heap.length) {
            grow();
        }
        int i = size;
        heap[i] = key;
        size++;
        while (i != 0 && compare(i, parent(i)) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public void decreaseKey(int i, T newVal) {
        heap[i] = newVal;
        while (i != 0 && compare(i, parent(i)) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public T getMin() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        return at(0);
    }

    public T extractMin() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        if (size == 1) {
            T root = at(0);
            heap[0] = null;
            size--;
            return root;
        }
        T root = at(0);
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        heapify(0);
        return root;
    }

    public void deleteKey(int i) {
        if (i == size - 1) {
            heap[size - 1] = null;
            size--;
            return;
        }
        heap[i] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        // The replacement element may need to move up or down.
        if (i > 0 && compare(i, parent(i)) < 0) {
            while (i != 0 && compare(i, parent(i)) < 0) {
                swap(i, parent(i));
                i = parent(i);
            }
        } else {
            heapify(i);
        }
    }

    private void heapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;
        if (l < size && compare(l, smallest) < 0) smallest = l;
        if (r < size && compare(r, smallest) < 0) smallest = r;
        if (smallest != i) {
            swap(i, smallest);
            heapify(smallest);
        }
    }

    public void increaseKey(int i, T newVal) {
        heap[i] = newVal;
        heapify(i);
    }

    public void changeKey(int i, T newVal) {
        int cmp = comparator.compare(at(i), newVal);
        if (cmp == 0) return;
        if (cmp < 0) {
            increaseKey(i, newVal);
        } else {
            decreaseKey(i, newVal);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
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
