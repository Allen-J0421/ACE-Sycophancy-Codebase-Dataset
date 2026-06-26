final class ModularExponentiation {
    private static final int MULTIPLICATIVE_IDENTITY = 1;

    private static final int DEMO_BASE = 3;
    private static final int DEMO_EXPONENT = 2;
    private static final int DEMO_MODULUS = 4;

    private ModularExponentiation() {
    }

    public static int powMod(int base, int exponent, int modulus) {
        int result = MULTIPLICATIVE_IDENTITY;
        int basePower = base;
        int remainingExponent = exponent;

        while (remainingExponent > 0) {
            if (isOdd(remainingExponent)) {
                result = multiplyMod(result, basePower, modulus);
            }

            basePower = squareMod(basePower, modulus);
            remainingExponent /= 2;
        }

        return result;
    }

    private static boolean isOdd(int value) {
        return (value & 1) == 1;
    }

    private static int squareMod(int value, int modulus) {
        return multiplyMod(value, value, modulus);
    }

    private static int multiplyMod(int left, int right, int modulus) {
        return (int) ((1L * left * right) % modulus);
    }

    public static void main(String[] args) {
        System.out.println(powMod(DEMO_BASE, DEMO_EXPONENT, DEMO_MODULUS));
    }
}
