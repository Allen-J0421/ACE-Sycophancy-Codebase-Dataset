import java.io.PrintStream;

public final class RadixDemo {
    private static final int[] DEFAULT_VALUES = {170, 45, 75, 90, 802, 24, 2, 66};
    private static final String USAGE = "Usage: java RadixDemo [integer ...]";

    private RadixDemo() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            int[] values = args.length == 0 ? DEFAULT_VALUES.clone() : parseValues(args);

            Radix.radixSort(values);
            printValues(values, out);
            return 0;
        } catch (IllegalArgumentException exception) {
            err.println(exception.getMessage());
            err.println(USAGE);
            return 1;
        }
    }

    private static void printValues(int[] values, PrintStream out) {
        String formatted = Radix.format(values);
        if (!formatted.isEmpty()) {
            out.print(formatted + " ");
        }
    }

    static int[] parseValues(String[] args) {
        int[] values = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                values[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid integer: " + args[i], exception);
            }
        }
        return values;
    }
}
