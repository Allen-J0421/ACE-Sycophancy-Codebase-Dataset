public final class EuclideanAlgorithm {

    private static final int EXAMPLE_FIRST_NUMBER = 35;
    private static final int EXAMPLE_SECOND_NUMBER = 15;

    private EuclideanAlgorithm() {
    }

    public static int gcd(int firstNumber, int secondNumber) {
        return gcdOfNonNegativeNumbers(Math.abs(firstNumber), Math.abs(secondNumber));
    }

    private static int gcdOfNonNegativeNumbers(int a, int b) {
        while (a != 0) {
            final int remainder = b % a;
            b = a;
            a = remainder;
        }

        return b;
    }

    static int findGCD(int firstNumber, int secondNumber) {
        return gcd(firstNumber, secondNumber);
    }

    public static void main(String[] args) {
        final int greatestCommonDivisor = gcd(EXAMPLE_FIRST_NUMBER, EXAMPLE_SECOND_NUMBER);

        System.out.println(greatestCommonDivisor);
    }
}
