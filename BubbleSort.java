class BubbleSort<T extends Comparable<T>> implements Sorter<T> {

    @Override
    public void sort(T[] arr) throws SortException {
        if (arr == null) throw new SortException("Cannot sort a null array");
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    ArrayUtils.swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }
}
