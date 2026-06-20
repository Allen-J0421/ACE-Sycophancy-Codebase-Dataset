import java.util.Arrays;
import java.util.Objects;

public final class RadixSort {
    private static final int RADIX = 10;

    private RadixSort() {
    }

    public static void sort(int[] values) {
        validateInput(values);
        sortValidated(values);
    }

    public static int[] sortedCopy(int[] values) {
        validateInput(values);
        int[] copy = Arrays.copyOf(values, values.length);
        sortValidated(copy);
        return copy;
    }

    private static void sortValidated(int[] values) {
        if (values.length < 2) {
            return;
        }

        int[] output = new int[values.length];
        int[] counts = new int[RADIX];
        int maxValue = findMax(values);

        for (int exponent = 1; maxValue / exponent > 0; exponent *= RADIX) {
            countingSortByDigit(values, output, counts, exponent);
        }
    }

    private static void validateInput(int[] values) {
        Objects.requireNonNull(values, "values");

        for (int value : values) {
            if (value < 0) {
                throw new IllegalArgumentException(
                    "Radix sort only supports non-negative integers."
                );
            }
        }
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
        int[] output,
        int[] counts,
        int exponent
    ) {
        Arrays.fill(counts, 0);

        for (int value : values) {
            counts[digitAt(value, exponent)]++;
        }

        for (int digit = 1; digit < RADIX; digit++) {
            counts[digit] += counts[digit - 1];
        }

        for (int index = values.length - 1; index >= 0; index--) {
            int value = values[index];
            int digit = digitAt(value, exponent);
            output[--counts[digit]] = value;
        }

        System.arraycopy(output, 0, values, 0, values.length);
    }

    private static int digitAt(int value, int exponent) {
        return (value / exponent) % RADIX;
    }
}
