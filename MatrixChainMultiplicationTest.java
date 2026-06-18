import java.util.Arrays;

public final class MatrixChainMultiplicationTest {

    private static final String SUCCESS_MESSAGE = "All tests passed.";
    private static final CostCase[] VALID_COST_CASES = {
        new CostCase(new int[] { 2, 1, 3, 4 }, 20),
        new CostCase(new int[] { 10, 20 }, 0),
        new CostCase(new int[] { 30, 35, 15, 5, 10, 20, 25 }, 15125),
    };
    private static final int[][] INVALID_DIMENSION_CASES = {
        null,
        new int[] { 10 },
        new int[] { 10, 0, 20 },
    };

    private MatrixChainMultiplicationTest() {
    }

    public static void main(String[] args) {
        for (CostCase testCase : VALID_COST_CASES) {
            assertMinimumCost(testCase);
        }
        for (int[] invalidDimensions : INVALID_DIMENSION_CASES) {
            assertInvalidDimensions(invalidDimensions);
        }

        System.out.println(SUCCESS_MESSAGE);
    }

    private static void assertMinimumCost(CostCase testCase) {
        assertCost(
                "minimumMultiplicationCost",
                MatrixChainMultiplication.minimumMultiplicationCost(testCase.dimensions),
                testCase);
        assertCost(
                "matrixMultiplication",
                MatrixChainMultiplication.matrixMultiplication(testCase.dimensions),
                testCase);
    }

    private static void assertCost(String methodName, int actualCost, CostCase testCase) {
        if (actualCost != testCase.expectedCost) {
            throw new AssertionError(
                    methodName
                            + " expected "
                            + testCase.expectedCost
                            + " for "
                            + Arrays.toString(testCase.dimensions)
                            + " but got "
                            + actualCost
                            + ".");
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

    private static final class CostCase {
        private final int[] dimensions;
        private final int expectedCost;

        private CostCase(int[] dimensions, int expectedCost) {
            this.dimensions = dimensions;
            this.expectedCost = expectedCost;
        }
    }
}
