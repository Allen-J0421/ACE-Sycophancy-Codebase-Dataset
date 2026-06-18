import java.util.Objects;

public final class IntArrayFormatter {
    private static final char VALUE_SEPARATOR = ' ';

    private IntArrayFormatter() {
    }

    public static String format(int[] values) {
        Objects.requireNonNull(values, "values");

        StringBuilder formattedValues = new StringBuilder();
        for (int value : values) {
            formattedValues.append(value).append(VALUE_SEPARATOR);
        }

        return formattedValues.toString();
    }
}
