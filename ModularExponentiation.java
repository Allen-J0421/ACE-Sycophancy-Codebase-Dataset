public final class ModularExponentiation {
    private ModularExponentiation() {
    }

    public static int modPow(int base, int exponent, int modulus) {
        return compute(ExponentiationContext.create(base, exponent, modulus));
    }

    private static int compute(ExponentiationContext context) {
        int factor = context.normalizedBase();
        int remainingExponent = context.exponent();
        int modulus = context.modulus();
        long result = 1 % modulus;

        while (remainingExponent > 0) {
            if ((remainingExponent & 1) != 0) {
                result = multiplyMod(result, factor, modulus);
            }

            factor = multiplyMod(factor, factor, modulus);
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

    private record ExponentiationContext(int normalizedBase, int exponent, int modulus) {
        private static ExponentiationContext create(int base, int exponent, int modulus) {
            if (exponent < 0) {
                throw new IllegalArgumentException("Exponent must be non-negative.");
            }

            if (modulus <= 0) {
                throw new IllegalArgumentException("Modulus must be positive.");
            }

            return new ExponentiationContext(normalizeBase(base, modulus), exponent, modulus);
        }
    }
}
