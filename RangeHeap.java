class RangeHeap extends AbstractHeap implements HeapSorter<Integer> {
    private final int[] arr;
    private int from;

    RangeHeap(int[] arr) {
        this.arr = arr;
    }

    @Override
    public void sort(int from, int to) {
        this.from = from;
        sort(to - from);
    }

    @Override
    protected boolean isGreater(int i, int j) {
        return arr[from + i] > arr[from + j];
    }

    @Override
    protected void swap(int i, int j) {
        int temp = arr[from + i];
        arr[from + i] = arr[from + j];
        arr[from + j] = temp;
    }
}
