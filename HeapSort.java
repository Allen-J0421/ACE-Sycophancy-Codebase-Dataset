import java.util.Comparator;

public class HeapSort {

    public static void heapSort(int[] arr) {
        heapSort(arr, 0, arr.length);
    }

    public static void heapSort(int[] arr, int from, int to) {
        if (from < 0 || to > arr.length || from > to)
            throw new IllegalArgumentException(
                "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
        new RangeHeap(arr, from, to).sort();
    }

    public static <T> void heapSort(T[] arr, Comparator<T> cmp) {
        heapSort(arr, 0, arr.length, cmp);
    }

    public static <T> void heapSort(T[] arr, int from, int to, Comparator<T> cmp) {
        if (from < 0 || to > arr.length || from > to)
            throw new IllegalArgumentException(
                "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
        new ObjectRangeHeap<>(arr, from, to, cmp).sort();
    }

    abstract static class AbstractHeap {
        protected final int size;

        AbstractHeap(int size) {
            this.size = size;
        }

        public void sort() {
            build();
            for (int i = size - 1; i > 0; i--) {
                swap(0, i);
                siftDown(0, i);
            }
        }

        private void build() {
            for (int i = size / 2 - 1; i >= 0; i--)
                siftDown(i, size);
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

    static class RangeHeap extends AbstractHeap {
        private final int[] arr;
        private final int from;

        private RangeHeap(int[] arr, int from, int to) {
            super(to - from);
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

    static class ObjectRangeHeap<T> extends AbstractHeap implements Heap<T> {
        private final T[] arr;
        private final int from;
        private final Comparator<T> cmp;

        ObjectRangeHeap(T[] arr, int from, int to, Comparator<T> cmp) {
            super(to - from);
            this.arr = arr;
            this.from = from;
            this.cmp = cmp;
        }

        @Override
        protected boolean isGreater(int i, int j) {
            return cmp.compare(arr[from + i], arr[from + j]) > 0;
        }

        @Override
        protected void swap(int i, int j) {
            T temp = arr[from + i];
            arr[from + i] = arr[from + j];
            arr[from + j] = temp;
        }
    }
}
