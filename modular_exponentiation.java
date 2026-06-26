final class ModularExponentiation {
    private ModularExponentiation() {
        // Utility class.
    }

    public static int powMod(int base, int exponent, int modulus) {
        int result = 1;
        int currentBase = base;
        int currentExponent = exponent;

        while (currentExponent > 0) {
            if ((currentExponent & 1) == 1) {
                result = multiplyMod(result, currentBase, modulus);
                currentExponent--;
            } else {
                currentBase = multiplyMod(currentBase, currentBase, modulus);
                currentExponent /= 2;
            }
        }

        return result;
    }

    private static int multiplyMod(int left, int right, int modulus) {
        return (int) ((1L * left * right) % modulus);
    }

    public static void main(String[] args) {
        int base = 3;
        int exponent = 2;
        int modulus = 4;

        System.out.println(powMod(base, exponent, modulus));
    }
}
