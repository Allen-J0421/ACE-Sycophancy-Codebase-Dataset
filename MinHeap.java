public class MinHeap {
    private final IntBinaryHeap heap;

    public MinHeap() {
        this.heap = new IntBinaryHeap();
    }

    public MinHeap(int initialCapacity) {
        this.heap = new IntBinaryHeap(initialCapacity);
    }

    public MinHeap(int[] values) {
        this.heap = new IntBinaryHeap(values);
    }

    public boolean offer(int value) {
        return heap.offer(value);
    }

    public boolean insertKey(int value) {
        return offer(value);
    }

    public int peek() {
        return heap.peek();
    }

    public int getMin() {
        return peek();
    }

    public int removeMin() {
        return heap.removeMin();
    }

    public int extractMin() {
        return heap.extractMin();
    }

    public int removeAt(int index) {
        return heap.removeAt(index);
    }

    public void deleteKey(int index) {
        heap.removeAt(index);
    }

    public void updateValue(int index, int newValue) {
        heap.updateValue(index, newValue);
    }

    public void decreaseKey(int index, int newValue) {
        heap.decreaseKey(index, newValue);
    }

    public void increaseKey(int index, int newValue) {
        heap.increaseKey(index, newValue);
    }

    public void changeValueOnAKey(int index, int newValue) {
        updateValue(index, newValue);
    }

    public void clear() {
        heap.clear();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public boolean isFull() {
        return heap.isFull();
    }

    public int size() {
        return heap.size();
    }

    public int capacity() {
        return heap.capacity();
    }

    public int valueAt(int index) {
        return heap.valueAt(index);
    }

    public int[] toArray() {
        return heap.toArray();
    }
}
