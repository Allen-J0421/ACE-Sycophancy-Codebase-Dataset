class ModularExponentiation {
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

    public static void main(String[] args) {
        assertEq(powMod(3, 2, 4),    1);   // 9 % 4
        assertEq(powMod(2, 10, 1000), 24);  // 1024 % 1000
        assertEq(powMod(5, 0, 7),    1);   // any base^0
        assertEq(powMod(6, 1, 1),    0);   // mod == 1 always yields 0
        assertEq(powMod(-3, 2, 5),   4);   // (-3)^2 % 5 = 9 % 5
        System.out.println("All assertions passed.");
    }

    private static void assertEq(int actual, int expected) {
        if (actual != expected) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }
}
