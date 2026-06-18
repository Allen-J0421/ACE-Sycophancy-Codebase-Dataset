package prefixsum;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

public final class PrefixSumArray implements Iterable<Integer> {

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

    public int size() {
        return values.length;
    }

    public boolean isEmpty() {
        return values.length == 0;
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
        StringJoiner output = new StringJoiner(Character.toString(separator));

        for (int value : values) {
            output.add(Integer.toString(value));
        }

        return output.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return values[index++];
            }
        };
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
