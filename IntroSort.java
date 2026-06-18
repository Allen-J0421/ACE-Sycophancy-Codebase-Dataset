class IntroSort<T extends Comparable<T>> implements Sorter<T> {
    private static final int INSERTION_SORT_THRESHOLD = 10;
    private final PivotSelector<T> pivotSelector;
    private final HeapSort<T> heapSort = new HeapSort<>();

    public IntroSort() {
        this(new MedianOfThreePivotSelector<>());
    }

    public IntroSort(PivotSelector<T> pivotSelector) {
        this.pivotSelector = pivotSelector;
    }

    @Override
    public void sort(T[] array) {
        if (array.length > 0) {
            int maxDepth = 2 * log2(array.length);
            introSort(array, 0, array.length - 1, maxDepth);
        }
    }

    private void introSort(T[] array, int low, int high, int depthLimit) {
        if (high - low < INSERTION_SORT_THRESHOLD) {
            insertionSort(array, low, high);
            return;
        }

        if (depthLimit == 0) {
            heapSort.heapSort(array, low, high);
            return;
        }

        if (low < high) {
            int partitionIndex = partition(array, low, high);
            introSort(array, low, partitionIndex - 1, depthLimit - 1);
            introSort(array, partitionIndex + 1, high, depthLimit - 1);
        }
    }

    private int partition(T[] array, int low, int high) {
        int pivotIndex = pivotSelector.selectPivot(array, low, high);
        T pivot = array[pivotIndex];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) < 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    private void insertionSort(T[] array, int low, int high) {
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

    private void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private int log2(int n) {
        return (n <= 1) ? 0 : 1 + log2(n >> 1);
    }
}
