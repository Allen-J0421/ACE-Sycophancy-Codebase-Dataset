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

        // Observer receives Result<Integer, GcdError> for both success and failure outcomes.
        int[] observedArgs = {Integer.MIN_VALUE, Integer.MIN_VALUE};
        Object[] observedResult = {null};
        GcdProvider logging = new LoggingGcdProvider(
                GcdProviderRegistry.get("iterative"),
                (a, b, result) -> { observedArgs[0] = a; observedArgs[1] = b; observedResult[0] = result; });
        assertCommand(() -> logging.compute(35, 15), 5);
        @SuppressWarnings("unchecked")
        int capturedValue = ((Result<Integer, GcdError>) observedResult[0]).fold(
                v -> v,
                e -> { throw new AssertionError("Observer captured Failure: " + e.reason()); });
        if (observedArgs[0] != 35 || observedArgs[1] != 15 || capturedValue != 5) {
            throw new AssertionError("Observer received wrong values: args=("
                    + observedArgs[0] + ", " + observedArgs[1] + ") value=" + capturedValue);
        }

        // Observer is called with Failure — no special-case suppression.
        Object[] failureObserved = {null};
        GcdProvider loggingOverflow = new LoggingGcdProvider(
                GcdProviderRegistry.get("iterative"),
                (a, b, result) -> failureObserved[0] = result);
        assertFailure(loggingOverflow, Integer.MIN_VALUE, 0);
        @SuppressWarnings("unchecked")
        Object __ = ((Result<Integer, GcdError>) failureObserved[0]).fold(
                v -> { throw new AssertionError("Observer captured Success, expected Failure: " + v); },
                e -> null);

        // Builder: default, named provider, direct provider injection.
        assertCommand(new GcdCommandBuilder().build(new Operands(35, 15)), 5);
        assertCommand(new GcdCommandBuilder().provider("recursive").build(new Operands(35, 15)), 5);
        assertCommand(new GcdCommandBuilder()
                .provider(GcdProviderRegistry.get("iterative"))
                .build(new Operands(-42, 56)), 14);

        // Builder: observer wiring through Result<Integer, GcdError>.
        Object[] builderObserved = {null};
        assertCommand(new GcdCommandBuilder()
                .provider("iterative")
                .observer((a, b, result) -> builderObserved[0] = result)
                .build(new Operands(35, 15)), 5);
        @SuppressWarnings("unchecked")
        int capturedBuilderValue = ((Result<Integer, GcdError>) builderObserved[0]).fold(
                v -> v,
                e -> { throw new AssertionError("Builder observer captured Failure: " + e.reason()); });
        if (capturedBuilderValue != 5) {
            throw new AssertionError("Builder observer received wrong result: " + capturedBuilderValue);
        }
    }

    private static void assertGcd(GcdProvider provider, int left, int right, int expected) {
        int actual = provider.compute(left, right).fold(
                v -> v,
                e -> { throw new AssertionError("gcd(" + left + ", " + right + ") failed: " + e.reason()); });
        if (actual != expected) {
            throw new AssertionError("gcd(" + left + ", " + right + ") = " + actual + "; expected " + expected);
        }
    }

    private static void assertFailure(GcdProvider provider, int a, int b) {
        provider.compute(a, b).fold(
                v -> { throw new AssertionError("Expected Failure from gcd(" + a + ", " + b + ") but got: " + v); },
                e -> null);
    }

    private static void assertOperands(int left, int right, Operands op) {
        if (op.left() != left || op.right() != right) {
            throw new AssertionError(
                    "Expected (" + left + ", " + right + ") but got (" + op.left() + ", " + op.right() + ")");
        }
    }

    private static void assertCommand(Command<Result<Integer, GcdError>> command, int expected) {
        int actual = command.execute().fold(
                v -> v,
                e -> { throw new AssertionError("Command failed: " + e.reason()); });
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
