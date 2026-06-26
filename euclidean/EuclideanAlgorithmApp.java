package euclidean;

public final class EuclideanAlgorithmApp {

    private static final int DEFAULT_A = 35;
    private static final int DEFAULT_B = 15;

    private EuclideanAlgorithmApp() {
        // Utility class.
    }

    public static void main(String[] args) {
        Operands operands = parseOperands(args);
        System.out.println(EuclideanAlgorithm.gcd(operands.left(), operands.right()));
    }

    static Operands parseOperands(String[] args) {
        if (args.length == 0) {
            return new Operands(DEFAULT_A, DEFAULT_B);
        }
        if (args.length != 2) {
            throw new IllegalArgumentException(
                "Expected either zero arguments or two integers: <a> <b>"
            );
        }

        return new Operands(
            parseIntArg(args[0], "first"),
            parseIntArg(args[1], "second")
        );
    }

    private static int parseIntArg(String value, String label) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + label + " integer: " + value, ex);
        }
    }

    record Operands(int left, int right) {}
}
