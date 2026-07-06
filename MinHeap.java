import java.util.*;

class MinHeap<T extends Comparable<T>> implements Iterable<T> {

    private final ArrayList<T> heapArray;
    private final HeapHelper<T> helper;

    public MinHeap() {
        heapArray = new ArrayList<>();
        helper = new HeapHelper<>(heapArray);
    }

    public MinHeap<T> insert(T key) {
        heapArray.add(key);
        helper.siftUp(heapArray.size() - 1);
        return this;
    }

    public MinHeap<T> decrease(int index, T new_val) {
        heapArray.set(index, new_val);
        helper.siftUp(index);
        return this;
    }

    public MinHeap<T> increase(int index, T new_val) {
        heapArray.set(index, new_val);
        helper.siftDown(index);
        return this;
    }

    public MinHeap<T> delete(int index) {
        int lastIndex = heapArray.size() - 1;
        if (index == lastIndex) {
            heapArray.remove(lastIndex);
            return this;
        }
        heapArray.set(index, heapArray.remove(lastIndex));
        if (index < heapArray.size()) {
            if (index > 0 && heapArray.get(index).compareTo(heapArray.get(helper.parent(index))) < 0) {
                helper.siftUp(index);
            } else {
                helper.siftDown(index);
            }
        }
        return this;
    }

    public MinHeap<T> changeValue(int index, T new_val) {
        if (heapArray.get(index).equals(new_val)) {
            return this;
        }
        if (heapArray.get(index).compareTo(new_val) < 0) {
            return increase(index, new_val);
        } else {
            return decrease(index, new_val);
        }
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
        helper.siftDown(0);

        return root;
    }

    @Override
    public Iterator<T> iterator() {
        return heapArray.iterator();
    }
}
