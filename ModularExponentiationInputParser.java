final class ModularExponentiationInputParser {
    private static final String USAGE = "Expected either no arguments or: <base> <exponent> <modulus>.";
    private static final ModularExponentiationInput SAMPLE_INPUT =
            new ModularExponentiationInput(3, 2, 4);

    private ModularExponentiationInputParser() {
    }

    static ModularExponentiationInput parse(String[] args) {
        if (args.length == 0) {
            return SAMPLE_INPUT;
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(USAGE);
        }

        return new ModularExponentiationInput(
                parseInt(args[0], "base"),
                parseInt(args[1], "exponent"),
                parseInt(args[2], "modulus"));
    }

    private static int parseInt(String rawValue, String name) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for " + name + ": " + rawValue, exception);
        }
    }
}
