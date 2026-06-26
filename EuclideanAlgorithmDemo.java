public final class EuclideanAlgorithmDemo {

    private static final int EXAMPLE_FIRST_NUMBER = 35;
    private static final int EXAMPLE_SECOND_NUMBER = 15;

    private EuclideanAlgorithmDemo() {
    }

    public static void main(String[] args) {
        System.out.println(exampleGcd());
    }

    private static int exampleGcd() {
        return EuclideanAlgorithm.gcd(EXAMPLE_FIRST_NUMBER, EXAMPLE_SECOND_NUMBER);
    }
}
