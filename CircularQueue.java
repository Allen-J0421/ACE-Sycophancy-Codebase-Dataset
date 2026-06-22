public final class CircularQueue {

    private final int[] elements;
    private int front;
    private int size;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        elements = new int[capacity];
        front = 0;
        size = 0;
    }

    public void enqueue(int value) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full.");
        }

        elements[nextInsertIndex()] = value;
        size++;
    }

    public int dequeue() {
        ensureNotEmpty();

        int removedValue = elements[front];
        front = advance(front);
        size--;
        return removedValue;
    }

    public int getFront() {
        ensureNotEmpty();
        return elements[front];
    }

    public int getRear() {
        ensureNotEmpty();
        return elements[lastElementIndex()];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == elements.length;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
    }

    private int nextInsertIndex() {
        return (front + size) % elements.length;
    }

    private int lastElementIndex() {
        return (front + size - 1 + elements.length) % elements.length;
    }

    private int advance(int index) {
        return (index + 1) % elements.length;
    }

    public static void main(String[] args) {
        CircularQueue queue = new CircularQueue(5);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println(queue.getFront() + " " + queue.getRear());

        queue.dequeue();
        System.out.println(queue.getFront() + " " + queue.getRear());

        queue.enqueue(40);
        System.out.println(queue.getFront() + " " + queue.getRear());
    }
}
