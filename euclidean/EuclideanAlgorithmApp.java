package euclidean;

public final class EuclideanAlgorithmApp {

    private static final int DEFAULT_A = 35;
    private static final int DEFAULT_B = 15;

    private EuclideanAlgorithmApp() {}

    static Operands parseOperands(String[] args) {
        if (args.length == 0) {
            return new Operands(DEFAULT_A, DEFAULT_B);
        }
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Expected either zero arguments or two integers: <a> <b>");
        }
        return ArgumentParser.parseOperands(args[0], args[1]);
    }

    public static void main(String[] args) {
        Operands operands = parseOperands(args);
        GcdProvider provider = new LoggingGcdProvider(
                GcdProviderRegistry.getDefault(),
                (a, b, result) -> System.err.println("gcd(" + a + ", " + b + ") = " + result));
        Command<Integer> command = new GcdCommand(operands, provider);
        System.out.println(command.execute());
    }
}
