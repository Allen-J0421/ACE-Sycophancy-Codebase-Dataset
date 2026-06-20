import java.util.Arrays;
import java.util.Objects;

public final class RadixSort {
    private static final int RADIX = 10;

    private RadixSort() {
    }

    public static void sort(int[] values) {
        sortValidated(requireSupportedValues(values));
    }

    public static int[] sortedCopy(int[] values) {
        int[] copy = copyOfSupportedValues(values);
        sortValidated(copy);
        return copy;
    }

    private static void sortValidated(int[] values) {
        if (values.length < 2) {
            return;
        }

        SortWorkspace workspace = new SortWorkspace(values);
        int maxValue = findMax(values);

        for (long exponent = 1; maxValue / exponent > 0; exponent *= RADIX) {
            countingSortByDigit(
                workspace.source(),
                workspace.target(),
                workspace.counts(),
                exponent
            );
            workspace.swapBuffers();
        }

        workspace.copyResultBackIfNeeded();
    }

    private static int[] requireSupportedValues(int[] values) {
        Objects.requireNonNull(values, "values");

        for (int value : values) {
            if (value < 0) {
                throw new IllegalArgumentException(
                    "Radix sort only supports non-negative integers."
                );
            }
        }

        return values;
    }

    private static int[] copyOfSupportedValues(int[] values) {
        int[] validatedValues = requireSupportedValues(values);
        return Arrays.copyOf(validatedValues, validatedValues.length);
    }

    private static int findMax(int[] values) {
        int maxValue = values[0];
        for (int index = 1; index < values.length; index++) {
            if (values[index] > maxValue) {
                maxValue = values[index];
            }
        }
        return maxValue;
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

    private static final class SortWorkspace {
        private final int[] originalValues;
        private final int[] buffer;
        private final int[] counts = new int[RADIX];
        private int[] source;
        private int[] target;

        private SortWorkspace(int[] values) {
            this.originalValues = values;
            this.buffer = new int[values.length];
            this.source = values;
            this.target = buffer;
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
