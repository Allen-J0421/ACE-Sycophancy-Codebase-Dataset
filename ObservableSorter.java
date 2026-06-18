import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ObservableSorter<T extends Comparable<T>> extends BaseSorter<T> {
    private final Sorter<T> delegate;
    private final List<SortListener> listeners;

    public ObservableSorter(Sorter<T> delegate) {
        super(delegate instanceof BaseSorter ? ((BaseSorter<T>) delegate).config :
              SortConfiguration.<T>builder().withComparator(Comparable::compareTo).build());
        this.delegate = delegate;
        this.listeners = new ArrayList<>();
    }

    public void addListener(SortListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SortListener listener) {
        listeners.remove(listener);
    }

    @Override
    protected void doSort(T[] array, Comparator<T> comparator) {
        try {
            notifyListeners(SortEvent.Type.SORT_STARTED, "Sort operation started");
            delegate.sort(array, comparator);
            notifyListeners(SortEvent.Type.SORT_COMPLETED, "Sort operation completed successfully");
        } catch (Exception e) {
            notifyListeners(SortEvent.Type.SORT_FAILED, "Sort operation failed: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    protected String getAlgorithmName() {
        return "Observable " + (delegate instanceof BaseSorter ?
               ((BaseSorter<T>) delegate).getAlgorithmName() : "Sorter");
    }

    private void notifyListeners(SortEvent.Type type, String message) {
        for (SortListener listener : listeners) {
            listener.onSortEvent(new SortEvent(type, message));
        }
    }

    private void notifyListeners(SortEvent.Type type, String message, Exception exception) {
        for (SortListener listener : listeners) {
            listener.onSortEvent(new SortEvent(type, message, exception));
        }
    }


    @Override
    public SortStatistics getStatistics() {
        return delegate.getStatistics();
    }
}
