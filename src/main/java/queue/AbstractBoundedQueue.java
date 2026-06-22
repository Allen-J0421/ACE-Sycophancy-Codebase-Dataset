package queue;

import java.util.AbstractQueue;

abstract class AbstractBoundedQueue<T> extends AbstractQueue<T> implements BoundedQueue<T> {

    private static final String EMPTY_QUEUE_MESSAGE = "Queue is empty.";
    private static final String FULL_QUEUE_MESSAGE = "Queue is full.";

    @Override
    public void enqueue(T value) {
        if (!offer(value)) {
            throw new IllegalStateException(FULL_QUEUE_MESSAGE);
        }
    }

    @Override
    public T dequeue() {
        requireNotEmpty();
        return poll();
    }

    @Override
    public T peekFront() {
        requireNotEmpty();
        return peek();
    }

    @Override
    public T peekRear() {
        requireNotEmpty();
        return peekLast();
    }

    public T getFront() {
        return peekFront();
    }

    public T getRear() {
        return peekRear();
    }

    protected final void requireNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException(EMPTY_QUEUE_MESSAGE);
        }
    }

    protected abstract T peekLast();
}
