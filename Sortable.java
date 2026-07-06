import java.util.List;

interface Sortable<T> {
    int size();
    T get(int index);
    void set(int index, T value);

    static <T> Sortable<T> of(T[] arr)      { return new ArraySortable<>(arr); }
    static <T> Sortable<T> of(List<T> list) { return new ListSortable<>(list); }
}

class ArraySortable<T> implements Sortable<T> {
    private final T[] arr;
    ArraySortable(T[] arr) { this.arr = arr; }
    public int size()           { return arr.length; }
    public T get(int i)         { return arr[i]; }
    public void set(int i, T v) { arr[i] = v; }
}

class ListSortable<T> implements Sortable<T> {
    private final List<T> list;
    ListSortable(List<T> list) { this.list = list; }
    public int size()           { return list.size(); }
    public T get(int i)         { return list.get(i); }
    public void set(int i, T v) { list.set(i, v); }
}
