package prefixsum;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.StringJoiner;

public final class PrefixSumArray extends AbstractList<Integer> implements RandomAccess {

    private static final PrefixSumArray EMPTY = new PrefixSumArray(new int[0]);

    private final int[] values;

    private PrefixSumArray(int[] values) {
        this.values = values;
    }

    public static PrefixSumArray from(int[] values) {
        int[] source = Objects.requireNonNull(values, "values must not be null");
        if (source.length == 0) {
            return EMPTY;
        }

        int[] prefixSums = new int[source.length];
        int runningTotal = 0;

        for (int i = 0; i < source.length; i++) {
            runningTotal += source[i];
            prefixSums[i] = runningTotal;
        }

        return new PrefixSumArray(prefixSums);
    }

    public static PrefixSumArray empty() {
        return EMPTY;
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public Integer get(int index) {
        return valueAt(index);
    }

    public int valueAt(int index) {
        return values[index];
    }

    public int[] toIntArray() {
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
        StringJoiner output = new StringJoiner(Character.toString(separator));

        for (int value : values) {
            output.add(Integer.toString(value));
        }

        return output.toString();
    }

    @Override
    public String toString() {
        return join(' ');
    }
}
