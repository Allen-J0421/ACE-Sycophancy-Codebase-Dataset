package euclidean;

public final class EuclideanAlgorithmTest {

    record GcdCase(int left, int right, int expected) {}

    private EuclideanAlgorithmTest() {}

    public static void main(String[] args) {
        GcdCase[] cases = {
            new GcdCase(35, 15, 5),
            new GcdCase(-42, 56, 14),
            new GcdCase(0, 9, 9),
            new GcdCase(9, 0, 9),
            new GcdCase(0, 0, 0),
        };
        for (GcdCase c : cases) {
            assertGcd(c.left(), c.right(), c.expected());
        }

        assertOperands(35, 15, EuclideanAlgorithmApp.parseOperands(new String[0]));
        assertOperands(-42, 56, EuclideanAlgorithmApp.parseOperands(new String[]{"-42", "56"}));

        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[]{"7"}));
        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[]{"7", "8x"}));

        assertOverflow(Integer.MIN_VALUE, 0);

        assertCommand(new GcdCommand(new Operands(35, 15)), 5);
        assertCommand(new GcdCommand(new Operands(-42, 56)), 14);
        assertCommand(() -> EuclideanAlgorithm.gcd(12, 8), 4);
    }

    private static void assertGcd(int left, int right, int expected) {
        int actual = EuclideanAlgorithm.gcd(left, right);
        if (actual != expected) {
            throw new AssertionError(
                    "gcd(" + left + ", " + right + ") = " + actual + "; expected " + expected);
        }
    }

    private static void assertOverflow(int a, int b) {
        try {
            EuclideanAlgorithm.gcd(a, b);
        } catch (ArithmeticException e) {
            return;
        }
        throw new AssertionError("Expected ArithmeticException from gcd(" + a + ", " + b + ")");
    }

    private static void assertOperands(int left, int right, Operands op) {
        if (op.left() != left || op.right() != right) {
            throw new AssertionError(
                    "Expected (" + left + ", " + right + ") but got (" + op.left() + ", " + op.right() + ")");
        }
    }

    private static void assertCommand(Command<Integer> command, int expected) {
        int actual = command.execute();
        if (actual != expected) {
            throw new AssertionError("Command.execute() = " + actual + "; expected " + expected);
        }
    }

    private static void assertIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException but call completed normally");
    }
}
