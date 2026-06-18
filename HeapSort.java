class HeapSort<T extends Comparable<T>> implements Sorter<T> {

    @Override
    public void sort(T[] array) {
        if (array.length > 0) {
            heapSort(array, 0, array.length - 1);
        }
    }

    void heapSort(T[] array, int low, int high) {
        int n = high - low + 1;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i, low);
        }

        for (int i = n - 1; i > 0; i--) {
            swap(array, low, low + i);
            heapify(array, i, 0, low);
        }
    }

    private void heapify(T[] array, int n, int i, int low) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && array[low + left].compareTo(array[low + largest]) > 0) {
            largest = left;
        }

        if (right < n && array[low + right].compareTo(array[low + largest]) > 0) {
            largest = right;
        }

        if (largest != i) {
            swap(array, low + i, low + largest);
            heapify(array, n, largest, low);
        }
    }

    private void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
