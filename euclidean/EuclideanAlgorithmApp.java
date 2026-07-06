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
                .observer((a, b, result) -> {
                    if (result instanceof Result.Success<?, ?> s) {
                        System.err.println("gcd(" + a + ", " + b + ") = " + s.value());
                    } else if (result instanceof Result.Failure<?, ?> f) {
                        System.err.println("gcd(" + a + ", " + b + ") failed: " + ((GcdError) f.error()).reason());
                    }
                })
                .build(operands);
        Result<Integer, GcdError> result = command.execute();
        if (result instanceof Result.Success<?, ?> s) {
            System.out.println(s.value());
        } else if (result instanceof Result.Failure<?, ?> f) {
            System.err.println("Error: " + ((GcdError) f.error()).reason());
            System.exit(1);
        }
    }
}
