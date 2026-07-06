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

        GcdProvider[] providers = {EuclideanAlgorithm.iterative(), EuclideanAlgorithm.recursive()};

        for (GcdProvider provider : providers) {
            for (GcdCase c : cases) {
                assertGcd(provider, c.left(), c.right(), c.expected());
            }
            assertOverflow(provider, Integer.MIN_VALUE, 0);
        }

        assertOperands(35, 15, EuclideanAlgorithmApp.parseOperands(new String[0]));
        assertOperands(-42, 56, EuclideanAlgorithmApp.parseOperands(new String[]{"-42", "56"}));

        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[]{"7"}));
        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[]{"7", "8x"}));

        assertCommand(new GcdCommand(new Operands(35, 15), EuclideanAlgorithm.iterative()), 5);
        assertCommand(new GcdCommand(new Operands(35, 15), EuclideanAlgorithm.recursive()), 5);
        assertCommand(new GcdCommand(new Operands(-42, 56), EuclideanAlgorithm.iterative()), 14);
        assertCommand(new GcdCommand(new Operands(-42, 56), EuclideanAlgorithm.recursive()), 14);
        assertCommand(() -> EuclideanAlgorithm.iterative().compute(12, 8), 4);
    }

    private static void assertGcd(GcdProvider provider, int left, int right, int expected) {
        int actual = provider.compute(left, right);
        if (actual != expected) {
            throw new AssertionError(
                    "gcd(" + left + ", " + right + ") = " + actual + "; expected " + expected);
        }
    }

    private static void assertOverflow(GcdProvider provider, int a, int b) {
        try {
            provider.compute(a, b);
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
