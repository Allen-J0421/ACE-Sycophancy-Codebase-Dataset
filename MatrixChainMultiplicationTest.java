public final class MatrixChainMultiplicationTest {

    private MatrixChainMultiplicationTest() {
    }

    public static void main(String[] args) {
        assertMinimumCost(new int[] { 2, 1, 3, 4 }, 20);
        assertMinimumCost(new int[] { 10, 20 }, 0);
        assertMinimumCost(new int[] { 30, 35, 15, 5, 10, 20, 25 }, 15125);
        assertInvalidDimensions(null);
        assertInvalidDimensions(new int[] { 10 });
        assertInvalidDimensions(new int[] { 10, 0, 20 });

        System.out.println("All tests passed.");
    }

    private static void assertMinimumCost(int[] dimensions, int expectedCost) {
        int actualCost = MatrixChainMultiplication.minimumMultiplicationCost(dimensions);
        if (actualCost != expectedCost) {
            throw new AssertionError("Expected " + expectedCost + " but got " + actualCost + ".");
        }
    }

    private static void assertInvalidDimensions(int[] dimensions) {
        try {
            MatrixChainMultiplication.minimumMultiplicationCost(dimensions);
            throw new AssertionError("Expected invalid dimensions to throw.");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }
}
