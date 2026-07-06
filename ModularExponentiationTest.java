class ModularExponentiationTest {
    @FunctionalInterface
    interface PowModFunction {
        int apply(int base, int exp, int mod);
    }

    record Case(String label, int base, int exp, int mod, int expected) {}
    record ThrowsCase(String label, int base, int exp, int mod) {}

    private static final PowModFunction FN = ModularExponentiation::powMod;

    private static final Case[] CASES = {
        new Case("small base",       3,  2,    4,   1),  // 9 % 4
        new Case("large exponent",   2, 10, 1000,  24),  // 1024 % 1000
        new Case("zero exponent",    5,  0,    7,   1),  // base^0 = 1
        new Case("mod one",          6,  1,    1,   0),  // always 0
        new Case("negative base",   -3,  2,    5,   4),  // 9 % 5
        new Case("base equals mod",  4,  3,    4,   0),  // 64 % 4
        new Case("base zero",        0,  5,    7,   0),  // 0^n = 0
        new Case("exp one",          7,  1,   11,   7),  // identity
    };

    private static final ThrowsCase[] THROWS_CASES = {
        new ThrowsCase("mod zero",        3,  2,  0),
        new ThrowsCase("mod negative",    3,  2, -1),
        new ThrowsCase("exp negative",    3, -1,  5),
    };

    public static void main(String[] args) {
        int passed = 0;

        for (Case c : CASES) {
            int actual = FN.apply(c.base(), c.exp(), c.mod());
            if (actual != c.expected()) {
                throw new AssertionError(
                    c.label() + ": powMod(" + c.base() + ", " + c.exp() + ", " + c.mod() + ")"
                    + " expected " + c.expected() + " but got " + actual);
            }
            passed++;
        }

        for (ThrowsCase c : THROWS_CASES) {
            assertThrows(c.label(), FN, c.base(), c.exp(), c.mod());
            passed++;
        }

        System.out.println("All " + passed + " tests passed.");
    }

    private static void assertThrows(String label, PowModFunction fn,
                                     int base, int exp, int mod) {
        try {
            fn.apply(base, exp, mod);
            throw new AssertionError(
                label + ": powMod(" + base + ", " + exp + ", " + mod + ")"
                + " expected IllegalArgumentException but no exception was thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
