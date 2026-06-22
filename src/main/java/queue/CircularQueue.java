package queue;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class CircularQueue<T> extends AbstractBoundedQueue<T> {

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
        resetState();
    }

    @Override
    public boolean offer(T value) {
        Objects.requireNonNull(value, "Queue elements must be non-null.");

        if (isFull()) {
            return false;
        }

        insertAtTail(value);
        recordMutation();
        return true;
    }

    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }

        T removedValue = removeHead();
        recordMutation();
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
    protected T peekLast() {
        return elementAt(indexFromHead(size - 1));
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, null);
        resetState();
        recordMutation();
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
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

    private void resetState() {
        head = 0;
        tail = 0;
        size = 0;
    }

    private void recordMutation() {
        modificationCount++;
    }

    private void insertAtTail(T value) {
        elements[tail] = value;
        tail = advance(tail);
        size++;
    }

    private T removeHead() {
        T removedValue = elementAt(head);
        elements[head] = null;
        head = advance(head);
        size--;
        return removedValue;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }

    private final class QueueIterator implements Iterator<T> {
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
            if (offset >= size) {
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
    }
}
