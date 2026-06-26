public final class EuclideanAlgorithm {

    private static final int EXAMPLE_FIRST_NUMBER = 35;
    private static final int EXAMPLE_SECOND_NUMBER = 15;

    private EuclideanAlgorithm() {
    }

    static int findGCD(int firstNumber, int secondNumber) {
        int a = Math.abs(firstNumber);
        int b = Math.abs(secondNumber);

        while (a != 0) {
            final int remainder = b % a;
            b = a;
            a = remainder;
        }

        return b;
    }

    public static void main(String[] args) {
        final int greatestCommonDivisor = findGCD(EXAMPLE_FIRST_NUMBER, EXAMPLE_SECOND_NUMBER);

        System.out.println(greatestCommonDivisor);
    }
}
