abstract class AbstractHeap {

    void sort(int n) {
        build(n);
        for (int i = n - 1; i > 0; i--) {
            swap(0, i);
            siftDown(0, i);
        }
    }

    private void build(int n) {
        for (int i = n / 2 - 1; i >= 0; i--)
            siftDown(i, n);
    }

    private void siftDown(int i, int n) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && isGreater(l, largest))
            largest = l;
        if (r < n && isGreater(r, largest))
            largest = r;
        if (largest != i) {
            swap(i, largest);
            siftDown(largest, n);
        }
    }

    protected abstract boolean isGreater(int i, int j);
    protected abstract void swap(int i, int j);
}
