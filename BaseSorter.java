import java.util.Comparator;

public abstract class BaseSorter<T> implements Sorter<T> {
    public final SortConfiguration<T> config;
    protected SortStatistics statistics;

    protected BaseSorter(SortConfiguration<T> config) {
        this.config = config;
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

        if (config.isValidateInput()) {
            SortValidator.validateBeforeSort(array);
        }

        config.getLogger().log("Starting " + getAlgorithmName() + " sort of " + array.length + " elements");

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

        config.getLogger().log(getAlgorithmName() + " sort completed successfully");
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
}
