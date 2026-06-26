public final class ModularExponentiationApp {
    private static final int DEFAULT_BASE = 3;
    private static final int DEFAULT_EXPONENT = 2;
    private static final int DEFAULT_MODULUS = 4;

    private ModularExponentiationApp() {
        // Application entrypoint only.
    }

    public static void main(String[] args) {
        Input input = parseArguments(args);

        System.out.println(
            ModularExponentiation.powMod(input.base, input.exponent, input.modulus)
        );
    }

    private static Input parseArguments(String[] args) {
        if (args.length == 0) {
            return new Input(DEFAULT_BASE, DEFAULT_EXPONENT, DEFAULT_MODULUS);
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(
                "Usage: java ModularExponentiationApp [base exponent modulus]"
            );
        }

        return new Input(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2])
        );
    }

    private static final class Input {
        private final int base;
        private final int exponent;
        private final int modulus;

        private Input(int base, int exponent, int modulus) {
            this.base = base;
            this.exponent = exponent;
            this.modulus = modulus;
        }
    }
}
