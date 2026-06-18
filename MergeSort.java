import java.util.Comparator;

public class MergeSort<T extends Comparable<T>> implements Sorter<T> {
    private static final int INSERTION_SORT_THRESHOLD = 10;

    private final Comparator<T> defaultComparator;
    private SortStatistics statistics;

    public MergeSort() {
        this.defaultComparator = Comparable::compareTo;
    }

    public MergeSort(Comparator<T> comparator) {
        this.defaultComparator = comparator;
    }

    @Override
    public void sort(T[] array) {
        sort(array, this.defaultComparator);
    }

    @Override
    public void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0) {
            return;
        }
        statistics = new SortStatistics();
        mergeSort(array, 0, array.length - 1, comparator);
        statistics.end();
    }

    @Override
    public SortStatistics getStatistics() {
        return statistics;
    }

    private void mergeSort(T[] array, int left, int right, Comparator<T> comparator) {
        if (right - left < INSERTION_SORT_THRESHOLD) {
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
                statistics.recordComparison();
                array[j + 1] = array[j];
                statistics.recordSwap();
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
            statistics.recordComparison();
            if (comparator.compare(leftArray[leftIdx], rightArray[rightIdx]) <= 0) {
                array[currentIdx++] = leftArray[leftIdx++];
            } else {
                array[currentIdx++] = rightArray[rightIdx++];
            }
            statistics.recordSwap();
        }

        while (leftIdx < leftArray.length) {
            array[currentIdx++] = leftArray[leftIdx++];
            statistics.recordSwap();
        }

        while (rightIdx < rightArray.length) {
            array[currentIdx++] = rightArray[rightIdx++];
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
