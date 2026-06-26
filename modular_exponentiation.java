final class ModularExponentiation {
    private static final int DEMO_BASE = 3;
    private static final int DEMO_EXPONENT = 2;
    private static final int DEMO_MODULUS = 4;

    private ModularExponentiation() {
        // Utility class.
    }

    public static int powMod(int base, int exponent, int modulus) {
        validateInputs(exponent, modulus);

        int result = 1;
        int currentBase = base;
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

    public static void main(String[] args) {
        System.out.println(runDemo());
    }

    private static int runDemo() {
        return powMod(DEMO_BASE, DEMO_EXPONENT, DEMO_MODULUS);
    }
}
