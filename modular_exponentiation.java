final class ModularExponentiation {
    private static final int DEMO_BASE = 3;
    private static final int DEMO_EXPONENT = 2;
    private static final int DEMO_MODULUS = 4;

    private ModularExponentiation() {
    }

    public static int powMod(int base, int exponent, int modulus) {
        int result = 1;
        int basePower = base;

        while (exponent > 0) {
            if (isOdd(exponent)) {
                result = multiplyMod(result, basePower, modulus);
            }

            basePower = multiplyMod(basePower, basePower, modulus);
            exponent /= 2;
        }

        return result;
    }

    private static boolean isOdd(int value) {
        return (value & 1) == 1;
    }

    private static int multiplyMod(int left, int right, int modulus) {
        return (int) ((1L * left * right) % modulus);
    }

    public static void main(String[] args) {
        System.out.println(powMod(DEMO_BASE, DEMO_EXPONENT, DEMO_MODULUS));
    }
}
