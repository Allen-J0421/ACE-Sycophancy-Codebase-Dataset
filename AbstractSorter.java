abstract class AbstractSorter<T extends Comparable<T>> implements Sorter<T> {
    protected SortingMetrics metrics = new SortingMetrics();
    protected static final int DEFAULT_INSERTION_THRESHOLD = 10;
    protected int insertionThreshold = DEFAULT_INSERTION_THRESHOLD;

    @Override
    public void sort(T[] array) {
        sortInternal(array);
    }

    public void sort(T[] array, int insertionThreshold) {
        this.insertionThreshold = insertionThreshold;
        sortInternal(array);
    }

    protected abstract void sortInternal(T[] array);

    protected void insertionSort(T[] array, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= low && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    protected void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public SortingMetrics getMetrics() {
        return metrics;
    }

    public void resetMetrics() {
        metrics = new SortingMetrics();
    }
}
