import java.io.PrintStream;

public final class EuclideanAlgorithms {
    private static final long DEFAULT_FIRST_OPERAND = 35;
    private static final long DEFAULT_SECOND_OPERAND = 15;

    private EuclideanAlgorithms() {
        // Utility class.
    }

    public static long gcd(long first, long second) {
        long left = Math.abs(first);
        long right = Math.abs(second);

        if (left == 0 && right == 0) {
            throw new IllegalArgumentException("At least one operand must be non-zero.");
        }

        while (right != 0) {
            long remainder = left % right;
            left = right;
            right = remainder;
        }

        return left;
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            if (isHelpRequest(args)) {
                out.println(usage());
                return 0;
            }

            Operands operands = parseOperands(args);
            out.println(gcd(operands.first(), operands.second()));
            return 0;
        } catch (IllegalArgumentException exception) {
            err.println(exception.getMessage());
            err.println(usage());
            return 1;
        }
    }

    private static boolean isHelpRequest(String[] args) {
        return args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]));
    }

    private static Operands parseOperands(String[] args) {
        if (args.length == 0) {
            return new Operands(DEFAULT_FIRST_OPERAND, DEFAULT_SECOND_OPERAND);
        }

        if (args.length != 2) {
            throw new IllegalArgumentException("Expected either zero arguments or exactly two integers.");
        }

        return new Operands(parseLong(args[0]), parseLong(args[1]));
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer: " + value, exception);
        }
    }

    private static String usage() {
        return "Usage: java EuclideanAlgorithms [first second]\n"
            + "Prints the greatest common divisor of two integers.\n"
            + "When no arguments are provided, defaults to "
            + DEFAULT_FIRST_OPERAND
            + " and "
            + DEFAULT_SECOND_OPERAND
            + ".";
    }
}

record Operands(long first, long second) {}
