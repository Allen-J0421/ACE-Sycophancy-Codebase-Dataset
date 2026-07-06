final class ModularExponentiation {
    private ModularExponentiation() {}

    public static int powMod(int base, int exp, int mod) {
        if (mod <= 0) throw new IllegalArgumentException("mod must be positive");
        if (exp < 0)  throw new IllegalArgumentException("exp must be non-negative");
        if (mod == 1) return 0;

        long result = 1;
        // Normalize to [0, mod) so negative bases work correctly.
        long b = ((long) base % mod + mod) % mod;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = result * b % mod;
            }
            b = b * b % mod;
            exp >>= 1;
        }

        return (int) result;
    }
}
