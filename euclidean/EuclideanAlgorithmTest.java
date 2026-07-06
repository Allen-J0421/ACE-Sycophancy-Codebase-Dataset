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

        GcdProvider[] providers = {GcdProviderRegistry.get("iterative"), GcdProviderRegistry.get("recursive")};

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

        assertCommand(() -> GcdProviderRegistry.getDefault().compute(12, 8), 4);
        assertIllegalArgument(() -> GcdProviderRegistry.get("unknown"));

        int[] observed = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        GcdProvider logging = new LoggingGcdProvider(
                GcdProviderRegistry.get("iterative"),
                (a, b, result) -> { observed[0] = a; observed[1] = b; observed[2] = result; });
        assertCommand(() -> logging.compute(35, 15), 5);
        if (observed[0] != 35 || observed[1] != 15 || observed[2] != 5) {
            throw new AssertionError(
                    "Observer received wrong values: (" + observed[0] + ", " + observed[1] + ") = " + observed[2]);
        }

        boolean[] observerCalled = {false};
        GcdProvider loggingOverflow = new LoggingGcdProvider(
                GcdProviderRegistry.get("iterative"),
                (a, b, result) -> observerCalled[0] = true);
        assertOverflow(loggingOverflow, Integer.MIN_VALUE, 0);
        if (observerCalled[0]) {
            throw new AssertionError("Observer must not be called when delegate throws");
        }

        assertCommand(new GcdCommandBuilder().build(new Operands(35, 15)), 5);
        assertCommand(new GcdCommandBuilder().provider("recursive").build(new Operands(35, 15)), 5);
        assertCommand(new GcdCommandBuilder()
                .provider(GcdProviderRegistry.get("iterative"))
                .build(new Operands(-42, 56)), 14);

        int[] builderObserved = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        assertCommand(new GcdCommandBuilder()
                .provider("iterative")
                .observer((a, b, result) -> { builderObserved[0] = a; builderObserved[1] = b; builderObserved[2] = result; })
                .build(new Operands(35, 15)), 5);
        if (builderObserved[0] != 35 || builderObserved[1] != 15 || builderObserved[2] != 5) {
            throw new AssertionError(
                    "Builder observer received wrong values: (" + builderObserved[0] + ", " + builderObserved[1] + ") = " + builderObserved[2]);
        }
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
