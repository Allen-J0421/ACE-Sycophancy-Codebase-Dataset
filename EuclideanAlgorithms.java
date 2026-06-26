public final class EuclideanAlgorithms {
    private static final int DEFAULT_FIRST_OPERAND = 35;
    private static final int DEFAULT_SECOND_OPERAND = 15;

    private EuclideanAlgorithms() {
        // Utility class.
    }

    public static int gcd(int first, int second) {
        int left = Math.abs(first);
        int right = Math.abs(second);

        if (left == 0 && right == 0) {
            throw new IllegalArgumentException("At least one operand must be non-zero.");
        }

        while (right != 0) {
            int remainder = left % right;
            left = right;
            right = remainder;
        }

        return left;
    }

    public static void main(String[] args) {
        int[] operands = parseOperands(args);
        System.out.println(gcd(operands[0], operands[1]));
    }

    private static int[] parseOperands(String[] args) {
        if (args.length == 0) {
            return new int[] {DEFAULT_FIRST_OPERAND, DEFAULT_SECOND_OPERAND};
        }

        if (args.length != 2) {
            throw new IllegalArgumentException("Expected either zero arguments or exactly two integers.");
        }

        return new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1])};
    }
}
