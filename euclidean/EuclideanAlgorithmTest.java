package euclidean;

public final class EuclideanAlgorithmTest {

    private EuclideanAlgorithmTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        assertGcd(35, 15, 5);
        assertGcd(-42, 56, 14);
        assertGcd(0, 9, 9);
        assertGcd(9, 0, 9);
        assertGcd(0, 0, 0);
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
}
