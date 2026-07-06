import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ModularExponentiationTest {
    @FunctionalInterface
    interface PowModFunction {
        int apply(int base, int exp, int mod);
    }

    @FunctionalInterface
    interface Assertion<C> {
        void verify(C testCase);

        default Assertion<C> andThen(Assertion<C> next) {
            return c -> { verify(c); next.verify(c); };
        }
    }

    static class TestRunner {
        private final List<Runnable> tests = new ArrayList<>();

        <C> void addAll(C[] cases, Assertion<C> assertion) {
            Arrays.stream(cases)
                  .map(c -> (Runnable) () -> assertion.verify(c))
                  .forEach(tests::add);
        }

        void runAll() {
            tests.forEach(Runnable::run);
            System.out.println("All " + tests.size() + " tests passed.");
        }
    }

    record Case(String label, int base, int exp, int mod, int expected) {}
    record ThrowsCase(String label, int base, int exp, int mod) {}

    private static final PowModFunction FN = ModularExponentiation::powMod;

    private static final Assertion<Case> EQUALS = c -> {
        int actual = FN.apply(c.base(), c.exp(), c.mod());
        if (actual != c.expected()) {
            throw new AssertionError(
                c.label() + ": powMod(" + c.base() + ", " + c.exp() + ", " + c.mod() + ")"
                + " expected " + c.expected() + " but got " + actual);
        }
    };

    private static final Assertion<ThrowsCase> THROWS_ILLEGAL_ARGUMENT = c -> {
        try {
            FN.apply(c.base(), c.exp(), c.mod());
            throw new AssertionError(
                c.label() + ": powMod(" + c.base() + ", " + c.exp() + ", " + c.mod() + ")"
                + " expected IllegalArgumentException but no exception was thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    };

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
        new ThrowsCase("mod zero",     3,  2,  0),
        new ThrowsCase("mod negative", 3,  2, -1),
        new ThrowsCase("exp negative", 3, -1,  5),
    };

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.addAll(CASES, EQUALS);
        runner.addAll(THROWS_CASES, THROWS_ILLEGAL_ARGUMENT);
        runner.runAll();
    }
}
