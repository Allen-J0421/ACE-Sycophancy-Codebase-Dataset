/**
 * Builds a binary max-heap from an arbitrary array in O(n) time using the
 * bottom-up (Floyd's) heap-construction algorithm.
 *
 * <p>The file is named {@code building_heap_from_array.java}, so no top-level
 * type may be {@code public}; the demo is launched via {@link BuildHeapDemo}.
 */
final class MaxHeap {

    private MaxHeap() {
    } // static-only utility

    /**
     * Rearranges {@code arr[0..size)} in place so it satisfies the max-heap
     * property: every parent is >= its children.
     *
     * @param arr  the array to heapify (modified in place)
     * @param size number of leading elements to treat as the heap
     */
    static void build(int[] arr, int size) {
        // Every node past the halfway point is a leaf and already a valid heap,
        // so we only need to sift down the internal nodes, deepest first.
        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(arr, size, i);
        }
    }

    /** Convenience overload that heapifies the entire array. */
    static void build(int[] arr) {
        build(arr, arr.length);
    }

    /**
     * Restores the max-heap property for the subtree rooted at {@code i},
     * assuming both child subtrees are already heaps.
     *
     * <p>Implemented iteratively to avoid recursion overhead on deep heaps.
     */
    private static void siftDown(int[] arr, int size, int i) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < size && arr[left] > arr[largest]) {
                largest = left;
            }
            if (right < size && arr[right] > arr[largest]) {
                largest = right;
            }
            if (largest == i) {
                return; // subtree already satisfies the heap property
            }

            swap(arr, i, largest);
            i = largest; // continue sifting into the affected child
        }
    }

    private static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}

/** Command-line demo for {@link MaxHeap}. */
final class BuildHeapDemo {

    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        MaxHeap.build(arr);

        StringBuilder out = new StringBuilder();
        for (int value : arr) {
            out.append(value).append(' ');
        }
        System.out.println(out.toString().trim());
    }
}
