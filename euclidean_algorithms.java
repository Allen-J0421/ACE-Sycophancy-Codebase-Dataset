class EuclideanAlgorithm {

    static int findGCD(int firstNumber, int secondNumber) {
        int a = Math.abs(firstNumber);
        int b = Math.abs(secondNumber);

        while (a != 0) {
            int remainder = b % a;
            b = a;
            a = remainder;
        }

        return b;
    }

    public static void main(String[] args) {
        final int firstNumber = 35;
        final int secondNumber = 15;
        final int greatestCommonDivisor = findGCD(firstNumber, secondNumber);

        System.out.println(greatestCommonDivisor);
    }
}
