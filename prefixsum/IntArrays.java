package prefixsum;

import java.util.Objects;

public final class IntArrays {

    private IntArrays() {
    }

    public static String joinWithSpaces(int[] values) {
        return join(values, ' ');
    }

    public static String join(int[] values, char separator) {
        int[] source = Objects.requireNonNull(values, "values must not be null");
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < source.length; i++) {
            if (i > 0) {
                output.append(separator);
            }
            output.append(source[i]);
        }

        return output.toString();
    }
}
