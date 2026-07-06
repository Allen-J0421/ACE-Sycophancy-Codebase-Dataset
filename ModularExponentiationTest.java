class ModularExponentiationTest {
    public static void main(String[] args) {
        assertEq("3^2 % 4",     ModularExponentiation.powMod(3, 2, 4),    1);
        assertEq("2^10 % 1000", ModularExponentiation.powMod(2, 10, 1000), 24);
        assertEq("5^0 % 7",     ModularExponentiation.powMod(5, 0, 7),    1);
        assertEq("6^1 % 1",     ModularExponentiation.powMod(6, 1, 1),    0);
        assertEq("(-3)^2 % 5",  ModularExponentiation.powMod(-3, 2, 5),   4);
        System.out.println("All tests passed.");
    }

    private static void assertEq(String label, int actual, int expected) {
        if (actual != expected) {
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
        }
    }
}
