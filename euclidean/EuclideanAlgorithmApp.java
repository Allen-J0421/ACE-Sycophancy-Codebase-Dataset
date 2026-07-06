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
        Command<Result<Integer, GcdError>> command = new GcdCommandBuilder()
                .observer((a, b, result) -> System.err.println(result.<String>fold(
                        v -> "gcd(" + a + ", " + b + ") = " + v,
                        e -> "gcd(" + a + ", " + b + ") failed: " + e.reason())))
                .build(operands);
        command.execute().fold(
                v -> { System.out.println(v); return null; },
                e -> { System.err.println("Error: " + e.reason()); System.exit(1); return null; });
    }
}
