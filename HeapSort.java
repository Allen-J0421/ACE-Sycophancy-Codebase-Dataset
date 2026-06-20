public class HeapSort {

    static void heapSort(int[] arr) {
        heapSort(arr, 0, arr.length);
    }

    static void heapSort(int[] arr, int from, int to) {
        new RangeHeap(arr, from, to).sort();
    }

    static class RangeHeap {
        private final int[] arr;
        private final int from;
        private final int size;

        RangeHeap(int[] arr, int from, int to) {
            if (from < 0 || to > arr.length || from > to)
                throw new IllegalArgumentException(
                        "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
            this.arr = arr;
            this.from = from;
            this.size = to - from;
        }

        void sort() {
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

        private void siftDown(int i, int heapSize) {
            while (true) {
                int largest = i;
                int l = 2 * i + 1;
                int r = 2 * i + 2;

                if (l < heapSize && arr[from + l] > arr[from + largest])
                    largest = l;

                if (r < heapSize && arr[from + r] > arr[from + largest])
                    largest = r;

                if (largest == i) break;

                swap(i, largest);
                i = largest;
            }
        }

        private void swap(int i, int j) {
            int temp = arr[from + i];
            arr[from + i] = arr[from + j];
            arr[from + j] = temp;
        }
    }
}
