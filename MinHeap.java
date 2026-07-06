import java.util.*;

class MinHeap<T extends Comparable<T>> implements Iterable<T> {

    private final ArrayList<T> heapArray;
    private final Comparator<T> comparator;

    public MinHeap() {
        heapArray = new ArrayList<>();
        comparator = Comparator.naturalOrder();
    }

    public MinHeap<T> insert(T key) {
        heapArray.add(key);
        siftUp(heapArray.size() - 1);
        return this;
    }

    public MinHeap<T> decrease(int index, T new_val) {
        heapArray.set(index, new_val);
        siftUp(index);
        return this;
    }

    public MinHeap<T> increase(int index, T new_val) {
        heapArray.set(index, new_val);
        siftDown(index);
        return this;
    }

    public MinHeap<T> delete(int index) {
        int lastIndex = heapArray.size() - 1;
        if (index == lastIndex) {
            heapArray.remove(lastIndex);
            return this;
        }
        T replacement = heapArray.remove(lastIndex);
        heapArray.set(index, replacement);
        if (index > 0 && comparator.compare(replacement, heapArray.get(parent(index))) < 0) {
            siftUp(index);
        } else {
            siftDown(index);
        }
        return this;
    }

    public MinHeap<T> changeValue(int index, T new_val) {
        T current = heapArray.get(index);
        int cmp = comparator.compare(current, new_val);
        if (cmp == 0) {
            return this;
        }
        heapArray.set(index, new_val);
        if (cmp < 0) {
            siftDown(index);
        } else {
            siftUp(index);
        }
        return this;
    }

    public T getMin() {
        return heapArray.get(0);
    }

    public T extractMin() {
        if (heapArray.isEmpty()) {
            return null;
        }
        if (heapArray.size() == 1) {
            return heapArray.remove(0);
        }
        T root = heapArray.get(0);
        heapArray.set(0, heapArray.remove(heapArray.size() - 1));
        siftDown(0);
        return root;
    }

    @Override
    public Iterator<T> iterator() {
        return heapArray.iterator();
    }

    private void siftUp(int index) {
        while (index != 0 && comparator.compare(heapArray.get(index), heapArray.get(parent(index))) < 0) {
            Collections.swap(heapArray, index, parent(index));
            index = parent(index);
        }
    }

    private void siftDown(int index) {
        int size = heapArray.size();
        int l = 2 * index + 1;
        int r = 2 * index + 2;
        int smallest = index;

        if (l < size && comparator.compare(heapArray.get(l), heapArray.get(smallest)) < 0) {
            smallest = l;
        }
        if (r < size && comparator.compare(heapArray.get(r), heapArray.get(smallest)) < 0) {
            smallest = r;
        }

        if (smallest != index) {
            Collections.swap(heapArray, index, smallest);
            siftDown(smallest);
        }
    }

    private static int parent(int index) {
        return (index - 1) / 2;
    }
}
