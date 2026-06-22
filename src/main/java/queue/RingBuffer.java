package queue;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class RingBuffer<T> implements Iterable<T> {

    private final Object[] elements;
    private int head;
    private int tail;
    private int size;
    private int modificationCount;

    RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }

        elements = new Object[capacity];
        head = 0;
        tail = 0;
        size = 0;
        modificationCount = 0;
    }

    boolean isEmpty() {
        return size == 0;
    }

    boolean isFull() {
        return size == elements.length;
    }

    int size() {
        return size;
    }

    int capacity() {
        return elements.length;
    }

    void addLast(T value) {
        elements[tail] = value;
        tail = advance(tail);
        size++;
        modificationCount++;
    }

    T removeFirst() {
        T removedValue = elementAt(head);
        elements[head] = null;
        head = advance(head);
        size--;
        modificationCount++;
        return removedValue;
    }

    T first() {
        return elementAt(head);
    }

    T last() {
        return elementAt(indexFromHead(size - 1));
    }

    void clear() {
        Arrays.fill(elements, null);
        head = 0;
        tail = 0;
        size = 0;
        modificationCount++;
    }

    @Override
    public Iterator<T> iterator() {
        return new RingBufferIterator();
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

    private final class RingBufferIterator implements Iterator<T> {
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
