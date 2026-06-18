import java.util.Arrays;

public final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
    }

    static int matrixMultiplication(int[] dimensions) {
        return minimumMultiplicationCost(dimensions);
    }

    public static int minimumMultiplicationCost(int[] dimensions) {
        MatrixDimensions matrixDimensions = MatrixDimensions.from(dimensions);

        CostTable minimumCosts = buildMinimumCostTable(matrixDimensions);
        return minimumCosts.get(0, matrixDimensions.lastIndex());
    }

    private static CostTable buildMinimumCostTable(MatrixDimensions dimensions) {
        int dimensionCount = dimensions.count();
        CostTable minimumCosts = new CostTable(dimensionCount);

        for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
            fillCostsForChainLength(dimensions, minimumCosts, chainLength);
        }

        return minimumCosts;
    }

    private static void fillCostsForChainLength(
            MatrixDimensions dimensions,
            CostTable minimumCosts,
            int chainLength) {
        for (int start = 0; start < dimensions.count() - chainLength; start++) {
            int end = start + chainLength;
            minimumCosts.set(start, end, minimumCostForRange(dimensions, minimumCosts, start, end));
        }
    }

    private static int minimumCostForRange(
            MatrixDimensions dimensions,
            CostTable minimumCosts,
            int start,
            int end) {
        int minimumCost = Integer.MAX_VALUE;

        for (int split = start + 1; split < end; split++) {
            int cost = costForSplit(dimensions, minimumCosts, start, split, end);
            minimumCost = Math.min(minimumCost, cost);
        }

        return minimumCost;
    }

    private static int costForSplit(
            MatrixDimensions dimensions,
            CostTable minimumCosts,
            int start,
            int split,
            int end) {
        return minimumCosts.get(start, split)
                + minimumCosts.get(split, end)
                + dimensions.get(start) * dimensions.get(split) * dimensions.get(end);
    }

    private static final class MatrixDimensions {
        private static final int MINIMUM_DIMENSION_COUNT = 2;

        private final int[] values;

        private MatrixDimensions(int[] values) {
            this.values = values;
        }

        private static MatrixDimensions from(int[] values) {
            validate(values);
            return new MatrixDimensions(Arrays.copyOf(values, values.length));
        }

        private static void validate(int[] values) {
            if (values == null) {
                throw new IllegalArgumentException("Dimensions must not be null.");
            }
            if (values.length < MINIMUM_DIMENSION_COUNT) {
                throw new IllegalArgumentException("At least two dimensions are required.");
            }

            for (int value : values) {
                if (value <= 0) {
                    throw new IllegalArgumentException("Dimensions must be positive.");
                }
            }
        }

        private int count() {
            return values.length;
        }

        private int get(int index) {
            return values[index];
        }

        private int lastIndex() {
            return values.length - 1;
        }
    }

    private static final class CostTable {
        private final int[][] costs;

        private CostTable(int dimensionCount) {
            costs = new int[dimensionCount][dimensionCount];
        }

        private int get(int start, int end) {
            return costs[start][end];
        }

        private void set(int start, int end, int cost) {
            costs[start][end] = cost;
        }
    }
}
