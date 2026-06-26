public final class ModularExponentiation {
    private ModularExponentiation() {
    }

    public static int modPow(int base, int exponent, int modulus) {
        validateExponent(exponent);
        validateModulus(modulus);

        int normalizedBase = normalizeBase(base, modulus);
        return compute(normalizedBase, exponent, modulus);
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

    private static void validateExponent(int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }

    private static void validateModulus(int modulus) {
        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
    }
}
