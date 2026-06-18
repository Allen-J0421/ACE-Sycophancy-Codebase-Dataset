interface Sorter<T extends Comparable<T>> {
    void sort(T[] array);
}

interface PivotSelector<T extends Comparable<T>> {
    int selectPivot(T[] array, int low, int high);
}

class MedianOfThreePivotSelector<T extends Comparable<T>> implements PivotSelector<T> {
    @Override
    public int selectPivot(T[] array, int low, int high) {
        int mid = low + (high - low) / 2;
        if (array[low].compareTo(array[mid]) > 0) swap(array, low, mid);
        if (array[low].compareTo(array[high]) > 0) swap(array, low, high);
        if (array[mid].compareTo(array[high]) > 0) swap(array, mid, high);
        swap(array, mid, high);
        return high;
    }

    private void swap(Object[] array, int i, int j) {
        Object temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

class HybridQuickSort<T extends Comparable<T>> implements Sorter<T> {
    private static final int INSERTION_SORT_THRESHOLD = 10;
    private final PivotSelector<T> pivotSelector;

    public HybridQuickSort() {
        this(new MedianOfThreePivotSelector<>());
    }

    public HybridQuickSort(PivotSelector<T> pivotSelector) {
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
            int partitionIndex = partition(array, low, high);
            quickSort(array, low, partitionIndex - 1);
            quickSort(array, partitionIndex + 1, high);
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
}

class QuickSort {
    public static void main(String[] args) {
        Integer[] numbers = {10, 7, 8, 9, 1, 5};

        Sorter<Integer> sorter = new HybridQuickSort<>();
        sorter.sort(numbers);

        System.out.println("Sorted array: " + java.util.Arrays.toString(numbers));
    }
}
