import java.util.Objects;

public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void siftDown(int[] values, int heapSize, int rootIndex) {
        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = currentIndex;
            int leftChildIndex = 2 * currentIndex + 1;
            int rightChildIndex = 2 * currentIndex + 2;

            if (leftChildIndex < heapSize && values[leftChildIndex] > values[largestIndex]) {
                largestIndex = leftChildIndex;
            }

            if (rightChildIndex < heapSize && values[rightChildIndex] > values[largestIndex]) {
                largestIndex = rightChildIndex;
            }

            if (largestIndex == currentIndex) {
                return;
            }

            swap(values, currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    public static void buildMaxHeap(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        for (int i = (values.length / 2) - 1; i >= 0; i--) {
            siftDown(values, values.length, i);
        }
    }

    public static void main(String[] args) {
        int[] sample = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        buildMaxHeap(sample);

        if (!isMaxHeap(sample)) {
            throw new IllegalStateException("Heap construction failed");
        }

        System.out.println(formatArray(sample));
    }

    public static boolean isMaxHeap(int[] values) {
        Objects.requireNonNull(values, "values");

        for (int parentIndex = 0; parentIndex < values.length / 2; parentIndex++) {
            int leftChildIndex = 2 * parentIndex + 1;
            int rightChildIndex = 2 * parentIndex + 2;

            if (leftChildIndex < values.length && values[parentIndex] < values[leftChildIndex]) {
                return false;
            }

            if (rightChildIndex < values.length && values[parentIndex] < values[rightChildIndex]) {
                return false;
            }
        }

        return true;
    }

    private static String formatArray(int[] values) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(values[i]);
        }

        return builder.toString();
    }

    private static void swap(int[] values, int i, int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }
}
