public final class HeapBuildResult {

    private final int[] inputValues;
    private final int[] heapValues;
    private final boolean validMaxHeap;

    private HeapBuildResult(int[] inputValues, int[] heapValues, boolean validMaxHeap) {
        this.inputValues = inputValues;
        this.heapValues = heapValues;
        this.validMaxHeap = validMaxHeap;
    }

    static HeapBuildResult of(int[] inputValues, int[] heapValues, boolean validMaxHeap) {
        return new HeapBuildResult(
                IntArrays.copyOf(inputValues),
                IntArrays.copyOf(heapValues),
                validMaxHeap);
    }

    public int[] inputValues() {
        return IntArrays.copyOf(inputValues);
    }

    public int[] heapValues() {
        return IntArrays.copyOf(heapValues);
    }

    public boolean isValidMaxHeap() {
        return validMaxHeap;
    }
}
