public interface IntHeap {
    boolean offer(int value);

    int peek();

    int removeMin();

    int removeAt(int index);

    void updateValue(int index, int newValue);

    void clear();

    boolean isEmpty();

    int size();

    int capacity();

    int valueAt(int index);

    int[] toArray();

    void addAll(int... values);
}
