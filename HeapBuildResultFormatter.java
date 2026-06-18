import java.util.Arrays;

final class HeapBuildResultFormatter {

    private HeapBuildResultFormatter() {
        // Utility class.
    }

    static String format(HeapBuildResult result) {
        StringBuilder output = new StringBuilder();
        output.append("Input: ").append(Arrays.toString(result.inputValues())).append(System.lineSeparator());
        output.append("Heap: ").append(Arrays.toString(result.heapValues())).append(System.lineSeparator());
        output.append("Valid max heap: ").append(result.isValidMaxHeap());
        return output.toString();
    }
}
