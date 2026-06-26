public final class EuclideanAlgorithm {

    private static final int EXAMPLE_FIRST_NUMBER = 35;
    private static final int EXAMPLE_SECOND_NUMBER = 15;

    private EuclideanAlgorithm() {
    }

    public static int gcd(int firstNumber, int secondNumber) {
        return gcdOfNonNegativeNumbers(Math.abs(firstNumber), Math.abs(secondNumber));
    }

    private static int gcdOfNonNegativeNumbers(int divisor, int dividend) {
        while (divisor != 0) {
            final int remainder = dividend % divisor;
            dividend = divisor;
            divisor = remainder;
        }

        return dividend;
    }

    static int findGCD(int firstNumber, int secondNumber) {
        return gcd(firstNumber, secondNumber);
    }

    private static int exampleGcd() {
        return gcd(EXAMPLE_FIRST_NUMBER, EXAMPLE_SECOND_NUMBER);
    }

    public static void main(String[] args) {
        System.out.println(exampleGcd());
    }
}
