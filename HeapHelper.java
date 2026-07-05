import java.util.*;

class HeapHelper<T extends Comparable<T>> {

    private final ArrayList<T> heap;

    HeapHelper(ArrayList<T> heap) {
        this.heap = heap;
    }

    void siftUp(int index) {
        while (index != 0 && heap.get(index).compareTo(heap.get(parent(index))) < 0) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    void siftDown(int index) {
        int size = heap.size();
        int smallest = index;
        int l = left(index);
        int r = right(index);

        if (l < size && heap.get(l).compareTo(heap.get(smallest)) < 0) {
            smallest = l;
        }
        if (r < size && heap.get(r).compareTo(heap.get(smallest)) < 0) {
            smallest = r;
        }

        if (smallest != index) {
            swap(index, smallest);
            siftDown(smallest);
        }
    }

    void swap(int a, int b) {
        T temp = heap.get(a);
        heap.set(a, heap.get(b));
        heap.set(b, temp);
    }

    int parent(int index) {
        return (index - 1) / 2;
    }

    private int left(int index) {
        return 2 * index + 1;
    }

    private int right(int index) {
        return 2 * index + 2;
    }
}
