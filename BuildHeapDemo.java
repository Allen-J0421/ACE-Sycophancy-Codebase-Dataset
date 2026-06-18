import java.util.Arrays;

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

        System.out.println(Arrays.toString(sample));
    }
}
