import java.util.NoSuchElementException;

class MinHeap<T extends Comparable<T>> {

    private final Object[] heap;
    private final int capacity;
    private int size;

    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new Object[capacity];
        this.size = 0;
    }

    // O(n) construction via Floyd's bottom-up heapification.
    public static <T extends Comparable<T>> MinHeap<T> from(T[] arr) {
        MinHeap<T> h = new MinHeap<>(arr.length);
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

    public boolean insertKey(T key) {
        if (size == capacity) {
            return false;
        }

        int i = size;
        heap[i] = key;
        size++;

        while (i != 0 && at(i).compareTo(at(parent(i))) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
        return true;
    }

    public void decreaseKey(int i, T newVal) {
        heap[i] = newVal;
        while (i != 0 && at(i).compareTo(at(parent(i))) < 0) {
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
        if (i > 0 && at(i).compareTo(at(parent(i))) < 0) {
            while (i != 0 && at(i).compareTo(at(parent(i))) < 0) {
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

        if (l < size && at(l).compareTo(at(smallest)) < 0) {
            smallest = l;
        }
        if (r < size && at(r).compareTo(at(smallest)) < 0) {
            smallest = r;
        }
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
        if (at(i).compareTo(newVal) == 0) {
            return;
        }
        if (at(i).compareTo(newVal) < 0) {
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
}
