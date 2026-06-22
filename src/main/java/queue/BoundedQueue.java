package queue;

import java.util.Queue;

public interface BoundedQueue<T> extends Queue<T> {

    void enqueue(T value);

    T dequeue();

    T peekFront();

    T peekRear();

    int capacity();

    default int remainingCapacity() {
        return capacity() - size();
    }

    default boolean isFull() {
        return remainingCapacity() == 0;
    }
}
