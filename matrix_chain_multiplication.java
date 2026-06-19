/**
 * Fluent API and entry point for matrix chain multiplication solving.
 */
public class MatrixChainMultiplication {

    public static SolverBuilder builder() {
        return new SolverBuilder();
    }

    /**
     * Algorithm abstraction for solving matrix chain multiplication.
     */
    public interface Solver {
        Solution solve();
    }

    /**
     * Fluent builder for configuring and creating solvers.
     */
    public static class SolverBuilder {
        private int[] dimensions;
        private boolean cachingEnabled = false;

        public SolverBuilder withDimensions(int[] dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public SolverBuilder enableCaching() {
            this.cachingEnabled = true;
            return this;
        }

        public Solver build() {
            Solver solver = new DynamicProgrammingSolver(dimensions);
            return cachingEnabled ? new CachingSolver(solver) : solver;
        }

        public Solution solve() {
            return build().solve();
        }
    }

    /**
     * Immutable result object containing the solution and metadata.
     */
    public static class Solution {
        private final int minimumCost;
        private final int[][] splitPoints;
        private final long computationTimeMs;
        private final int matrixCount;
        private final ParenthesizationBuilder parenthesizer;

        Solution(int minimumCost, int[][] splitPoints, long computationTimeMs, int matrixCount) {
            this.minimumCost = minimumCost;
            this.splitPoints = splitPoints;
            this.computationTimeMs = computationTimeMs;
            this.matrixCount = matrixCount;
            this.parenthesizer = new ParenthesizationBuilder(splitPoints, matrixCount);
        }

        public int getMinimumCost() {
            return minimumCost;
        }

        public long getComputationTimeMs() {
            return computationTimeMs;
        }

        public String getOptimalParenthesization() {
            return parenthesizer.build();
        }

        public String getOptimalParenthesization(char matrixPrefix) {
            return parenthesizer.buildWithPrefix(matrixPrefix);
        }

        @Override
        public String toString() {
            return String.format("Minimum multiplications: %d (computed in %dms), Optimal order: %s",
                    minimumCost, computationTimeMs, getOptimalParenthesization());
        }
    }

    /**
     * Builds optimal parenthesization strings from split point data.
     */
    static class ParenthesizationBuilder {
        private final int[][] splitPoints;
        private final int matrixCount;
        private char matrixPrefix = 'M';

        ParenthesizationBuilder(int[][] splitPoints, int matrixCount) {
            this.splitPoints = splitPoints;
            this.matrixCount = matrixCount;
        }

        String build() {
            return buildWithPrefix('M');
        }

        String buildWithPrefix(char prefix) {
            this.matrixPrefix = prefix;
            return reconstruct(0, matrixCount - 2);
        }

        private String reconstruct(int start, int end) {
            if (start == end) {
                return String.valueOf(matrixPrefix) + start;
            }
            int split = splitPoints[start][end];
            String left = reconstruct(start, split);
            String right = reconstruct(split + 1, end);
            return "(" + left + " × " + right + ")";
        }
    }

    /**
     * Decorator pattern: caches solver results for identical inputs.
     */
    static class CachingSolver implements Solver {
        private final Solver delegate;
        private Solution cachedResult;

        CachingSolver(Solver delegate) {
            this.delegate = delegate;
        }

        @Override
        public Solution solve() {
            if (cachedResult == null) {
                cachedResult = delegate.solve();
            }
            return cachedResult;
        }
    }

    /**
     * Bottom-up dynamic programming solver for matrix chain multiplication.
     * Time Complexity: O(n³) where n is the number of matrices
     * Space Complexity: O(n²) for the DP tables
     */
    static class DynamicProgrammingSolver implements Solver {
        private static final int MIN_MATRICES = 2;
        private final int[] dimensions;
        private final int numMatrices;
        private final int[][] costTable;
        private final int[][] splitTable;

        DynamicProgrammingSolver(int[] dimensions) {
            this.dimensions = validateAndStore(dimensions);
            this.numMatrices = dimensions.length;
            this.costTable = new int[numMatrices][numMatrices];
            this.splitTable = new int[numMatrices][numMatrices];
        }

        @Override
        public Solution solve() {
            long startTime = System.currentTimeMillis();
            computeOptimalCosts();
            long endTime = System.currentTimeMillis();

            return new Solution(costTable[0][numMatrices - 1], splitTable,
                    endTime - startTime, numMatrices);
        }

        private int[] validateAndStore(int[] dimensions) {
            if (dimensions == null || dimensions.length < MIN_MATRICES) {
                throw new IllegalArgumentException("At least 2 matrices required");
            }
            return dimensions;
        }

        private void computeOptimalCosts() {
            for (int chainLength = 2; chainLength < numMatrices; chainLength++) {
                for (int start = 0; start < numMatrices - chainLength; start++) {
                    int end = start + chainLength;
                    costTable[start][end] = Integer.MAX_VALUE;

                    for (int split = start + 1; split < end; split++) {
                        int cost = computeCostForSplit(start, split, end);
                        if (cost < costTable[start][end]) {
                            costTable[start][end] = cost;
                            splitTable[start][end] = split;
                        }
                    }
                }
            }
        }

        private int computeCostForSplit(int start, int split, int end) {
            return costTable[start][split]
                 + costTable[split][end]
                 + dimensions[start] * dimensions[split] * dimensions[end];
        }
    }

    public static void main(String[] args) {
        Solution solution = builder()
                .withDimensions(new int[] { 2, 1, 3, 4 })
                .enableCaching()
                .solve();
        System.out.println(solution);
    }
}
