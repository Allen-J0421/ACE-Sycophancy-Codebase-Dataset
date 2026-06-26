record ModularExponentiationInput(int base, int exponent, int modulus) {
    private static final String USAGE = "Expected either no arguments or: <base> <exponent> <modulus>.";
    private static final ModularExponentiationInput SAMPLE = new ModularExponentiationInput(3, 2, 4);

    ModularExponentiationInput {
        validateExponent(exponent);
        validateModulus(modulus);
    }

    static ModularExponentiationInput fromArgs(String[] args) {
        if (args.length == 0) {
            return SAMPLE;
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(USAGE);
        }

        return new ModularExponentiationInput(
                parseArgument(args[0], "base"),
                parseArgument(args[1], "exponent"),
                parseArgument(args[2], "modulus"));
    }

    private static void validateExponent(int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }

    private static void validateModulus(int modulus) {
        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
    }

    private static int parseArgument(String rawValue, String name) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for " + name + ": " + rawValue, exception);
        }
    }
}
