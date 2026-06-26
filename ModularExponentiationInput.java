final class ModularExponentiationInput {
    private static final String USAGE = "Expected either no arguments or: <base> <exponent> <modulus>.";
    private static final ModularExponentiationInput SAMPLE =
            new ModularExponentiationInput(3, 2, 4);

    private final int base;
    private final int exponent;
    private final int modulus;

    private ModularExponentiationInput(int base, int exponent, int modulus) {
        this.base = base;
        this.exponent = exponent;
        this.modulus = modulus;
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

    int base() {
        return base;
    }

    int exponent() {
        return exponent;
    }

    int modulus() {
        return modulus;
    }

    private static int parseArgument(String rawValue, String name) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for " + name + ": " + rawValue, exception);
        }
    }
}
