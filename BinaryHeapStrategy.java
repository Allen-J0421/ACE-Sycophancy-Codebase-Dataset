import java.util.*;

class BinaryHeapStrategy<T> implements Heapify<T> {

    private final Comparator<T> comparator;

    BinaryHeapStrategy(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void siftUp(List<T> heap, int index) {
        while (index != 0 && comparator.compare(heap.get(index), heap.get(parent(index))) < 0) {
            Collections.swap(heap, index, parent(index));
            index = parent(index);
        }
    }

    @Override
    public void siftDown(List<T> heap, int index) {
        int size = heap.size();
        int l = 2 * index + 1;
        int r = 2 * index + 2;
        int smallest = index;

        if (l < size && comparator.compare(heap.get(l), heap.get(smallest)) < 0) {
            smallest = l;
        }
        if (r < size && comparator.compare(heap.get(r), heap.get(smallest)) < 0) {
            smallest = r;
        }

        if (smallest != index) {
            Collections.swap(heap, index, smallest);
            siftDown(heap, smallest);
        }
    }

    @Override
    public void fix(List<T> heap, int index) {
        if (index > 0 && comparator.compare(heap.get(index), heap.get(parent(index))) < 0) {
            siftUp(heap, index);
        } else {
            siftDown(heap, index);
        }
    }

    private static int parent(int index) {
        return (index - 1) / 2;
    }
}
