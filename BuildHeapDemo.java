import java.util.Arrays;

public final class BuildHeapDemo {

    private static final int[] DEFAULT_INPUT = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

    private BuildHeapDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        try {
            int[] values = parseValues(args);
            int[] heap = BuildHeap.buildHeapCopy(values);

            System.out.println("Input: " + Arrays.toString(values));
            System.out.println("Heap: " + Arrays.toString(heap));
            System.out.println("Valid max heap: " + BuildHeap.isMaxHeap(heap));
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            printUsage();
        }
    }

    private static int[] parseValues(String[] args) {
        if (args.length == 0) {
            return Arrays.copyOf(DEFAULT_INPUT, DEFAULT_INPUT.length);
        }

        int[] values = new int[args.length];
        for (int index = 0; index < args.length; index++) {
            values[index] = parseInteger(args[index]);
        }

        return values;
    }

    private static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("All arguments must be integers: \"" + value + "\"", exception);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: java -cp . BuildHeapDemo [integer ...]");
    }
}
