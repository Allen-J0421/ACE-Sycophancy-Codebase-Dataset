package collections;

public class CircularQueue<T> implements Queue<T> {

    private Object[] arr;

    private int front;

    private int size;

    private int capacity;

    public CircularQueue(int initialCapacity) {
        this.capacity = initialCapacity;
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

    private void resize() {
        int newCapacity = capacity * 2;
        Object[] newArr = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newArr[i] = arr[(front + i) % capacity];
        }
        arr = newArr;
        front = 0;
        capacity = newCapacity;
    }

    private void requireNonEmpty() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
    }

    public void enqueue(T value) {
        if (size == capacity) resize();
        arr[(front + size) % capacity] = value;
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
