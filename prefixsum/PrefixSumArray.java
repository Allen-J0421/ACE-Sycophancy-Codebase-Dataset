package prefixsum;

import java.util.Arrays;

public final class PrefixSumArray {

    private final int[] values;

    PrefixSumArray(int[] values) {
        this.values = values;
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

    public String join(char separator) {
        StringBuilder output = new StringBuilder();

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
}
