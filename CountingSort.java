import java.util.Objects;

/**
 * Stable counting sort for integer arrays.
 */
public final class CountingSort {

    private CountingSort() {
    }

    /**
     * Legacy wrapper retained for compatibility.
     */
    public static int[] countSort(int[] values) {
        return sortedCopy(values);
    }

    /**
     * Legacy wrapper retained for compatibility.
     */
    public static int[] sort(int[] values) {
        return sortedCopy(values);
    }

    /**
     * Returns a sorted copy of {@code values}.
     */
    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");
        int[] sortedValues = new int[values.length];
        copyInto(values, sortedValues);
        return sortedValues;
    }

    /**
     * Writes the sorted contents of {@code input} into {@code output}.
     */
    public static void copyInto(int[] input, int[] output) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(output, "output");
        requireSameLength(input, output);

        if (input.length == 0) {
            return;
        }

        int[] source = aliasSafeInput(input, output);
        CountingTable countingTable = CountingTable.from(source);
        writeSortedValues(source, output, countingTable);
    }

    /**
     * Sorts {@code values} in place.
     */
    public static void sortInPlace(int[] values) {
        copyInto(values, values);
    }

    private static int[] aliasSafeInput(int[] input, int[] output) {
        return input == output ? input.clone() : input;
    }

    private static void requireSameLength(int[] input, int[] output) {
        if (input.length != output.length) {
            throw new IllegalArgumentException(
                "Input and output arrays must have the same length."
            );
        }
    }

    private static Range findRange(int[] values) {
        int minValue = values[0];
        int maxValue = values[0];

        for (int i = 1; i < values.length; i++) {
            int value = values[i];
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        return new Range(minValue, maxValue);
    }

    private static void writeSortedValues(
        int[] input,
        int[] output,
        CountingTable countingTable
    ) {
        for (int i = input.length - 1; i >= 0; i--) {
            int value = input[i];
            output[countingTable.takeOutputIndex(value)] = value;
        }
    }

    private record CountingTable(Range range, int[] prefixCounts) {
        private static CountingTable from(int[] values) {
            Range range = findRange(values);
            int[] prefixCounts = new int[range.size()];

            for (int value : values) {
                prefixCounts[range.indexOf(value)]++;
            }

            for (int i = 1; i < prefixCounts.length; i++) {
                prefixCounts[i] += prefixCounts[i - 1];
            }

            return new CountingTable(range, prefixCounts);
        }

        private int takeOutputIndex(int value) {
            int countIndex = range.indexOf(value);
            int outputIndex = prefixCounts[countIndex] - 1;
            prefixCounts[countIndex]--;
            return outputIndex;
        }
    }

    private record Range(int minValue, int maxValue) {
        private int size() {
            long size = (long) maxValue - minValue + 1;
            if (size > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                    "Counting sort range is too large to index."
                );
            }

            return (int) size;
        }

        private int indexOf(int value) {
            return value - minValue;
        }
    }
}
