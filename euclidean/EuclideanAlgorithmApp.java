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
        Command<GcdResult> command = new GcdCommandBuilder()
                .observer((a, b, result) -> {
                    if (result instanceof GcdResult.Success s) {
                        System.err.println("gcd(" + a + ", " + b + ") = " + s.value());
                    } else if (result instanceof GcdResult.Failure f) {
                        System.err.println("gcd(" + a + ", " + b + ") failed: " + f.reason());
                    }
                })
                .build(operands);
        GcdResult result = command.execute();
        if (result instanceof GcdResult.Success s) {
            System.out.println(s.value());
        } else if (result instanceof GcdResult.Failure f) {
            System.err.println("Error: " + f.reason());
            System.exit(1);
        }
    }
}
