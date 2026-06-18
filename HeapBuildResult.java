import java.util.Arrays;

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
                Arrays.copyOf(requireValues(inputValues), inputValues.length),
                Arrays.copyOf(requireValues(heapValues), heapValues.length),
                validMaxHeap);
    }

    public int[] inputValues() {
        return Arrays.copyOf(inputValues, inputValues.length);
    }

    public int[] heapValues() {
        return Arrays.copyOf(heapValues, heapValues.length);
    }

    public boolean isValidMaxHeap() {
        return validMaxHeap;
    }

    private static int[] requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        return values;
    }
}
