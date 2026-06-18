import java.util.Comparator;

@Deprecated(since = "2.0", forRemoval = true)
public class MergeSort<T extends Comparable<T>> implements Sorter<T> {
    private final SortConfiguration<T> config;
    private SortStatistics statistics;

    public MergeSort() {
        this(SortConfiguration.<T>builder()
                .withComparator(Comparable::compareTo)
                .build());
    }

    public MergeSort(Comparator<T> comparator) {
        this(SortConfiguration.<T>builder()
                .withComparator(comparator)
                .build());
    }

    public MergeSort(SortConfiguration<T> config) {
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

        config.getLogger().log("Starting sort of " + array.length + " elements");

        if (config.isTrackStatistics()) {
            statistics = new SortStatistics();
        }

        mergeSort(array, 0, array.length - 1, comparator);

        if (config.isTrackStatistics()) {
            statistics.end();
        }

        if (config.isValidateInput()) {
            SortValidator.validateAfterSort(array, comparator);
        }

        config.getLogger().log("Sort completed successfully");
    }

    @Override
    public SortStatistics getStatistics() {
        return statistics;
    }

    private void mergeSort(T[] array, int left, int right, Comparator<T> comparator) {
        if (right - left < config.getInsertionSortThreshold()) {
            insertionSort(array, left, right, comparator);
            return;
        }

        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid, comparator);
            mergeSort(array, mid + 1, right, comparator);
            merge(array, left, mid, right, comparator);
        }
    }

    private void insertionSort(T[] array, int left, int right, Comparator<T> comparator) {
        for (int i = left + 1; i <= right; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= left && comparator.compare(array[j], key) > 0) {
                recordComparison();
                array[j + 1] = array[j];
                recordSwap();
                j--;
            }
            array[j + 1] = key;
        }
    }

    private void merge(T[] array, int left, int mid, int right, Comparator<T> comparator) {
        T[] leftArray = copyRange(array, left, mid);
        T[] rightArray = copyRange(array, mid + 1, right);

        int leftIdx = 0, rightIdx = 0, currentIdx = left;

        while (leftIdx < leftArray.length && rightIdx < rightArray.length) {
            recordComparison();
            if (comparator.compare(leftArray[leftIdx], rightArray[rightIdx]) <= 0) {
                array[currentIdx++] = leftArray[leftIdx++];
            } else {
                array[currentIdx++] = rightArray[rightIdx++];
            }
            recordSwap();
        }

        while (leftIdx < leftArray.length) {
            array[currentIdx++] = leftArray[leftIdx++];
            recordSwap();
        }

        while (rightIdx < rightArray.length) {
            array[currentIdx++] = rightArray[rightIdx++];
            recordSwap();
        }
    }

    private void recordComparison() {
        if (config.isTrackStatistics() && statistics != null) {
            statistics.recordComparison();
        }
    }

    private void recordSwap() {
        if (config.isTrackStatistics() && statistics != null) {
            statistics.recordSwap();
        }
    }

    @SuppressWarnings("unchecked")
    private T[] copyRange(T[] array, int from, int to) {
        int length = to - from + 1;
        T[] result = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), length);
        System.arraycopy(array, from, result, 0, length);
        return result;
    }
}
