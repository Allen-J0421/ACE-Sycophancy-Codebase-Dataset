import java.util.*;

class MinHeap<T extends Comparable<T>> implements Iterable<T> {

    private final ArrayList<T> heapArray;
    private final Heapify<T> strategy;

    public MinHeap() {
        this(new BinaryHeapStrategy<T>(Comparator.naturalOrder()));
    }

    public MinHeap(Heapify<T> strategy) {
        heapArray = new ArrayList<>();
        this.strategy = strategy;
    }

    public MinHeap<T> insert(T key) {
        heapArray.add(key);
        strategy.siftUp(heapArray, heapArray.size() - 1);
        return this;
    }

    public MinHeap<T> decrease(int index, T new_val) {
        heapArray.set(index, new_val);
        strategy.fix(heapArray, index);
        return this;
    }

    public MinHeap<T> increase(int index, T new_val) {
        heapArray.set(index, new_val);
        strategy.fix(heapArray, index);
        return this;
    }

    public MinHeap<T> delete(int index) {
        int lastIndex = heapArray.size() - 1;
        if (index == lastIndex) {
            heapArray.remove(lastIndex);
            return this;
        }
        heapArray.set(index, heapArray.remove(lastIndex));
        strategy.fix(heapArray, index);
        return this;
    }

    public MinHeap<T> changeValue(int index, T new_val) {
        if (heapArray.get(index).equals(new_val)) {
            return this;
        }
        heapArray.set(index, new_val);
        strategy.fix(heapArray, index);
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
        strategy.siftDown(heapArray, 0);
        return root;
    }

    @Override
    public Iterator<T> iterator() {
        return heapArray.iterator();
    }
}
