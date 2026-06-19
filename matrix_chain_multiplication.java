/**
 * Fluent API and entry point for matrix chain multiplication solving.
 */
public class MatrixChainMultiplication {

    public static SolverBuilder builder() {
        return new SolverBuilder();
    }

    /**
     * Validated, immutable representation of matrix dimensions.
     */
    public static class MatrixDimensions {
        private static final int MIN_MATRICES = 2;
        private final int[] values;

        public MatrixDimensions(int[] dimensions) {
            if (dimensions == null || dimensions.length < MIN_MATRICES) {
                throw new IllegalArgumentException("At least 2 matrices required");
            }
            this.values = dimensions.clone();
        }

        public int size() {
            return values.length;
        }

        public int get(int index) {
            return values[index];
        }

        int[] toArray() {
            return values.clone();
        }
    }

    /**
     * Encapsulates the DP tables and query operations.
     */
    static class DPTable {
        private final int[][] cost;
        private final int[][] splits;
        private final int size;

        DPTable(int size) {
            this.size = size;
            this.cost = new int[size][size];
            this.splits = new int[size][size];
        }

        void setCost(int i, int j, int value) {
            cost[i][j] = value;
        }

        int getCost(int i, int j) {
            return cost[i][j];
        }

        void recordSplit(int i, int j, int splitPoint) {
            splits[i][j] = splitPoint;
        }

        int getSplit(int i, int j) {
            return splits[i][j];
        }

        int getMinimumCost() {
            return cost[0][size - 1];
        }

        int[][] getSplits() {
            return splits;
        }
    }

    /**
     * Captures computation performance metrics.
     */
    public static class PerformanceMetrics {
        private final long computationTimeMs;
        private final int matricesProcessed;

        public PerformanceMetrics(long computationTimeMs, int matricesProcessed) {
            this.computationTimeMs = computationTimeMs;
            this.matricesProcessed = matricesProcessed;
        }

        public long getComputationTimeMs() {
            return computationTimeMs;
        }

        public int getMatricesProcessed() {
            return matricesProcessed;
        }

        @Override
        public String toString() {
            return String.format("computed in %dms (%d matrices)", computationTimeMs, matricesProcessed);
        }
    }

    /**
     * Algorithm abstraction for solving matrix chain multiplication.
     */
    public interface Solver {
        Solution solve();
    }

    /**
     * Configuration for solver behavior.
     */
    public static class SolverConfig {
        private final MatrixDimensions dimensions;
        private final boolean cachingEnabled;
        private final String algorithm;

        public SolverConfig(MatrixDimensions dimensions, boolean cachingEnabled, String algorithm) {
            this.dimensions = dimensions;
            this.cachingEnabled = cachingEnabled;
            this.algorithm = algorithm;
        }

        public MatrixDimensions getDimensions() {
            return dimensions;
        }

        public boolean isCachingEnabled() {
            return cachingEnabled;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }

    /**
     * Fluent builder for configuring and creating solvers.
     */
    public static class SolverBuilder {
        private MatrixDimensions dimensions;
        private boolean cachingEnabled = false;
        private String algorithm = "DP";

        public SolverBuilder withDimensions(int[] dimensions) {
            this.dimensions = new MatrixDimensions(dimensions);
            return this;
        }

        public SolverBuilder enableCaching() {
            this.cachingEnabled = true;
            return this;
        }

        public Solver build() {
            SolverConfig config = new SolverConfig(dimensions, cachingEnabled, algorithm);
            Solver solver = new BottomUpDynamicProgrammingSolver(config);
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
        private final PerformanceMetrics metrics;
        private final int matrixCount;
        private final ParenthesizationBuilder parenthesizer;

        Solution(int minimumCost, int[][] splitPoints, PerformanceMetrics metrics, int matrixCount) {
            this.minimumCost = minimumCost;
            this.splitPoints = splitPoints;
            this.metrics = metrics;
            this.matrixCount = matrixCount;
            this.parenthesizer = new ParenthesizationBuilder(splitPoints, matrixCount);
        }

        public int getMinimumCost() {
            return minimumCost;
        }

        public PerformanceMetrics getMetrics() {
            return metrics;
        }

        public String getOptimalParenthesization() {
            return parenthesizer.build();
        }

        public String getOptimalParenthesization(char matrixPrefix) {
            return parenthesizer.buildWithPrefix(matrixPrefix);
        }

        @Override
        public String toString() {
            return String.format("Minimum multiplications: %d (%s), Optimal order: %s",
                    minimumCost, metrics, getOptimalParenthesization());
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
    static class BottomUpDynamicProgrammingSolver implements Solver {
        private final SolverConfig config;
        private final DPTable table;

        BottomUpDynamicProgrammingSolver(SolverConfig config) {
            this.config = config;
            this.table = new DPTable(config.getDimensions().size());
        }

        @Override
        public Solution solve() {
            long startTime = System.currentTimeMillis();
            computeOptimalCosts();
            long endTime = System.currentTimeMillis();

            PerformanceMetrics metrics = new PerformanceMetrics(
                    endTime - startTime,
                    config.getDimensions().size()
            );

            return new Solution(
                    table.getMinimumCost(),
                    table.getSplits(),
                    metrics,
                    config.getDimensions().size()
            );
        }

        private void computeOptimalCosts() {
            int n = config.getDimensions().size();
            int[] dims = config.getDimensions().toArray();

            for (int chainLength = 2; chainLength < n; chainLength++) {
                for (int start = 0; start < n - chainLength; start++) {
                    int end = start + chainLength;
                    table.setCost(start, end, Integer.MAX_VALUE);

                    for (int split = start + 1; split < end; split++) {
                        int cost = table.getCost(start, split)
                                 + table.getCost(split, end)
                                 + dims[start] * dims[split] * dims[end];

                        if (cost < table.getCost(start, end)) {
                            table.setCost(start, end, cost);
                            table.recordSplit(start, end, split);
                        }
                    }
                }
            }
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
