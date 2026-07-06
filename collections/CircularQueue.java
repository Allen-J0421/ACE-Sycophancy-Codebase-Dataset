package collections;

public class CircularQueue<T> implements Queue<T> {

    private Object[] arr;

    private int front;

    private int size;

    private int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        arr = new Object[capacity];
        front = 0;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int rearIndex() {
        return (front + size - 1) % capacity;
    }

    private void requireNonFull() {
        if (size == capacity) throw new IllegalStateException("Queue is full");
    }

    private void requireNonEmpty() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
    }

    public void enqueue(T value) {
        requireNonFull();
        arr[(rearIndex() + 1) % capacity] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        requireNonEmpty();
        T value = (T) arr[front];
        arr[front] = null;
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T getFront() {
        requireNonEmpty();
        return (T) arr[front];
    }

    @SuppressWarnings("unchecked")
    public T getRear() {
        requireNonEmpty();
        return (T) arr[rearIndex()];
    }
}
