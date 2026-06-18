class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        new MergeSorter(values).sort();
    }

    private static final class MergeSorter {
        private final int[] values;
        private final int[] scratch;

        private MergeSorter(int[] values) {
            this.values = values;
            this.scratch = values.clone();
        }

        private void sort() {
            sortRange(scratch, values, 0, values.length);
        }

        private void sortRange(int[] source, int[] target, int start, int end) {
            if (end - start < 2) {
                return;
            }

            int middle = start + (end - start) / 2;
            sortRange(target, source, start, middle);
            sortRange(target, source, middle, end);
            mergeRange(source, target, start, middle, end);
        }

        private void mergeRange(int[] source, int[] target, int start, int middle, int end) {
            int leftIndex = start;
            int rightIndex = middle;
            int targetIndex = start;

            while (leftIndex < middle && rightIndex < end) {
                if (source[leftIndex] <= source[rightIndex]) {
                    target[targetIndex++] = source[leftIndex++];
                } else {
                    target[targetIndex++] = source[rightIndex++];
                }
            }

            while (leftIndex < middle) {
                target[targetIndex++] = source[leftIndex++];
            }

            while (rightIndex < end) {
                target[targetIndex++] = source[rightIndex++];
            }
        }
    }

    private static void printArray(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        sort(values);
        printArray(values);
    }
}
