import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.AbstractQueue;

public final class CircularQueue<T> extends AbstractQueue<T> {

    private final Object[] elements;
    private int head;
    private int tail;
    private int size;
    private int modificationCount;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        elements = new Object[capacity];
        head = 0;
        tail = 0;
        size = 0;
        modificationCount = 0;
    }

    public void enqueue(T value) {
        if (!offer(value)) {
            throw new IllegalStateException("Queue is full.");
        }
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
        return poll();
    }

    public T peekFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
        return peek();
    }

    public T peekRear() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
        return elementAt(indexFromHead(size - 1));
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

    @Override
    public boolean offer(T value) {
        Objects.requireNonNull(value, "Queue elements must be non-null.");

        if (isFull()) {
            return false;
        }

        elements[tail] = value;
        tail = advance(tail);
        size++;
        modificationCount++;
        return true;
    }

    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }

        T removedValue = elementAt(head);
        elements[head] = null;
        head = advance(head);
        size--;
        modificationCount++;
        return removedValue;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return elementAt(head);
    }

    @Override
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
        modificationCount++;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final int expectedModificationCount = modificationCount;
            private int offset;

            @Override
            public boolean hasNext() {
                ensureUnmodified();
                return offset < size;
            }

            @Override
            public T next() {
                ensureUnmodified();
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T value = elementAt(indexFromHead(offset));
                offset++;
                return value;
            }

            private void ensureUnmodified() {
                if (expectedModificationCount != modificationCount) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');

        for (int offset = 0; offset < size; offset++) {
            if (offset > 0) {
                builder.append(", ");
            }

            builder.append(elementAt(indexFromHead(offset)));
        }

        builder.append(']');
        return builder.toString();
    }

    private int advance(int index) {
        return (index + 1) % elements.length;
    }

    private int indexFromHead(int offset) {
        return (head + offset) % elements.length;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }
}
