class ThreeWayQuickSort<T extends Comparable<T>> implements Sorter<T> {
    private static final int INSERTION_SORT_THRESHOLD = 10;
    private final PivotSelector<T> pivotSelector;

    public ThreeWayQuickSort() {
        this(new MedianOfThreePivotSelector<>());
    }

    public ThreeWayQuickSort(PivotSelector<T> pivotSelector) {
        this.pivotSelector = pivotSelector;
    }

    @Override
    public void sort(T[] array) {
        if (array.length > 0) {
            quickSort(array, 0, array.length - 1);
        }
    }

    private void quickSort(T[] array, int low, int high) {
        if (high - low < INSERTION_SORT_THRESHOLD) {
            insertionSort(array, low, high);
            return;
        }

        if (low < high) {
            int[] partitionIndices = threeWayPartition(array, low, high);
            quickSort(array, low, partitionIndices[0] - 1);
            quickSort(array, partitionIndices[1] + 1, high);
        }
    }

    private int[] threeWayPartition(T[] array, int low, int high) {
        int pivotIndex = pivotSelector.selectPivot(array, low, high);
        T pivot = array[pivotIndex];

        int lt = low;
        int gt = high;
        int i = low;

        while (i <= gt) {
            int cmp = array[i].compareTo(pivot);
            if (cmp < 0) {
                swap(array, lt, i);
                lt++;
                i++;
            } else if (cmp > 0) {
                swap(array, i, gt);
                gt--;
            } else {
                i++;
            }
        }

        return new int[]{lt, gt};
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
}
