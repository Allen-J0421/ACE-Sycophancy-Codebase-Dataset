public final class ModularExponentiationRequestParser {
    private static final int ARGUMENT_COUNT = 3;
    private static final int DEFAULT_BASE = 3;
    private static final int DEFAULT_EXPONENT = 2;
    private static final int DEFAULT_MODULUS = 4;
    private static final String USAGE = "Usage: java ModularExponentiationApp [base exponent modulus]";

    private ModularExponentiationRequestParser() {
        // Parser utility.
    }

    public static ModularExponentiationRequest parse(String[] args) {
        if (args.length == 0) {
            return defaultRequest();
        }

        if (args.length != ARGUMENT_COUNT) {
            throw new IllegalArgumentException(USAGE);
        }

        return new ModularExponentiationRequest(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2])
        );
    }

    private static ModularExponentiationRequest defaultRequest() {
        return new ModularExponentiationRequest(DEFAULT_BASE, DEFAULT_EXPONENT, DEFAULT_MODULUS);
    }
}
