import java.util.Objects;
import java.util.StringJoiner;

final class BuildHeap {
    private static final String VALUE_SEPARATOR = " ";
    private static final int[] SAMPLE_VALUES = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

    private BuildHeap() {
    }

    private static void siftDown(int[] values, int heapSize, int startIndex) {
        int currentIndex = startIndex;

        while (hasLeftChild(currentIndex, heapSize)) {
            int largerChildIndex = largerChildIndex(values, heapSize, currentIndex);

            if (isHeapOrdered(values, currentIndex, largerChildIndex)) {
                return;
            }

            swap(values, currentIndex, largerChildIndex);
            currentIndex = largerChildIndex;
        }
    }

    private static boolean hasLeftChild(int index, int heapSize) {
        return leftChildIndex(index) < heapSize;
    }

    private static int largerChildIndex(int[] values, int heapSize, int parentIndex) {
        int leftChildIndex = leftChildIndex(parentIndex);
        int rightChildIndex = rightChildIndex(parentIndex);

        if (isRightChildLarger(values, heapSize, leftChildIndex, rightChildIndex)) {
            return rightChildIndex;
        }

        return leftChildIndex;
    }

    private static boolean isHeapOrdered(int[] values, int parentIndex, int childIndex) {
        return values[parentIndex] >= values[childIndex];
    }

    private static boolean isRightChildLarger(
            int[] values,
            int heapSize,
            int leftChildIndex,
            int rightChildIndex) {
        return rightChildIndex < heapSize && values[rightChildIndex] > values[leftChildIndex];
    }

    private static int leftChildIndex(int index) {
        return 2 * index + 1;
    }

    private static int rightChildIndex(int index) {
        return 2 * index + 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    static void buildHeap(int[] values) {
        buildMaxHeap(values);
    }

    static void buildMaxHeap(int[] values) {
        Objects.requireNonNull(values, "values");

        for (int index = lastParentIndex(values.length); index >= 0; index--) {
            siftDown(values, values.length, index);
        }
    }

    private static int lastParentIndex(int heapSize) {
        return heapSize / 2 - 1;
    }

    private static String formatValues(int[] values) {
        if (values.length == 0) {
            return "";
        }

        StringJoiner output = new StringJoiner(VALUE_SEPARATOR);

        for (int value : values) {
            output.add(Integer.toString(value));
        }

        return output.toString() + VALUE_SEPARATOR;
    }

    private static int[] sampleValues() {
        return SAMPLE_VALUES.clone();
    }

    public static void main(String[] args) {
        int[] values = sampleValues();

        buildMaxHeap(values);
        System.out.println(formatValues(values));
    }
}
