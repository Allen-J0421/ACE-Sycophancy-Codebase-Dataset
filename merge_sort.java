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
        private final int length;

        private MergeSorter(int[] values) {
            this.values = values;
            this.scratch = values.clone();
            this.length = values.length;
        }

        private void sort() {
            MergeBuffers buffers = new MergeBuffers(scratch, values);

            for (int width = 1; width < length; width *= 2) {
                mergePass(buffers.source, buffers.target, width);
                buffers.swap();
            }

            copyBackIfNeeded(buffers.source);
        }

        private void mergePass(int[] source, int[] target, int width) {
            for (int start = 0; start < length; start += width * 2) {
                int middle = Math.min(start + width, length);
                int end = Math.min(start + (width * 2), length);
                mergeRange(source, target, start, middle, end);
            }
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

            copyRemaining(source, target, leftIndex, middle, targetIndex);
            copyRemaining(source, target, rightIndex, end, targetIndex + (middle - leftIndex));
        }

        private void copyRemaining(int[] source, int[] target, int start, int end, int targetIndex) {
            if (start < end) {
                System.arraycopy(source, start, target, targetIndex, end - start);
            }
        }

        private void copyBackIfNeeded(int[] source) {
            if (source != values) {
                System.arraycopy(source, 0, values, 0, length);
            }
        }

        private static final class MergeBuffers {
            private int[] source;
            private int[] target;

            private MergeBuffers(int[] source, int[] target) {
                this.source = source;
                this.target = target;
            }

            private void swap() {
                int[] nextSource = target;
                target = source;
                source = nextSource;
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
