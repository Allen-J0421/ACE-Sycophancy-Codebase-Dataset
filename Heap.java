interface Heap<T extends Comparable<T>> extends Iterable<T> {
    Heap<T> insert(T key);
    Heap<T> delete(int index);
    Heap<T> decrease(int index, T new_val);
    Heap<T> increase(int index, T new_val);
    Heap<T> changeValue(int index, T new_val);
    T peek();
    T poll();
}
