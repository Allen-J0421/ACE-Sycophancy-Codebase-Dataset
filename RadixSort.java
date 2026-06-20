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
        int[] validatedValues = requireSupportedValues(values);
        int[] copy = Arrays.copyOf(validatedValues, validatedValues.length);
        sortValidated(copy);
        return copy;
    }

    private static void sortValidated(int[] values) {
        if (values.length < 2) {
            return;
        }

        SortWorkspace workspace = new SortWorkspace(values.length);
        int maxValue = findMax(values);

        for (int exponent = 1; maxValue / exponent > 0; exponent *= RADIX) {
            countingSortByDigit(values, workspace, exponent);
        }
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
        int[] values,
        SortWorkspace workspace,
        int exponent
    ) {
        Arrays.fill(workspace.counts, 0);

        for (int value : values) {
            workspace.counts[digitAt(value, exponent)]++;
        }

        for (int digit = 1; digit < RADIX; digit++) {
            workspace.counts[digit] += workspace.counts[digit - 1];
        }

        for (int index = values.length - 1; index >= 0; index--) {
            int value = values[index];
            int digit = digitAt(value, exponent);
            workspace.output[--workspace.counts[digit]] = value;
        }

        System.arraycopy(workspace.output, 0, values, 0, values.length);
    }

    private static int digitAt(int value, int exponent) {
        return (value / exponent) % RADIX;
    }

    private static final class SortWorkspace {
        private final int[] output;
        private final int[] counts = new int[RADIX];

        private SortWorkspace(int length) {
            this.output = new int[length];
        }
    }
}
