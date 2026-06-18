import java.util.Comparator;

public final class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        requireValues(values);
        sort(values, 0, values.length);
    }

    public static void sort(int[] values, int fromInclusive, int toExclusive) {
        requireValues(values);
        validateRange(values.length, fromInclusive, toExclusive);
        if (toExclusive - fromInclusive < 2) {
            return;
        }

        new IntMergeSorter(values, fromInclusive, toExclusive).sort(fromInclusive, toExclusive);
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

        new ObjectMergeSorter<>(values, comparator).sort(0, values.length);
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

    private static void validateRange(int length, int fromInclusive, int toExclusive) {
        if (fromInclusive < 0 || toExclusive > length || fromInclusive > toExclusive) {
            throw new IndexOutOfBoundsException(
                "Invalid range: [" + fromInclusive + ", " + toExclusive + ") for length " + length
            );
        }
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    private abstract static class MergeSorter {
        final void sort(int left, int right) {
            if (right - left < 2) {
                return;
            }

            int mid = midpoint(left, right);
            sort(left, mid);
            sort(mid, right);
            merge(left, mid, right);
        }

        abstract void merge(int left, int mid, int right);
    }

    private static final class IntMergeSorter extends MergeSorter {
        private final int[] values;
        private final int[] buffer;
        private final int bufferOffset;

        private IntMergeSorter(int[] values, int fromInclusive, int toExclusive) {
            this.values = values;
            this.buffer = new int[toExclusive - fromInclusive];
            this.bufferOffset = fromInclusive;
        }

        @Override
        void merge(int left, int mid, int right) {
            int bufferLeft = left - bufferOffset;
            System.arraycopy(values, left, buffer, bufferLeft, right - left);

            int leftIndex = bufferLeft;
            int leftEnd = bufferLeft + (mid - left);
            int rightIndex = leftEnd;
            int destIndex = left;
            int bufferRight = bufferLeft + (right - left);

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

    private static final class ObjectMergeSorter<T> extends MergeSorter {
        private final T[] values;
        private final Object[] buffer;
        private final Comparator<? super T> comparator;

        private ObjectMergeSorter(T[] values, Comparator<? super T> comparator) {
            this.values = values;
            this.comparator = comparator;
            this.buffer = new Object[values.length];
        }

        @Override
        void merge(int left, int mid, int right) {
            System.arraycopy(values, left, buffer, left, right - left);

            int leftIndex = left;
            int rightIndex = mid;
            int destIndex = left;
            int leftEnd = mid;
            int bufferRight = right;

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
}
