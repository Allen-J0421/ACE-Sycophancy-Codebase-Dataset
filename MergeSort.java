import java.util.Comparator;

public final class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        requireValues(values);
        sort(values, new Range(0, values.length));
    }

    private static void sort(int[] values, Range range) {
        if (range.size() < 2) {
            return;
        }

        new IntMergeSorter(values, range).sort();
    }

    public static void sort(int[] values, int fromInclusive, int toExclusive) {
        requireValues(values);
        Range range = new Range(fromInclusive, toExclusive);
        validateRange(values.length, range);
        sort(values, range);
    }

    public static <T extends Comparable<? super T>> void sort(T[] values) {
        sort(values, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] values, Comparator<? super T> comparator) {
        requireValues(values);
        requireComparator(comparator);
        if (values.length < 2) {
            return;
        }

        new ObjectMergeSorter<>(values, comparator).sort(new Range(0, values.length));
    }

    private static void requireValues(Object values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static void requireComparator(Object comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
    }

    private static void validateRange(int length, Range range) {
        if (range.fromInclusive < 0 || range.toExclusive > length || range.fromInclusive > range.toExclusive) {
            throw new IndexOutOfBoundsException(
                "Invalid range: [" + range.fromInclusive + ", " + range.toExclusive + ") for length " + length
            );
        }
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    private static final class IntMergeSorter {
        private final int[] values;
        private final int[] buffer;
        private final int bufferOffset;
        private final Range range;

        private IntMergeSorter(int[] values, Range range) {
            this.values = values;
            this.range = range;
            this.buffer = new int[range.size()];
            this.bufferOffset = range.fromInclusive;
        }

        private void sort() {
            sort(range);
        }

        private void sort(Range currentRange) {
            if (currentRange.size() < 2) {
                return;
            }

            int mid = midpoint(currentRange.fromInclusive, currentRange.toExclusive);
            Range leftRange = new Range(currentRange.fromInclusive, mid);
            Range rightRange = new Range(mid, currentRange.toExclusive);
            sort(leftRange);
            sort(rightRange);
            merge(currentRange, leftRange, rightRange);
        }

        private void merge(Range currentRange, Range leftRange, Range rightRange) {
            int bufferLeft = currentRange.fromInclusive - bufferOffset;
            System.arraycopy(values, currentRange.fromInclusive, buffer, bufferLeft, currentRange.size());

            int leftIndex = bufferLeft;
            int leftEnd = bufferLeft + leftRange.size();
            int rightIndex = leftEnd;
            int destIndex = currentRange.fromInclusive;
            int bufferRight = bufferLeft + currentRange.size();

            while (leftIndex < leftEnd && rightIndex < bufferRight) {
                if (buffer[leftIndex] <= buffer[rightIndex]) {
                    values[destIndex++] = buffer[leftIndex++];
                } else {
                    values[destIndex++] = buffer[rightIndex++];
                }
            }

            while (leftIndex < leftEnd) {
                values[destIndex++] = buffer[leftIndex++];
            }
        }
    }

    private static final class ObjectMergeSorter<T> {
        private final T[] values;
        private final Object[] buffer;
        private final Comparator<? super T> comparator;

        private ObjectMergeSorter(T[] values, Comparator<? super T> comparator) {
            this.values = values;
            this.comparator = comparator;
            this.buffer = new Object[values.length];
        }

        private void sort(Range range) {
            if (range.size() < 2) {
                return;
            }

            int mid = midpoint(range.fromInclusive, range.toExclusive);
            Range leftRange = new Range(range.fromInclusive, mid);
            Range rightRange = new Range(mid, range.toExclusive);
            sort(leftRange);
            sort(rightRange);
            merge(range, leftRange, rightRange);
        }

        private void merge(Range currentRange, Range leftRange, Range rightRange) {
            System.arraycopy(values, currentRange.fromInclusive, buffer, currentRange.fromInclusive, currentRange.size());

            int leftIndex = currentRange.fromInclusive;
            int rightIndex = leftRange.toExclusive;
            int destIndex = currentRange.fromInclusive;
            int leftEnd = leftRange.toExclusive;
            int bufferRight = rightRange.toExclusive;

            while (leftIndex < leftEnd && rightIndex < bufferRight) {
                @SuppressWarnings("unchecked")
                T leftValue = (T) buffer[leftIndex];
                @SuppressWarnings("unchecked")
                T rightValue = (T) buffer[rightIndex];

                if (comparator.compare(leftValue, rightValue) <= 0) {
                    values[destIndex++] = leftValue;
                    leftIndex++;
                } else {
                    values[destIndex++] = rightValue;
                    rightIndex++;
                }
            }

            while (leftIndex < leftEnd) {
                @SuppressWarnings("unchecked")
                T value = (T) buffer[leftIndex++];
                values[destIndex++] = value;
            }
        }
    }

    private static final class Range {
        private final int fromInclusive;
        private final int toExclusive;

        private Range(int fromInclusive, int toExclusive) {
            this.fromInclusive = fromInclusive;
            this.toExclusive = toExclusive;
        }

        private int size() {
            return toExclusive - fromInclusive;
        }
    }
}
