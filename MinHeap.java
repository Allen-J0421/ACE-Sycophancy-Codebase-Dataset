public final class MinHeap implements IntHeap {
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

    @Override
    public boolean offer(int value) {
        return heap.offer(value);
    }

    @Deprecated(forRemoval = false)
    public boolean insertKey(int value) {
        return offer(value);
    }

    @Override
    public int peek() {
        return heap.peek();
    }

    @Deprecated(forRemoval = false)
    public int getMin() {
        return peek();
    }

    @Override
    public int removeMin() {
        return heap.removeMin();
    }

    @Deprecated(forRemoval = false)
    public int extractMin() {
        return heap.extractMin();
    }

    @Override
    public int removeAt(int index) {
        return heap.removeAt(index);
    }

    @Deprecated(forRemoval = false)
    public void deleteKey(int index) {
        heap.removeAt(index);
    }

    @Override
    public void updateValue(int index, int newValue) {
        heap.updateValue(index, newValue);
    }

    @Deprecated(forRemoval = false)
    public void decreaseKey(int index, int newValue) {
        heap.decreaseKey(index, newValue);
    }

    @Deprecated(forRemoval = false)
    public void increaseKey(int index, int newValue) {
        heap.increaseKey(index, newValue);
    }

    @Deprecated(forRemoval = false)
    public void changeValueOnAKey(int index, int newValue) {
        updateValue(index, newValue);
    }

    @Override
    public void clear() {
        heap.clear();
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    @Deprecated(forRemoval = false)
    public boolean isFull() {
        return heap.isAtCapacity();
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public int capacity() {
        return heap.capacity();
    }

    @Override
    public int valueAt(int index) {
        return heap.valueAt(index);
    }

    @Override
    public int[] toArray() {
        return heap.toArray();
    }

    @Override
    public void addAll(int... values) {
        heap.addAll(values);
    }
}
