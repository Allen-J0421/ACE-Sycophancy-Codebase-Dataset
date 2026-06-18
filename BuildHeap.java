public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void buildHeap(int[] values) {
        IntArrayHeap.wrap(values).buildMaxHeap();
    }

    public static int[] buildHeapCopy(int[] values) {
        return IntArrayHeap.copyOf(values).buildMaxHeap().toArray();
    }

    public static boolean isMaxHeap(int[] values) {
        return IntArrayHeap.wrap(values).isMaxHeap();
    }
}
