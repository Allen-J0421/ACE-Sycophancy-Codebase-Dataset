import java.util.List;

interface Heapify<T> {
    void siftUp(List<T> heap, int index);
    void siftDown(List<T> heap, int index);
    // Restores the heap invariant at index without the caller needing to know which direction.
    void fix(List<T> heap, int index);
}
