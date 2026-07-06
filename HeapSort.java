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

    static class RangeHeap {
        private final int[] arr;
        private final int from;
        private final int size;

        private RangeHeap(int[] arr, int from, int to) {
            this.arr = arr;
            this.from = from;
            this.size = to - from;
        }

        private void sort() {
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
            if (l < n && get(l) > get(largest))
                largest = l;
            if (r < n && get(r) > get(largest))
                largest = r;
            if (largest != i) {
                swap(i, largest);
                siftDown(largest, n);
            }
        }

        private int get(int i) {
            return arr[from + i];
        }

        private void swap(int i, int j) {
            int temp = arr[from + i];
            arr[from + i] = arr[from + j];
            arr[from + j] = temp;
        }
    }

    static class ObjectRangeHeap<T> implements Heap<T> {
        private final T[] arr;
        private final int from;
        private final int size;
        private final Comparator<T> cmp;

        ObjectRangeHeap(T[] arr, int from, int to, Comparator<T> cmp) {
            this.arr = arr;
            this.from = from;
            this.size = to - from;
            this.cmp = cmp;
        }

        @Override
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
            if (l < n && cmp.compare(get(l), get(largest)) > 0)
                largest = l;
            if (r < n && cmp.compare(get(r), get(largest)) > 0)
                largest = r;
            if (largest != i) {
                swap(i, largest);
                siftDown(largest, n);
            }
        }

        private T get(int i) {
            return arr[from + i];
        }

        private void swap(int i, int j) {
            T temp = arr[from + i];
            arr[from + i] = arr[from + j];
            arr[from + j] = temp;
        }
    }
}
