import java.util.Comparator;

public interface SortStrategy<T> {
    void sort(T[] array, int left, int right, Comparator<T> comparator);
    String getName();
}

class InsertionSortStrategy<T> implements SortStrategy<T> {
    private final Comparator<T> comparator;
    private final SortStatistics statistics;

    public InsertionSortStrategy(Comparator<T> comparator, SortStatistics statistics) {
        this.comparator = comparator;
        this.statistics = statistics;
    }

    @Override
    public void sort(T[] array, int left, int right, Comparator<T> comparator) {
        for (int i = left + 1; i <= right; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= left && comparator.compare(array[j], key) > 0) {
                if (statistics != null) statistics.recordComparison();
                array[j + 1] = array[j];
                if (statistics != null) statistics.recordSwap();
                j--;
            }
            array[j + 1] = key;
        }
    }

    @Override
    public String getName() {
        return "Insertion Sort";
    }
}

class HeapSortStrategy<T> implements SortStrategy<T> {
    private final SortStatistics statistics;

    public HeapSortStrategy(SortStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public void sort(T[] array, int left, int right, Comparator<T> comparator) {
        int length = right - left + 1;
        for (int i = length / 2 - 1; i >= 0; i--) {
            heapify(array, left, length, i, comparator);
        }

        for (int i = length - 1; i >= 0; i--) {
            T temp = array[left];
            array[left] = array[left + i];
            array[left + i] = temp;
            if (statistics != null) statistics.recordSwap();

            heapify(array, left, i, 0, comparator);
        }
    }

    private void heapify(T[] array, int left, int length, int i, Comparator<T> comparator) {
        int largest = i;
        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;

        if (leftChild < length && comparator.compare(array[left + leftChild], array[left + largest]) > 0) {
            if (statistics != null) statistics.recordComparison();
            largest = leftChild;
        }

        if (rightChild < length && comparator.compare(array[left + rightChild], array[left + largest]) > 0) {
            if (statistics != null) statistics.recordComparison();
            largest = rightChild;
        }

        if (largest != i) {
            T temp = array[left + i];
            array[left + i] = array[left + largest];
            array[left + largest] = temp;
            if (statistics != null) statistics.recordSwap();

            heapify(array, left, length, largest, comparator);
        }
    }

    @Override
    public String getName() {
        return "Heap Sort";
    }
}
