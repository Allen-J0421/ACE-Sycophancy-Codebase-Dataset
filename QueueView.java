interface QueueView<T> {
    void enqueue(T value);

    T dequeue();

    T peekFront();

    T peekRear();
}
