import java.util.Arrays;

final class HeapBuildResult {

    private final int[] inputValues;
    private final int[] heapValues;
    private final boolean validMaxHeap;

    private HeapBuildResult(int[] inputValues, int[] heapValues, boolean validMaxHeap) {
        this.inputValues = inputValues;
        this.heapValues = heapValues;
        this.validMaxHeap = validMaxHeap;
    }

    static HeapBuildResult from(int[] values) {
        int[] inputCopy = Arrays.copyOf(requireValues(values), values.length);
        int[] heapCopy = BuildHeap.buildHeapCopy(inputCopy);
        boolean validMaxHeap = BuildHeap.isMaxHeap(heapCopy);

        return new HeapBuildResult(inputCopy, heapCopy, validMaxHeap);
    }

    int[] inputValues() {
        return Arrays.copyOf(inputValues, inputValues.length);
    }

    int[] heapValues() {
        return Arrays.copyOf(heapValues, heapValues.length);
    }

    boolean isValidMaxHeap() {
        return validMaxHeap;
    }

    private static int[] requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        return values;
    }
}
