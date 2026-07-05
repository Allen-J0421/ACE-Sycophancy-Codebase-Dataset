package sorting.core;

import java.util.Comparator;

public class CountingComparator<T> implements Comparator<T> {
    private final Comparator<T> delegate;
    private long count;

    public CountingComparator(Comparator<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(T a, T b) {
        count++;
        return delegate.compare(a, b);
    }

    public long getCount() { return count; }
}
