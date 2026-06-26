public final class ModularExponentiationRequest {
    private static final int DEFAULT_BASE = 3;
    private static final int DEFAULT_EXPONENT = 2;
    private static final int DEFAULT_MODULUS = 4;

    private final int base;
    private final int exponent;
    private final int modulus;

    private ModularExponentiationRequest(int base, int exponent, int modulus) {
        this.base = base;
        this.exponent = exponent;
        this.modulus = modulus;
    }

    public static ModularExponentiationRequest fromArgs(String[] args) {
        if (args.length == 0) {
            return defaults();
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(
                "Usage: java ModularExponentiationApp [base exponent modulus]"
            );
        }

        return new ModularExponentiationRequest(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2])
        );
    }

    private static ModularExponentiationRequest defaults() {
        return new ModularExponentiationRequest(DEFAULT_BASE, DEFAULT_EXPONENT, DEFAULT_MODULUS);
    }

    public int base() {
        return base;
    }

    public int exponent() {
        return exponent;
    }

    public int modulus() {
        return modulus;
    }
}
