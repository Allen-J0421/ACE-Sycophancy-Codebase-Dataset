final class ModularExponentiation {
    private ModularExponentiation() {
    }

    public static int powMod(int base, int exponent, int modulus) {
        validateInputs(exponent, modulus);

        int normalizedBase = normalizeBase(base, modulus);
        int remainingExponent = exponent;
        long result = 1 % modulus;

        while (remainingExponent > 0) {
            if ((remainingExponent & 1) != 0) {
                result = multiplyMod(result, normalizedBase, modulus);
            }

            normalizedBase = multiplyMod(normalizedBase, normalizedBase, modulus);
            remainingExponent >>= 1;
        }

        return (int) result;
    }

    private static void validateInputs(int exponent, int modulus) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }

        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
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

    public static void main(String[] args) {
        int base = 3;
        int exponent = 2;
        int modulus = 4;

        System.out.println(powMod(base, exponent, modulus));
    }
}
