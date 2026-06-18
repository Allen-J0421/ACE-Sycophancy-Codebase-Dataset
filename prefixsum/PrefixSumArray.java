package prefixsum;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class PrefixSumArray {

    private static final PrefixSumArray EMPTY = new PrefixSumArray(new int[0]);

    private final int[] values;

    private PrefixSumArray(int[] values) {
        this.values = values;
    }

    public static PrefixSumArray from(int[] values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length == 0) {
            return EMPTY;
        }
        return new PrefixSumArray(PrefixSumCalculator.compute(values));
    }

    public static PrefixSumArray empty() {
        return EMPTY;
    }

    public int size() {
        return values.length;
    }

    public boolean isEmpty() {
        return values.length == 0;
    }

    public int valueAt(int index) {
        return values[index];
    }

    public int lastValue() {
        if (isEmpty()) {
            throw new IllegalStateException("prefix sum array is empty");
        }

        return values[values.length - 1];
    }

    public int[] toIntArray() {
        return Arrays.copyOf(values, values.length);
    }

    public List<Integer> asList() {
        return new AbstractList<>() {
            @Override
            public Integer get(int index) {
                return valueAt(index);
            }

            @Override
            public int size() {
                return values.length;
            }
        };
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

    public String format() {
        return join(' ');
    }

    @Override
    public String toString() {
        return "PrefixSumArray[" + join(',') + "]";
    }
}
