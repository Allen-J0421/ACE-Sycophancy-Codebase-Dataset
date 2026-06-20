package sorting;

import java.util.Objects;

public final class IntArrayFormatter {
    private static final char VALUE_SEPARATOR = ' ';

    private IntArrayFormatter() {
        // Utility class.
    }

    public static void print(int[] values) {
        System.out.println(format(values));
    }

    public static String format(int[] values) {
        Objects.requireNonNull(values, "values");

        StringBuilder output = new StringBuilder();

        for (int value : values) {
            if (!output.isEmpty()) {
                output.append(VALUE_SEPARATOR);
            }
            output.append(value);
        }

        return output.toString();
    }
}
