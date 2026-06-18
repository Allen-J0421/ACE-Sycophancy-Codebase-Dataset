package prefixsum;

import java.util.Arrays;
import java.util.Objects;

public final class PrefixSumArray {

    private final int[] values;

    private PrefixSumArray(int[] values) {
        this.values = values;
    }

    public static PrefixSumArray from(int[] values) {
        int[] source = Objects.requireNonNull(values, "values must not be null");
        int[] prefixSums = new int[source.length];
        int runningTotal = 0;

        for (int i = 0; i < source.length; i++) {
            runningTotal += source[i];
            prefixSums[i] = runningTotal;
        }

        return new PrefixSumArray(prefixSums);
    }

    public int length() {
        return values.length;
    }

    public int get(int index) {
        return values[index];
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    public void copyInto(int[] destination) {
        int[] target = Objects.requireNonNull(destination, "destination must not be null");

        if (target.length != values.length) {
            throw new IllegalArgumentException("destination length must match prefix sum length");
        }

        System.arraycopy(values, 0, target, 0, values.length);
    }

    public String join(char separator) {
        StringBuilder output = new StringBuilder(values.length * 2);

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                output.append(separator);
            }
            output.append(values[i]);
        }

        return output.toString();
    }

    @Override
    public String toString() {
        return join(' ');
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PrefixSumArray that)) {
            return false;
        }
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
