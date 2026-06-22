public class CircularQueue {

    private static final int EMPTY_VALUE = -1;
    private static final String FULL_MESSAGE = "Queue is full!";
    private static final String EMPTY_MESSAGE = "Queue is empty!";

    private final int[] elements;
    private int front;
    private int size;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive");
        }

        elements = new int[capacity];
        front = 0;
        size = 0;
    }

    public void enqueue(int value) {
        if (!offer(value)) {
            System.out.println(FULL_MESSAGE);
        }
    }

    public int dequeue() {
        Integer value = poll();
        if (value == null) {
            System.out.println(EMPTY_MESSAGE);
            return EMPTY_VALUE;
        }

        return value;
    }

    public boolean offer(int value) {
        if (isFull()) {
            return false;
        }

        elements[nextRearIndex()] = value;
        size++;
        return true;
    }

    public Integer poll() {
        if (isEmpty()) {
            return null;
        }

        int value = elements[front];
        front = indexFromFront(1);
        size--;
        return value;
    }

    public int getFront() {
        return peekFront();
    }

    public int getRear() {
        return peekRear();
    }

    public int peekFront() {
        return valueOrEmpty(peek());
    }

    public int peekRear() {
        return valueOrEmpty(peekLast());
    }

    public Integer peek() {
        if (isEmpty()) {
            return null;
        }

        return elements[front];
    }

    public Integer peekLast() {
        if (isEmpty()) {
            return null;
        }

        return elements[rearIndex()];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity();
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    private int nextRearIndex() {
        return indexFromFront(size);
    }

    private int rearIndex() {
        return indexFromFront(size - 1);
    }

    private int indexFromFront(int offset) {
        return (front + offset) % capacity();
    }

    private int valueOrEmpty(Integer value) {
        if (value == null) {
            return EMPTY_VALUE;
        }

        return value;
    }
}
