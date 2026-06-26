final class ModularExponentiationDemo {
    private static final int SAMPLE_BASE = 3;
    private static final int SAMPLE_EXPONENT = 2;
    private static final int SAMPLE_MODULUS = 4;

    private ModularExponentiationDemo() {
    }

    public static void main(String[] args) {
        if (args.length != 0 && args.length != 3) {
            throw new IllegalArgumentException("Expected either no arguments or: <base> <exponent> <modulus>.");
        }

        int base = args.length == 0 ? SAMPLE_BASE : parseArgument(args[0], "base");
        int exponent = args.length == 0 ? SAMPLE_EXPONENT : parseArgument(args[1], "exponent");
        int modulus = args.length == 0 ? SAMPLE_MODULUS : parseArgument(args[2], "modulus");

        System.out.println(ModularExponentiation.modPow(base, exponent, modulus));
    }

    private static int parseArgument(String rawValue, String name) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for " + name + ": " + rawValue, exception);
        }
    }
}
