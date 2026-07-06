final class AbstractHeap {

    @FunctionalInterface
    interface Predicate { boolean test(int i, int j); }

    @FunctionalInterface
    interface Consumer { void accept(int i, int j); }

    private AbstractHeap() {}

    static void sort(int n, Predicate isGreater, Consumer swap) {
        build(n, isGreater, swap);
        for (int i = n - 1; i > 0; i--) {
            swap.accept(0, i);
            siftDown(0, i, isGreater, swap);
        }
    }

    private static void build(int n, Predicate isGreater, Consumer swap) {
        for (int i = n / 2 - 1; i >= 0; i--)
            siftDown(i, n, isGreater, swap);
    }

    private static void siftDown(int i, int n, Predicate isGreater, Consumer swap) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && isGreater.test(l, largest)) largest = l;
        if (r < n && isGreater.test(r, largest)) largest = r;
        if (largest != i) {
            swap.accept(i, largest);
            siftDown(largest, n, isGreater, swap);
        }
    }
}
