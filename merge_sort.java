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
            Run left = new Run(source, start, middle);
            Run right = new Run(source, middle, end);
            int targetIndex = start;

            while (left.hasRemaining() && right.hasRemaining()) {
                if (left.peek() <= right.peek()) {
                    target[targetIndex++] = left.take();
                } else {
                    target[targetIndex++] = right.take();
                }
            }

            copyRemaining(left, target, targetIndex);
            copyRemaining(right, target, targetIndex + left.remainingLength());
        }

        private void copyRemaining(Run run, int[] target, int targetIndex) {
            if (run.hasRemaining()) {
                System.arraycopy(run.source, run.index, target, targetIndex, run.remainingLength());
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

        private static final class Run {
            private final int[] source;
            private final int end;
            private int index;

            private Run(int[] source, int start, int end) {
                this.source = source;
                this.index = start;
                this.end = end;
            }

            private boolean hasRemaining() {
                return index < end;
            }

            private int peek() {
                return source[index];
            }

            private int take() {
                return source[index++];
            }

            private int remainingLength() {
                return end - index;
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
