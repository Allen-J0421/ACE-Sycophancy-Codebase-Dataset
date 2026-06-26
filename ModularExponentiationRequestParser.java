public final class ModularExponentiationRequestParser {
    private static final int DEFAULT_BASE = 3;
    private static final int DEFAULT_EXPONENT = 2;
    private static final int DEFAULT_MODULUS = 4;

    private ModularExponentiationRequestParser() {
        // Parser utility.
    }

    public static ModularExponentiationRequest parse(String[] args) {
        if (args.length == 0) {
            return defaultRequest();
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(
                "Usage: java ModularExponentiationApp [base exponent modulus]"
            );
        }

        return ModularExponentiationRequest.of(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2])
        );
    }

    private static ModularExponentiationRequest defaultRequest() {
        return ModularExponentiationRequest.of(DEFAULT_BASE, DEFAULT_EXPONENT, DEFAULT_MODULUS);
    }
}
