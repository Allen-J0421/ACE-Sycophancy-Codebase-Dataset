import java.io.PrintStream;
import java.math.BigInteger;

public final class EuclideanAlgorithms {
    private static final BigInteger DEFAULT_FIRST_OPERAND = BigInteger.valueOf(35);
    private static final BigInteger DEFAULT_SECOND_OPERAND = BigInteger.valueOf(15);

    private EuclideanAlgorithms() {
        // Utility class.
    }

    public static BigInteger gcd(long first, long second) {
        return gcd(BigInteger.valueOf(first), BigInteger.valueOf(second));
    }

    public static BigInteger gcd(BigInteger first, BigInteger second) {
        return Operands.of(first, second).gcd();
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            CliRequest request = parseRequest(args);
            if (request.helpRequested()) {
                out.println(usageText());
                return 0;
            }

            out.println(request.operands().gcd());
            return 0;
        } catch (IllegalArgumentException exception) {
            err.println(exception.getMessage());
            err.println(usageText());
            return 1;
        }
    }

    static String usageText() {
        return "Usage: java EuclideanAlgorithms [first second]\n"
            + "Prints the greatest common divisor of two integers.\n"
            + "When no arguments are provided, defaults to "
            + DEFAULT_FIRST_OPERAND
            + " and "
            + DEFAULT_SECOND_OPERAND
            + ".";
    }

    private static CliRequest parseRequest(String[] args) {
        if (args.length == 0) {
            return CliRequest.compute(Operands.defaults());
        }

        if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            return CliRequest.help();
        }

        if (args.length != 2) {
            throw new IllegalArgumentException("Expected either zero arguments or exactly two integers.");
        }

        return CliRequest.compute(Operands.of(parseInteger(args[0]), parseInteger(args[1])));
    }

    private static BigInteger parseInteger(String value) {
        try {
            return new BigInteger(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer: " + value, exception);
        }
    }

    private record CliRequest(Operands operands, boolean helpRequested) {
        private static CliRequest compute(Operands operands) {
            return new CliRequest(operands, false);
        }

        private static CliRequest help() {
            return new CliRequest(null, true);
        }
    }

    private record Operands(BigInteger first, BigInteger second) {
        private static Operands defaults() {
            return new Operands(DEFAULT_FIRST_OPERAND, DEFAULT_SECOND_OPERAND);
        }

        private static Operands of(BigInteger first, BigInteger second) {
            return new Operands(
                requireOperand(first, "first"),
                requireOperand(second, "second")
            );
        }

        private static BigInteger requireOperand(BigInteger operand, String name) {
            if (operand == null) {
                throw new IllegalArgumentException(name + " operand must not be null.");
            }

            return operand;
        }

        private BigInteger gcd() {
            BigInteger left = first.abs();
            BigInteger right = second.abs();

            if (left.equals(BigInteger.ZERO) && right.equals(BigInteger.ZERO)) {
                throw new IllegalArgumentException("At least one operand must be non-zero.");
            }

            while (!right.equals(BigInteger.ZERO)) {
                BigInteger remainder = left.remainder(right);
                left = right;
                right = remainder;
            }

            return left;
        }
    }
}
