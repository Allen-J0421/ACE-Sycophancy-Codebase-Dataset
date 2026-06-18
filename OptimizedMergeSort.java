import java.util.Comparator;

public class OptimizedMergeSort<T extends Comparable<T>> extends BaseSorter<T> {
    private final SortStrategy<T> baseCaseStrategy;

    public OptimizedMergeSort(SortConfiguration<T> config, SortStrategy<T> strategy) {
        super(config);
        this.baseCaseStrategy = strategy;
    }

    public OptimizedMergeSort(SortConfiguration<T> config) {
        this(config, new InsertionSortStrategy<>(config.getComparator(), null));
    }

    @Override
    protected void doSort(T[] array, Comparator<T> comparator) {
        mergeSort(array, 0, array.length - 1, comparator);
    }

    @Override
    protected String getAlgorithmName() {
        return "Optimized Merge Sort";
    }

    private void mergeSort(T[] array, int left, int right, Comparator<T> comparator) {
        if (right - left < config.getInsertionSortThreshold()) {
            baseCaseStrategy.sort(array, left, right, comparator);
            return;
        }

        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid, comparator);
            mergeSort(array, mid + 1, right, comparator);
            merge(array, left, mid, right, comparator);
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
}
