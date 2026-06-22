package queue;

import java.util.Iterator;
import java.util.Objects;

public final class CircularQueue<T> extends AbstractBoundedQueue<T> {

    private final RingBuffer<T> buffer;

    public CircularQueue(int capacity) {
        buffer = new RingBuffer<>(capacity);
    }

    @Override
    public boolean offer(T value) {
        Objects.requireNonNull(value, "Queue elements must be non-null.");

        if (isFull()) {
            return false;
        }

        buffer.addLast(value);
        return true;
    }

    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }

        return buffer.removeFirst();
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return buffer.first();
    }

    @Override
    protected T peekLast() {
        return buffer.last();
    }

    @Override
    public int size() {
        return buffer.size();
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public void clear() {
        buffer.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return buffer.iterator();
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
