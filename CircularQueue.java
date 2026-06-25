import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class CircularQueue<T> extends AbstractQueue<T> implements QueueView<T> {
    private static final String EMPTY_QUEUE_MESSAGE = "Queue is empty.";
    private static final String FULL_QUEUE_MESSAGE = "Queue is full.";
    private static final String NON_NULL_MESSAGE = "Queue elements must be non-null.";

    private final Object[] elements;
    private int head;
    private int size;
    private int modificationCount;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.elements = new Object[capacity];
    }

    public void enqueue(T element) {
        if (!offer(element)) {
            throw new IllegalStateException(FULL_QUEUE_MESSAGE);
        }
    }

    public T dequeue() {
        requireNotEmpty();
        T removed = removeHead();
        modificationCount++;
        return removed;
    }

    public T peekFront() {
        requireNotEmpty();
        return elementAt(head);
    }

    public T peekRear() {
        requireNotEmpty();
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
    public boolean offer(T element) {
        Objects.requireNonNull(element, NON_NULL_MESSAGE);
        if (isFull()) {
            return false;
        }
        insertAtTail(element);
        modificationCount++;
        return true;
    }

    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        T removed = removeHead();
        modificationCount++;
        return removed;
    }

    @Override
    public T peek() {
        return isEmpty() ? null : elementAt(head);
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    public int remainingCapacity() {
        return capacity() - size;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, null);
        head = 0;
        size = 0;
        modificationCount++;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int offset;
            private final int expectedModificationCount = modificationCount;

            @Override
            public boolean hasNext() {
                checkForComodification();
                return offset < size;
            }

            @Override
            public T next() {
                checkForComodification();
                if (offset >= size) {
                    throw new NoSuchElementException();
                }
                T value = elementAt(indexFromHead(offset));
                offset++;
                return value;
            }

            private void checkForComodification() {
                if (expectedModificationCount != modificationCount) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(elementAt(indexFromHead(i)));
        }
        return builder.append(']').toString();
    }

    @Override
    public String snapshot() {
        return toString();
    }

    private int advance(int index) {
        return (index + 1) % elements.length;
    }

    private int tailIndex() {
        return (head + size) % elements.length;
    }

    private int indexFromHead(int offset) {
        return (head + offset) % elements.length;
    }

    private void requireNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException(EMPTY_QUEUE_MESSAGE);
        }
    }

    private void insertAtTail(T element) {
        elements[tailIndex()] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    private T removeHead() {
        T removed = (T) elements[head];
        elements[head] = null;
        head = advance(head);
        size--;
        return removed;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }
}
