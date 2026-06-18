class MergeSort {
    private static final int[] SAMPLE_VALUES = {38, 27, 43, 10};

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
        private int[] source;
        private int[] target;

        private MergeSorter(int[] values) {
            this.values = values;
            this.scratch = values.clone();
            this.length = values.length;
            this.source = scratch;
            this.target = values;
        }

        private void sort() {
            for (int width = 1; width < length; width *= 2) {
                mergePass(width);
                swapBuffers();
            }

            copyBackIfNeeded();
        }

        private void mergePass(int width) {
            for (int start = 0; start < length; start += width * 2) {
                mergeRange(MergeWindow.from(start, width, length));
            }
        }

        private void mergeRange(MergeWindow window) {
            Run left = window.leftRun(source);
            Run right = window.rightRun(source);
            int targetIndex = window.targetStart();

            while (left.hasRemaining() && right.hasRemaining()) {
                if (left.peek() <= right.peek()) {
                    target[targetIndex++] = left.take();
                } else {
                    target[targetIndex++] = right.take();
                }
            }

            left.copyRemainingTo(target, targetIndex);
            right.copyRemainingTo(target, targetIndex + left.remainingLength());
        }

        private void copyBackIfNeeded() {
            if (source != values) {
                System.arraycopy(source, 0, values, 0, length);
            }
        }

        private void swapBuffers() {
            int[] nextSource = target;
            target = source;
            source = nextSource;
        }

        private static final class MergeWindow {
            private final int start;
            private final int middle;
            private final int end;

            private MergeWindow(int start, int middle, int end) {
                this.start = start;
                this.middle = middle;
                this.end = end;
            }

            private static MergeWindow from(int start, int width, int length) {
                int middle = Math.min(start + width, length);
                int end = Math.min(start + (width * 2), length);
                return new MergeWindow(start, middle, end);
            }

            private Run leftRun(int[] source) {
                return new Run(source, start, middle);
            }

            private Run rightRun(int[] source) {
                return new Run(source, middle, end);
            }

            private int targetStart() {
                return start;
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

            private void copyRemainingTo(int[] target, int targetIndex) {
                if (hasRemaining()) {
                    System.arraycopy(source, index, target, targetIndex, remainingLength());
                }
            }
        }
    }

    private static void printArray(int[] values) {
        System.out.println(formatArray(values));
    }

    private static String formatArray(int[] values) {
        StringBuilder builder = new StringBuilder();
        for (int value : values) {
            builder.append(value).append(' ');
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        int[] values = SAMPLE_VALUES.clone();

        sort(values);
        printArray(values);
    }
}
