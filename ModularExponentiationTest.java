class ModularExponentiationTest {
    record Case(String label, int base, int exp, int mod, int expected) {}

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

    public static void main(String[] args) {
        int passed = 0;
        for (Case c : CASES) {
            int actual = ModularExponentiation.powMod(c.base(), c.exp(), c.mod());
            if (actual != c.expected()) {
                throw new AssertionError(
                    c.label() + ": powMod(" + c.base() + ", " + c.exp() + ", " + c.mod() + ")"
                    + " expected " + c.expected() + " but got " + actual);
            }
            passed++;
        }
        System.out.println("All " + passed + " tests passed.");
    }
}
