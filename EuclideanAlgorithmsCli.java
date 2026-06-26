import java.io.PrintStream;
import java.math.BigInteger;

final class EuclideanAlgorithmsCli {
    private static final BigInteger DEFAULT_FIRST_OPERAND = BigInteger.valueOf(35);
    private static final BigInteger DEFAULT_SECOND_OPERAND = BigInteger.valueOf(15);
    private static final String INVALID_ARGUMENT_COUNT_MESSAGE =
        "Expected either zero arguments or exactly two integers.";
    private static final String USAGE_TEXT = "Usage: java EuclideanAlgorithms [first second]\n"
        + "Prints the greatest common divisor of two integers.\n"
        + "When no arguments are provided, defaults to "
        + DEFAULT_FIRST_OPERAND
        + " and "
        + DEFAULT_SECOND_OPERAND
        + ".";

    private EuclideanAlgorithmsCli() {
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
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

    static String usageText() {
        return USAGE_TEXT;
    }

    private static boolean isHelpRequest(String[] args) {
        return args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]));
    }

    private static Operands parseOperands(String[] args) {
        return switch (args.length) {
            case 0 -> new Operands(DEFAULT_FIRST_OPERAND, DEFAULT_SECOND_OPERAND);
            case 2 -> new Operands(parseInteger(args[0]), parseInteger(args[1]));
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
