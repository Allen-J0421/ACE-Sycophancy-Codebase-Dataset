class RangeHeap extends AbstractHeap {
    private final int[] arr;
    private final int from;

    RangeHeap(int[] arr, int from) {
        this.arr = arr;
        this.from = from;
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
