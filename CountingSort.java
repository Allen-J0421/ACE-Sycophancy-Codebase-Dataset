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
    @Deprecated(forRemoval = false)
    public static int[] countSort(int[] values) {
        return sortedCopy(values);
    }

    /**
     * Legacy wrapper retained for compatibility.
     */
    @Deprecated(forRemoval = false)
    public static int[] sort(int[] values) {
        return sortedCopy(values);
    }

    /**
     * Returns a sorted copy of {@code values}.
     */
    public static int[] sortedCopy(int[] values) {
        SortRequest request = SortRequest.copyOf(values);
        execute(request);
        return request.output();
    }

    /**
     * Writes the sorted contents of {@code input} into {@code output}.
     */
    public static void copyInto(int[] input, int[] output) {
        execute(SortRequest.into(input, output));
    }

    /**
     * Sorts {@code values} in place.
     */
    public static void sortInPlace(int[] values) {
        execute(SortRequest.inPlace(values));
    }

    private static void execute(SortRequest request) {
        if (request.source().length == 0) {
            return;
        }

        CountingTable countingTable = CountingTable.from(request.source());
        countingTable.writeSortedValuesTo(request.source(), request.output());
    }

    private static void requireSameLength(int[] input, int[] output) {
        if (input.length != output.length) {
            throw new IllegalArgumentException(
                "Input and output arrays must have the same length."
            );
        }
    }

    private record SortRequest(int[] source, int[] output) {
        private static SortRequest copyOf(int[] values) {
            Objects.requireNonNull(values, "values");
            return new SortRequest(values, new int[values.length]);
        }

        private static SortRequest into(int[] input, int[] output) {
            Objects.requireNonNull(input, "input");
            Objects.requireNonNull(output, "output");
            requireSameLength(input, output);
            return new SortRequest(aliasSafeSource(input, output), output);
        }

        private static SortRequest inPlace(int[] values) {
            Objects.requireNonNull(values, "values");
            return new SortRequest(values.clone(), values);
        }

        private static int[] aliasSafeSource(int[] input, int[] output) {
            return input == output ? input.clone() : input;
        }
    }

    private record CountingTable(Range range, int[] prefixCounts) {
        private static CountingTable from(int[] values) {
            Range range = Range.from(values);
            int[] prefixCounts = new int[range.size()];

            for (int value : values) {
                prefixCounts[range.indexOf(value)]++;
            }

            for (int i = 1; i < prefixCounts.length; i++) {
                prefixCounts[i] += prefixCounts[i - 1];
            }

            return new CountingTable(range, prefixCounts);
        }

        private void writeSortedValuesTo(int[] input, int[] output) {
            for (int i = input.length - 1; i >= 0; i--) {
                int value = input[i];
                output[takeOutputIndex(value)] = value;
            }
        }

        private int takeOutputIndex(int value) {
            int countIndex = range.indexOf(value);
            int outputIndex = prefixCounts[countIndex] - 1;
            prefixCounts[countIndex]--;
            return outputIndex;
        }
    }

    private record Range(int minValue, int maxValue) {
        private static Range from(int[] values) {
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
