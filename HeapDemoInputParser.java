import java.util.Arrays;

final class HeapDemoInputParser {

    private static final int[] DEFAULT_INPUT = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};
    private static final String USAGE = "Usage: java -cp . BuildHeapDemo [integer ...]";

    private HeapDemoInputParser() {
        // Utility class.
    }

    static int[] parse(String[] args) {
        if (args.length == 0) {
            return Arrays.copyOf(DEFAULT_INPUT, DEFAULT_INPUT.length);
        }

        int[] values = new int[args.length];
        for (int index = 0; index < args.length; index++) {
            values[index] = parseInteger(args[index]);
        }

        return values;
    }

    static String usage() {
        return USAGE;
    }

    private static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("All arguments must be integers: \"" + value + "\"", exception);
        }
    }
}
