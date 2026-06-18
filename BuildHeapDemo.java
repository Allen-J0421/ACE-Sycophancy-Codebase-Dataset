import java.util.StringJoiner;

public final class BuildHeapDemo {

    private BuildHeapDemo() {
        // Demo class.
    }

    public static void main(String[] args) {
        int[] sample = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        BuildHeap.buildMaxHeap(sample);

        if (!BuildHeap.isMaxHeap(sample)) {
            throw new IllegalStateException("Heap construction failed");
        }

        System.out.println(formatArray(sample));
    }

    private static String formatArray(int[] values) {
        StringJoiner joiner = new StringJoiner(" ");

        for (int value : values) {
            joiner.add(Integer.toString(value));
        }

        return joiner.toString();
    }
}
