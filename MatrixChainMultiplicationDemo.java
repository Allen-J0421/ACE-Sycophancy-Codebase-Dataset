public final class MatrixChainMultiplicationDemo {

    private static final int[] SAMPLE_DIMENSIONS = { 2, 1, 3, 4 };

    private MatrixChainMultiplicationDemo() {
    }

    public static void main(String[] args) {
        System.out.println(MatrixChainMultiplication.minimumMultiplicationCost(SAMPLE_DIMENSIONS));
    }
}
