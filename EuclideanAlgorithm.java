public final class EuclideanAlgorithm {

    private EuclideanAlgorithm() {
    }

    public static void main(String[] args) {
        EuclideanAlgorithmDemo.main(args);
    }

    public static int gcd(int firstNumber, int secondNumber) {
        return gcdOfNonNegativeNumbers(Math.abs(firstNumber), Math.abs(secondNumber));
    }

    static int findGCD(int firstNumber, int secondNumber) {
        return gcd(firstNumber, secondNumber);
    }

    private static int gcdOfNonNegativeNumbers(int divisor, int dividend) {
        while (divisor != 0) {
            final int remainder = dividend % divisor;
            dividend = divisor;
            divisor = remainder;
        }

        return dividend;
    }
}
