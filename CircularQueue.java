import java.util.Arrays;

public final class CircularQueue<T> {

    private final Object[] elements;
    private int head;
    private int tail;
    private int size;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        elements = new Object[capacity];
        head = 0;
        tail = 0;
        size = 0;
    }

    public void enqueue(T value) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full.");
        }

        elements[tail] = value;
        tail = advance(tail);
        size++;
    }

    public T dequeue() {
        ensureNotEmpty();

        T removedValue = elementAt(head);
        elements[head] = null;
        head = advance(head);
        size--;
        return removedValue;
    }

    public T peekFront() {
        ensureNotEmpty();
        return elementAt(head);
    }

    public T peekRear() {
        ensureNotEmpty();
        return elementAt(previousIndex(tail));
    }

    public T getFront() {
        return peekFront();
    }

    public T getRear() {
        return peekRear();
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

    public void clear() {
        Arrays.fill(elements, null);
        head = 0;
        tail = 0;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');

        for (int offset = 0; offset < size; offset++) {
            if (offset > 0) {
                builder.append(", ");
            }

            builder.append(elementAt((head + offset) % elements.length));
        }

        builder.append(']');
        return builder.toString();
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
    }

    private int advance(int index) {
        return (index + 1) % elements.length;
    }

    private int previousIndex(int index) {
        return (index - 1 + elements.length) % elements.length;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }

    public static void main(String[] args) {
        CircularQueue<Integer> queue = new CircularQueue<>(5);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println(queue.peekFront() + " " + queue.peekRear());

        queue.dequeue();
        System.out.println(queue.peekFront() + " " + queue.peekRear());

        queue.enqueue(40);
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        System.out.println(queue);
    }
}
