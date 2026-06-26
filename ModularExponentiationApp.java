public final class ModularExponentiationApp {
    private static final int DEFAULT_BASE = 3;
    private static final int DEFAULT_EXPONENT = 2;
    private static final int DEFAULT_MODULUS = 4;

    private ModularExponentiationApp() {
        // Application entrypoint only.
    }

    public static void main(String[] args) {
        int[] values = parseArguments(args);
        int base = values[0];
        int exponent = values[1];
        int modulus = values[2];

        System.out.println(ModularExponentiation.powMod(base, exponent, modulus));
    }

    private static int[] parseArguments(String[] args) {
        if (args.length == 0) {
            return new int[] {DEFAULT_BASE, DEFAULT_EXPONENT, DEFAULT_MODULUS};
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(
                "Usage: java ModularExponentiationApp [base exponent modulus]"
            );
        }

        return new int[] {
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2])
        };
    }
}
