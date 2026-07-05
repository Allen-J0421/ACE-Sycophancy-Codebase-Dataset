import java.util.*;

class MinHeap<T extends Comparable<T>> {

    private ArrayList<T> heapArray;

    public MinHeap() {
        heapArray = new ArrayList<>();
    }

    private void swap(int a, int b) {
        T temp = heapArray.get(a);
        heapArray.set(a, heapArray.get(b));
        heapArray.set(b, temp);
    }

    private int parent(int key) {
        return (key - 1) / 2;
    }

    private int left(int key) {
        return 2 * key + 1;
    }

    private int right(int key) {
        return 2 * key + 2;
    }

    public void insertKey(T key) {
        heapArray.add(key);
        int i = heapArray.size() - 1;

        while (i != 0 && heapArray.get(i).compareTo(heapArray.get(parent(i))) < 0) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public void decreaseKey(int index, T new_val) {
        heapArray.set(index, new_val);

        while (index != 0 && heapArray.get(index).compareTo(heapArray.get(parent(index))) < 0) {
            swap(index, parent(index));
            index = parent(index);
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
        MinHeapify(0);

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
            if (index > 0 && heapArray.get(index).compareTo(heapArray.get(parent(index))) < 0) {
                int i = index;
                while (i != 0 && heapArray.get(i).compareTo(heapArray.get(parent(i))) < 0) {
                    swap(i, parent(i));
                    i = parent(i);
                }
            } else {
                MinHeapify(index);
            }
        }
    }

    private void MinHeapify(int key) {
        int l = left(key);
        int r = right(key);
        int size = heapArray.size();

        int smallest = key;
        if (l < size && heapArray.get(l).compareTo(heapArray.get(smallest)) < 0) {
            smallest = l;
        }
        if (r < size && heapArray.get(r).compareTo(heapArray.get(smallest)) < 0) {
            smallest = r;
        }

        if (smallest != key) {
            swap(key, smallest);
            MinHeapify(smallest);
        }
    }

    public void increaseKey(int index, T new_val) {
        heapArray.set(index, new_val);
        MinHeapify(index);
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
}

class MinHeapTest {
    public static void main(String[] args) {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        System.out.print(h.extractMin() + " ");
        System.out.print(h.getMin() + " ");

        h.decreaseKey(2, 1);
        System.out.print(h.getMin());
    }
}
