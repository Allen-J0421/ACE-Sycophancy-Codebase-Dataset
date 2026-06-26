public final class ModularExponentiation {
    private ModularExponentiation() {
        // Utility class.
    }

    public static int powMod(int base, int exponent, int modulus) {
        validateInputs(exponent, modulus);

        int result = 1 % modulus;
        int currentBase = normalizeMod(base, modulus);
        int currentExponent = exponent;

        while (currentExponent > 0) {
            if ((currentExponent & 1) == 1) {
                result = multiplyMod(result, currentBase, modulus);
            }
            currentBase = multiplyMod(currentBase, currentBase, modulus);
            currentExponent >>= 1;
        }

        return result;
    }

    private static void validateInputs(int exponent, int modulus) {
        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }

    private static int multiplyMod(int left, int right, int modulus) {
        return (int) ((1L * left * right) % modulus);
    }

    private static int normalizeMod(int value, int modulus) {
        int normalized = value % modulus;
        return normalized < 0 ? normalized + modulus : normalized;
    }

    public static void main(String[] args) {
        ModularExponentiationApp.main(args);
    }
}
