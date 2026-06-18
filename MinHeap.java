public final class MinHeap implements LegacyMinHeapApi {
    private final IntBinaryHeap heap;

    public MinHeap() {
        this(new IntBinaryHeap());
    }

    public MinHeap(int initialCapacity) {
        this(new IntBinaryHeap(initialCapacity));
    }

    public MinHeap(int[] values) {
        this(new IntBinaryHeap(values));
    }

    public static MinHeap from(int... values) {
        return new MinHeap(values);
    }

    private MinHeap(IntBinaryHeap heap) {
        this.heap = heap;
    }

    @Override
    public boolean offer(int value) {
        return heap.offer(value);
    }

    @Override
    public int peek() {
        return heap.peek();
    }

    @Override
    public int removeMin() {
        return heap.removeMin();
    }

    @Override
    public int removeAt(int index) {
        return heap.removeAt(index);
    }

    @Override
    public void updateValue(int index, int newValue) {
        heap.updateValue(index, newValue);
    }

    @Override
    public void clear() {
        heap.clear();
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
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
