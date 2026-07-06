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
            assertFailure(provider, Integer.MIN_VALUE, 0);
        }

        assertOperands(35, 15, EuclideanAlgorithmApp.parseOperands(new String[0]));
        assertOperands(-42, 56, EuclideanAlgorithmApp.parseOperands(new String[]{"-42", "56"}));

        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[]{"7"}));
        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[]{"7", "8x"}));

        assertCommand(() -> GcdProviderRegistry.getDefault().compute(12, 8), 4);
        assertIllegalArgument(() -> GcdProviderRegistry.get("unknown"));

        // Observer receives GcdResult for both success and failure outcomes.
        int[] observedArgs = {Integer.MIN_VALUE, Integer.MIN_VALUE};
        GcdResult[] observedResult = {null};
        GcdProvider logging = new LoggingGcdProvider(
                GcdProviderRegistry.get("iterative"),
                (a, b, result) -> { observedArgs[0] = a; observedArgs[1] = b; observedResult[0] = result; });
        assertCommand(() -> logging.compute(35, 15), 5);
        if (observedArgs[0] != 35 || observedArgs[1] != 15
                || !(observedResult[0] instanceof GcdResult.Success s) || s.value() != 5) {
            throw new AssertionError("Observer received wrong values: args=("
                    + observedArgs[0] + ", " + observedArgs[1] + ") result=" + observedResult[0]);
        }

        // Observer is now called even on failure — no special-case suppression.
        GcdResult[] failureObserved = {null};
        GcdProvider loggingOverflow = new LoggingGcdProvider(
                GcdProviderRegistry.get("iterative"),
                (a, b, result) -> failureObserved[0] = result);
        assertFailure(loggingOverflow, Integer.MIN_VALUE, 0);
        if (!(failureObserved[0] instanceof GcdResult.Failure)) {
            throw new AssertionError("Observer must be called with Failure when computation overflows");
        }

        // Builder: default, named provider, direct provider injection.
        assertCommand(new GcdCommandBuilder().build(new Operands(35, 15)), 5);
        assertCommand(new GcdCommandBuilder().provider("recursive").build(new Operands(35, 15)), 5);
        assertCommand(new GcdCommandBuilder()
                .provider(GcdProviderRegistry.get("iterative"))
                .build(new Operands(-42, 56)), 14);

        // Builder: observer wiring through GcdResult.
        GcdResult[] builderObserved = {null};
        assertCommand(new GcdCommandBuilder()
                .provider("iterative")
                .observer((a, b, result) -> builderObserved[0] = result)
                .build(new Operands(35, 15)), 5);
        if (!(builderObserved[0] instanceof GcdResult.Success bs) || bs.value() != 5) {
            throw new AssertionError("Builder observer received wrong result: " + builderObserved[0]);
        }
    }

    private static void assertGcd(GcdProvider provider, int left, int right, int expected) {
        GcdResult result = provider.compute(left, right);
        if (!(result instanceof GcdResult.Success s) || s.value() != expected) {
            throw new AssertionError(
                    "gcd(" + left + ", " + right + ") = " + result + "; expected " + expected);
        }
    }

    private static void assertFailure(GcdProvider provider, int a, int b) {
        GcdResult result = provider.compute(a, b);
        if (!(result instanceof GcdResult.Failure)) {
            throw new AssertionError("Expected Failure from gcd(" + a + ", " + b + ") but got: " + result);
        }
    }

    private static void assertOperands(int left, int right, Operands op) {
        if (op.left() != left || op.right() != right) {
            throw new AssertionError(
                    "Expected (" + left + ", " + right + ") but got (" + op.left() + ", " + op.right() + ")");
        }
    }

    private static void assertCommand(Command<GcdResult> command, int expected) {
        GcdResult result = command.execute();
        if (!(result instanceof GcdResult.Success s) || s.value() != expected) {
            throw new AssertionError("Command.execute() = " + result + "; expected " + expected);
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
