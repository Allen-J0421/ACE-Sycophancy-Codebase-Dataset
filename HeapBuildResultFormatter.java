final class HeapBuildResultFormatter {

    private HeapBuildResultFormatter() {
        // Utility class.
    }

    static String format(HeapBuildResult result) {
        StringBuilder output = new StringBuilder();
        output.append("Input: ").append(IntArrays.format(result.inputValues())).append(System.lineSeparator());
        output.append("Heap: ").append(IntArrays.format(result.heapValues())).append(System.lineSeparator());
        output.append("Valid max heap: ").append(result.isValidMaxHeap());
        return output.toString();
    }
}
