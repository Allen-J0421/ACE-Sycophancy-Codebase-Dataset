import java.util.Arrays;

public final class MatrixChainMultiplication {

    private static final int FIRST_MULTIPLIABLE_CHAIN_LENGTH = 2;

    private MatrixChainMultiplication() {
    }

    static int matrixMultiplication(int[] dimensions) {
        return minimumMultiplicationCost(dimensions);
    }

    public static int minimumMultiplicationCost(int[] dimensions) {
        MatrixDimensions matrixDimensions = MatrixDimensions.from(dimensions);

        CostTable minimumCosts = buildMinimumCostTable(matrixDimensions);
        return minimumCosts.get(ChainRange.fromStartAndEnd(0, matrixDimensions.lastIndex()));
    }

    private static CostTable buildMinimumCostTable(MatrixDimensions dimensions) {
        int dimensionCount = dimensions.count();
        CostTable minimumCosts = new CostTable(dimensionCount);

        for (int chainLength = FIRST_MULTIPLIABLE_CHAIN_LENGTH;
                chainLength < dimensionCount;
                chainLength++) {
            fillCostsForChainLength(dimensions, minimumCosts, chainLength);
        }

        return minimumCosts;
    }

    private static void fillCostsForChainLength(
            MatrixDimensions dimensions,
            CostTable minimumCosts,
            int chainLength) {
        for (int start = 0; start < dimensions.count() - chainLength; start++) {
            ChainRange range = ChainRange.fromStartAndLength(start, chainLength);
            minimumCosts.set(range, minimumCostForRange(dimensions, minimumCosts, range));
        }
    }

    private static int minimumCostForRange(
            MatrixDimensions dimensions,
            CostTable minimumCosts,
            ChainRange range) {
        int minimumCost = Integer.MAX_VALUE;

        for (int split = range.firstSplit(); split < range.end(); split++) {
            int cost = costForSplit(dimensions, minimumCosts, range, split);
            minimumCost = Math.min(minimumCost, cost);
        }

        return minimumCost;
    }

    private static int costForSplit(
            MatrixDimensions dimensions,
            CostTable minimumCosts,
            ChainRange range,
            int split) {
        return minimumCosts.get(range.start(), split)
                + minimumCosts.get(split, range.end())
                + dimensions.get(range.start()) * dimensions.get(split) * dimensions.get(range.end());
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

    private static final class ChainRange {
        private final int start;
        private final int end;

        private ChainRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        private static ChainRange fromStartAndLength(int start, int length) {
            return new ChainRange(start, start + length);
        }

        private static ChainRange fromStartAndEnd(int start, int end) {
            return new ChainRange(start, end);
        }

        private int start() {
            return start;
        }

        private int end() {
            return end;
        }

        private int firstSplit() {
            return start + 1;
        }
    }

    private static final class CostTable {
        private final int[][] costs;

        private CostTable(int dimensionCount) {
            costs = new int[dimensionCount][dimensionCount];
        }

        private int get(ChainRange range) {
            return get(range.start(), range.end());
        }

        private int get(int start, int end) {
            return costs[start][end];
        }

        private void set(ChainRange range, int cost) {
            costs[range.start()][range.end()] = cost;
        }
    }
}
