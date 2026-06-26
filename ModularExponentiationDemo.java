final class ModularExponentiationDemo {
    private static final String USAGE = "Expected either no arguments or: <base> <exponent> <modulus>.";
    private static final ModularExponentiationInput SAMPLE_INPUT =
            new ModularExponentiationInput(3, 2, 4);

    private ModularExponentiationDemo() {
    }

    public static void main(String[] args) {
        ModularExponentiationInput input = parseInput(args);
        System.out.println(ModularExponentiation.modPow(input));
    }

    private static ModularExponentiationInput parseInput(String[] args) {
        return switch (args.length) {
            case 0 -> SAMPLE_INPUT;
            case 3 -> new ModularExponentiationInput(
                    parseInt(args[0], "base"),
                    parseInt(args[1], "exponent"),
                    parseInt(args[2], "modulus"));
            default -> throw new IllegalArgumentException(USAGE);
        };
    }

    private static int parseInt(String rawValue, String name) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for " + name + ": " + rawValue, exception);
        }
    }
}
