package euclidean;

public final class EuclideanAlgorithmTest {

    private EuclideanAlgorithmTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        GcdCase[] gcdCases = {
            new GcdCase(35, 15, 5),
            new GcdCase(-42, 56, 14),
            new GcdCase(0, 9, 9),
            new GcdCase(9, 0, 9),
            new GcdCase(0, 0, 0)
        };

        for (GcdCase testCase : gcdCases) {
            assertGcd(testCase.left, testCase.right, testCase.expected);
        }

        assertOperands(35, 15, EuclideanAlgorithmApp.parseOperands(new String[0]));
        assertOperands(-42, 56, EuclideanAlgorithmApp.parseOperands(new String[] { "-42", "56" }));
        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[] { "7" }));
        assertIllegalArgument(() -> EuclideanAlgorithmApp.parseOperands(new String[] { "7", "8x" }));
        assertOverflow(Integer.MIN_VALUE, 0);
    }

    private static void assertGcd(int a, int b, int expected) {
        int actual = EuclideanAlgorithm.gcd(a, b);
        if (actual != expected) {
            throw new AssertionError(
                "gcd(" + a + ", " + b + ") expected " + expected + " but was " + actual
            );
        }
    }

    private static void assertOverflow(int a, int b) {
        try {
            EuclideanAlgorithm.gcd(a, b);
        } catch (ArithmeticException expected) {
            return;
        }

        throw new AssertionError(
            "gcd(" + a + ", " + b + ") was expected to overflow but completed normally"
        );
    }

    private static void assertOperands(
        int expectedLeft,
        int expectedRight,
        EuclideanAlgorithmApp.Operands actual
    ) {
        if (actual.left() != expectedLeft || actual.right() != expectedRight) {
            throw new AssertionError(
                "Expected operands (" + expectedLeft + ", " + expectedRight + ") but was ("
                    + actual.left() + ", " + actual.right() + ")"
            );
        }
    }

    private static void assertIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException but call completed normally");
    }

    private record GcdCase(int left, int right, int expected) {}
}
