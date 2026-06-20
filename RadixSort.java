import java.util.Arrays;
import java.util.Objects;

public final class RadixSort {
    private static final int RADIX = 10;

    private RadixSort() {
    }

    public static void sort(int[] values) {
        sortValidated(analyze(values));
    }

    public static int[] sortedCopy(int[] values) {
        SortContext input = analyze(values);
        int[] copy = Arrays.copyOf(input.originalValues(), input.length());
        sortValidated(SortContext.forValues(copy, input.maxValue()));
        return copy;
    }

    private static void sortValidated(SortContext context) {
        if (context.length() < 2) {
            return;
        }

        for (long exponent = 1; context.maxValue() / exponent > 0; exponent *= RADIX) {
            countingSortByDigit(
                context.source(),
                context.target(),
                context.counts(),
                exponent
            );
            context.swapBuffers();
        }

        context.copyResultBackIfNeeded();
    }

    private static SortContext analyze(int[] values) {
        Objects.requireNonNull(values, "values");
        if (values.length == 0) {
            return SortContext.forValues(values, 0);
        }

        int maxValue = values[0];

        for (int index = 0; index < values.length; index++) {
            int value = values[index];
            if (value < 0) {
                throw new IllegalArgumentException(
                    "Radix sort only supports non-negative integers."
                );
            }

            if (value > maxValue) {
                maxValue = value;
            }
        }

        return SortContext.forValues(values, maxValue);
    }

    private static void countingSortByDigit(
        int[] source,
        int[] target,
        int[] counts,
        long exponent
    ) {
        Arrays.fill(counts, 0);

        for (int value : source) {
            counts[digitAt(value, exponent)]++;
        }

        for (int digit = 1; digit < RADIX; digit++) {
            counts[digit] += counts[digit - 1];
        }

        for (int index = source.length - 1; index >= 0; index--) {
            int value = source[index];
            int digit = digitAt(value, exponent);
            target[--counts[digit]] = value;
        }
    }

    private static int digitAt(int value, long exponent) {
        return (int) ((value / exponent) % RADIX);
    }

    private static final class SortContext {
        private final int[] originalValues;
        private final int maxValue;
        private final int[] buffer;
        private final int[] counts = new int[RADIX];
        private int[] source;
        private int[] target;

        private SortContext(int[] values, int maxValue) {
            this.originalValues = values;
            this.maxValue = maxValue;
            this.buffer = new int[values.length];
            this.source = values;
            this.target = buffer;
        }

        private static SortContext forValues(int[] values, int maxValue) {
            return new SortContext(values, maxValue);
        }

        private int length() {
            return originalValues.length;
        }

        private int[] originalValues() {
            return originalValues;
        }

        private int maxValue() {
            return maxValue;
        }

        private int[] source() {
            return source;
        }

        private int[] target() {
            return target;
        }

        private int[] counts() {
            return counts;
        }

        private void swapBuffers() {
            int[] previousSource = source;
            source = target;
            target = previousSource;
        }

        private void copyResultBackIfNeeded() {
            if (source != originalValues) {
                System.arraycopy(source, 0, originalValues, 0, originalValues.length);
            }
        }
    }
}
