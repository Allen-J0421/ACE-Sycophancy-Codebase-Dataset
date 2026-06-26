class ModularExponentiation {
    private static final int DEMO_BASE = 3;
    private static final int DEMO_EXPONENT = 2;
    private static final int DEMO_MODULUS = 4;

    public static int powMod(int base, int exponent, int modulus) {
        int result = 1;
        int currentBase = base;

        while (exponent >= 1) {
            if ((exponent & 1) == 1) {
                result = multiplyMod(result, currentBase, modulus);
            }

            currentBase = multiplyMod(currentBase, currentBase, modulus);
            exponent /= 2;
        }

        return result;
    }

    private static int multiplyMod(int left, int right, int modulus) {
        return (int) ((1L * left * right) % modulus);
    }

    public static void main(String[] args) {
        System.out.println(powMod(DEMO_BASE, DEMO_EXPONENT, DEMO_MODULUS));
    }
}
