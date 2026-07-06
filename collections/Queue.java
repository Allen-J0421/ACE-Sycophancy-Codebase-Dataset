package collections;

public interface Queue<T> {
    void enqueue(T value);
    T dequeue();
    T getFront();
    T getRear();
    int size();
    boolean isEmpty();
}
