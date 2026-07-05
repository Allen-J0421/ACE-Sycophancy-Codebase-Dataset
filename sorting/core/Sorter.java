package sorting.core;

public class Sorter<T> {
    private final SortContext<T> context;

    public Sorter(SortContext<T> context) {
        this.context = context;
    }

    public void sort(T[] arr) {
        context.sort(arr);
    }
}
