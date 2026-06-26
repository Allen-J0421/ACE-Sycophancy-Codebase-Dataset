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
        return Cli.run(args, out, err);
    }

    static String usageText() {
        return Cli.USAGE_TEXT;
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
            if (first.signum() == 0 && second.signum() == 0) {
                throw new IllegalArgumentException("At least one operand must be non-zero.");
            }

            return first.abs().gcd(second.abs());
        }
    }

    private static final class Cli {
        private static final String INVALID_ARGUMENT_COUNT_MESSAGE =
            "Expected either zero arguments or exactly two integers.";
        private static final String USAGE_TEXT = "Usage: java EuclideanAlgorithms [first second]\n"
            + "Prints the greatest common divisor of two integers.\n"
            + "When no arguments are provided, defaults to "
            + DEFAULT_FIRST_OPERAND
            + " and "
            + DEFAULT_SECOND_OPERAND
            + ".";

        private Cli() {
        }

        private static int run(String[] args, PrintStream out, PrintStream err) {
            try {
                if (isHelpRequest(args)) {
                    out.println(USAGE_TEXT);
                    return 0;
                }

                out.println(parseOperands(args).gcd());
                return 0;
            } catch (IllegalArgumentException exception) {
                err.println(exception.getMessage());
                err.println(USAGE_TEXT);
                return 1;
            }
        }

        private static boolean isHelpRequest(String[] args) {
            return args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]));
        }

        private static Operands parseOperands(String[] args) {
            return switch (args.length) {
                case 0 -> Operands.defaults();
                case 2 -> Operands.of(parseInteger(args[0]), parseInteger(args[1]));
                default -> throw new IllegalArgumentException(INVALID_ARGUMENT_COUNT_MESSAGE);
            };
        }

        private static BigInteger parseInteger(String value) {
            try {
                return new BigInteger(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid integer: " + value, exception);
            }
        }
    }
}
