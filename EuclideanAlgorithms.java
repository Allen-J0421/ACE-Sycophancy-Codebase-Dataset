import java.io.PrintStream;
import java.math.BigInteger;
public final class EuclideanAlgorithms {
    private static final CommandLine COMMAND_LINE = new CommandLine(
        BigInteger.valueOf(35),
        BigInteger.valueOf(15)
    );

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
        return COMMAND_LINE.run(args, out, err);
    }

    private record Operands(BigInteger first, BigInteger second) {
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

    private static final class CommandLine {
        private final Operands defaultOperands;

        private CommandLine(BigInteger defaultFirstOperand, BigInteger defaultSecondOperand) {
            defaultOperands = Operands.of(defaultFirstOperand, defaultSecondOperand);
        }

        private int run(String[] args, PrintStream out, PrintStream err) {
            try {
                Command command = parse(args);
                return command.execute(out);
            } catch (IllegalArgumentException exception) {
                err.println(exception.getMessage());
                err.println(usage());
                return 1;
            }
        }

        private Command parse(String[] args) {
            if (args.length == 0) {
                return new ComputeCommand(defaultOperands);
            }

            if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
                return new HelpCommand(usage());
            }

            if (args.length != 2) {
                throw new IllegalArgumentException("Expected either zero arguments or exactly two integers.");
            }

            return new ComputeCommand(Operands.of(parseInteger(args[0]), parseInteger(args[1])));
        }

        private BigInteger parseInteger(String value) {
            try {
                return new BigInteger(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid integer: " + value, exception);
            }
        }

        private String usage() {
            return "Usage: java EuclideanAlgorithms [first second]\n"
                + "Prints the greatest common divisor of two integers.\n"
                + "When no arguments are provided, defaults to "
                + defaultOperands.first()
                + " and "
                + defaultOperands.second()
                + ".";
        }
    }

    private sealed interface Command permits ComputeCommand, HelpCommand {
        int execute(PrintStream out);
    }

    private record ComputeCommand(Operands operands) implements Command {
        @Override
        public int execute(PrintStream out) {
            out.println(operands.gcd());
            return 0;
        }
    }

    private record HelpCommand(String usage) implements Command {
        @Override
        public int execute(PrintStream out) {
            out.println(usage);
            return 0;
        }
    }
}
