public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void buildHeap(int[] values) {
        IntArrayHeap.buildInPlace(values);
    }

    public static int[] buildHeapCopy(int[] values) {
        return IntArrayHeap.buildCopy(values);
    }

    public static boolean isMaxHeap(int[] values) {
        return IntArrayHeap.isMaxHeap(values);
    }

    public static HeapBuildResult analyze(int[] values) {
        return IntArrayHeap.analyze(values);
    }
}
