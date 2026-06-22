/**
 * Modular exponentiation: computes {@code base^exponent mod modulus} efficiently
 * using binary (square-and-multiply) exponentiation, which runs in
 * O(log exponent) multiplications rather than O(exponent).
 */
public final class ModularExponentiation {

    private ModularExponentiation() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Computes {@code (base^exponent) mod modulus}.
     *
     * <p>All intermediate products are kept in {@code long} arithmetic to avoid
     * 32-bit overflow, and the base is normalized into {@code [0, modulus)} so a
     * negative base yields a non-negative result, consistent with the
     * mathematical definition of the modulo operation.
     *
     * @param base     the base; may be negative
     * @param exponent the exponent; must be non-negative
     * @param modulus  the modulus; must be positive
     * @return {@code (base^exponent) mod modulus}, always in {@code [0, modulus)}
     * @throws IllegalArgumentException if {@code modulus <= 0} or {@code exponent < 0}
     */
    public static int powMod(int base, int exponent, int modulus) {
        if (modulus <= 0) {
            throw new IllegalArgumentException("modulus must be positive, got " + modulus);
        }
        if (exponent < 0) {
            throw new IllegalArgumentException("exponent must be non-negative, got " + exponent);
        }

        // Normalize the base into [0, modulus). Handles modulus == 1 (everything
        // collapses to 0) and negative bases in one step.
        long result = 1 % modulus;
        long b = ((base % modulus) + modulus) % modulus;
        int e = exponent;

        while (e > 0) {
            if ((e & 1) == 1) {
                result = (result * b) % modulus;
            }
            b = (b * b) % modulus;
            e >>>= 1;
        }

        return (int) result;
    }

    public static void main(String[] args) {
        // 3^2 mod 4 = 9 mod 4 = 1
        System.out.println(powMod(3, 2, 4));
    }
}
