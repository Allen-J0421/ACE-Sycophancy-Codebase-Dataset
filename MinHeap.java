import java.util.*;

class MinHeap<T extends Comparable<T>> implements Iterable<T> {

    private final ArrayList<T> heapArray;
    private final HeapHelper<T> helper;

    public MinHeap() {
        heapArray = new ArrayList<>();
        helper = new HeapHelper<>(heapArray);
    }

    public void insertKey(T key) {
        heapArray.add(key);
        helper.siftUp(heapArray.size() - 1);
    }

    public void decreaseKey(int index, T new_val) {
        heapArray.set(index, new_val);
        helper.siftUp(index);
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

    public void deleteKey(int index) {
        int lastIndex = heapArray.size() - 1;
        if (index == lastIndex) {
            heapArray.remove(lastIndex);
            return;
        }
        heapArray.set(index, heapArray.remove(lastIndex));
        if (index < heapArray.size()) {
            if (index > 0 && heapArray.get(index).compareTo(heapArray.get(helper.parent(index))) < 0) {
                helper.siftUp(index);
            } else {
                helper.siftDown(index);
            }
        }
    }

    public void increaseKey(int index, T new_val) {
        heapArray.set(index, new_val);
        helper.siftDown(index);
    }

    public void changeValueOnAKey(int index, T new_val) {
        if (heapArray.get(index).equals(new_val)) {
            return;
        }
        if (heapArray.get(index).compareTo(new_val) < 0) {
            increaseKey(index, new_val);
        } else {
            decreaseKey(index, new_val);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return heapArray.iterator();
    }
}
