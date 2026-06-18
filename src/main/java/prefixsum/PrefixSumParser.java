package prefixsum;

final class PrefixSumParser {

    private PrefixSumParser() {
        // Utility class.
    }

    static int[] parse(String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("args must not be null");
        }

        int[] values = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = parseValue(args[i], i);
        }

        return values;
    }

    private static int parseValue(String value, int index) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing integer value at position " + index);
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                "Invalid integer at position " + index + ": \"" + value + "\"",
                exception);
        }
    }
}
