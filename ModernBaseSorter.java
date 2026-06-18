import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ModernBaseSorter<T extends Comparable<T>> implements Sorter<T> {
    protected final SortConfiguration<T> config;
    protected final List<ModernSortListener> listeners;
    protected SortStatistics statistics;
    private long startTime;

    protected ModernBaseSorter(SortConfiguration<T> config) {
        this.config = config;
        this.listeners = new ArrayList<>();
    }

    public ModernBaseSorter<T> addListener(ModernSortListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public void sort(T[] array) {
        sort(array, config.getComparator());
    }

    @Override
    public void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0) {
            return;
        }

        startTime = System.nanoTime();

        try {
            notifyStarted("Starting " + getAlgorithmName());

            if (config.isValidateInput()) {
                SortValidator.validateBeforeSort(array);
            }

            if (config.isTrackStatistics()) {
                statistics = new SortStatistics();
            }

            doSort(array, comparator);

            if (config.isTrackStatistics()) {
                statistics.end();
            }

            if (config.isValidateInput()) {
                SortValidator.validateAfterSort(array, comparator);
            }

            notifyCompleted();

        } catch (Exception e) {
            notifyFailed(e);
            throw e;
        }
    }

    @Override
    public SortStatistics getStatistics() {
        return statistics;
    }

    protected abstract void doSort(T[] array, Comparator<T> comparator);

    protected abstract String getAlgorithmName();

    protected void recordComparison() {
        if (config.isTrackStatistics() && statistics != null) {
            statistics.recordComparison();
        }
    }

    protected void recordSwap() {
        if (config.isTrackStatistics() && statistics != null) {
            statistics.recordSwap();
        }
    }

    @SuppressWarnings("unchecked")
    protected T[] copyRange(T[] array, int from, int to) {
        int length = to - from + 1;
        T[] result = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), length);
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    private void notifyStarted(String message) {
        for (ModernSortListener listener : listeners) {
            listener.onStarted(message);
        }
    }

    private void notifyCompleted() {
        long duration = System.nanoTime() - startTime;
        SortResult result = new SortResult(
                duration,
                statistics != null ? statistics.getComparisons() : 0,
                statistics != null ? statistics.getSwaps() : 0,
                true,
                getAlgorithmName() + " completed successfully");

        for (ModernSortListener listener : listeners) {
            listener.onCompleted(getAlgorithmName() + " sort completed", result);
        }
    }

    private void notifyFailed(Exception exception) {
        for (ModernSortListener listener : listeners) {
            listener.onFailed("Sort operation failed", exception);
        }
    }
}
