public final class ModularExponentiation {
    private ModularExponentiation() {
    }

    public static int modPow(ModularExponentiationInput input) {
        int modulus = input.modulus();
        int normalizedBase = normalizeBase(input.base(), modulus);
        return compute(normalizedBase, input.exponent(), modulus);
    }

    public static int modPow(int base, int exponent, int modulus) {
        return modPow(new ModularExponentiationInput(base, exponent, modulus));
    }

    private static int compute(int base, int exponent, int modulus) {
        int remainingExponent = exponent;
        long result = 1 % modulus;

        while (remainingExponent > 0) {
            if ((remainingExponent & 1) != 0) {
                result = multiplyMod(result, base, modulus);
            }

            base = multiplyMod(base, base, modulus);
            remainingExponent >>= 1;
        }

        return (int) result;
    }

    @Deprecated
    public static int powMod(int base, int exponent, int modulus) {
        return modPow(base, exponent, modulus);
    }

    @Deprecated
    public static int powMod(ModularExponentiationInput input) {
        return modPow(input);
    }

    private static int normalizeBase(int base, int modulus) {
        int normalizedBase = base % modulus;
        if (normalizedBase < 0) {
            normalizedBase += modulus;
        }
        return normalizedBase;
    }

    private static int multiplyMod(long left, long right, int modulus) {
        return (int) ((left * right) % modulus);
    }
}
