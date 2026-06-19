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
     * Strategy for formatting solution results.
     */
    public interface ResultFormatter {
        String format(Solution solution);
    }

    /**
     * Standard human-readable result format.
     */
    public static class DefaultFormatter implements ResultFormatter {
        @Override
        public String format(Solution solution) {
            return String.format("Minimum multiplications: %d (%s), Optimal order: %s",
                    solution.getMinimumCost(), solution.getMetrics(),
                    solution.getOptimalParenthesization());
        }
    }

    /**
     * Compact result format.
     */
    public static class CompactFormatter implements ResultFormatter {
        @Override
        public String format(Solution solution) {
            return String.format("Cost: %d | Time: %dms",
                    solution.getMinimumCost(), solution.getMetrics().getComputationTimeMs());
        }
    }

    /**
     * JSON-style result format.
     */
    public static class JsonFormatter implements ResultFormatter {
        @Override
        public String format(Solution solution) {
            return String.format("{\"minimumCost\": %d, \"timeMs\": %d, \"parenthesization\": \"%s\"}",
                    solution.getMinimumCost(), solution.getMetrics().getComputationTimeMs(),
                    solution.getOptimalParenthesization());
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
     * Builder for constructing Solution objects.
     */
    public static class SolutionBuilder {
        private int minimumCost;
        private int[][] splitPoints;
        private PerformanceMetrics metrics;
        private int matrixCount;

        public SolutionBuilder withMinimumCost(int cost) {
            this.minimumCost = cost;
            return this;
        }

        public SolutionBuilder withSplitPoints(int[][] splits) {
            this.splitPoints = splits;
            return this;
        }

        public SolutionBuilder withMetrics(PerformanceMetrics metrics) {
            this.metrics = metrics;
            return this;
        }

        public SolutionBuilder withMatrixCount(int count) {
            this.matrixCount = count;
            return this;
        }

        public Solution build() {
            return new Solution(minimumCost, splitPoints, metrics, matrixCount);
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
        private final ResultFormatter formatter;

        Solution(int minimumCost, int[][] splitPoints, PerformanceMetrics metrics, int matrixCount) {
            this.minimumCost = minimumCost;
            this.splitPoints = splitPoints;
            this.metrics = metrics;
            this.matrixCount = matrixCount;
            this.parenthesizer = new ParenthesizationBuilder(splitPoints, matrixCount);
            this.formatter = new DefaultFormatter();
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

        public String format(ResultFormatter formatter) {
            return formatter.format(this);
        }

        @Override
        public String toString() {
            return formatter.format(this);
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

            return new SolutionBuilder()
                    .withMinimumCost(table.getMinimumCost())
                    .withSplitPoints(table.getSplits())
                    .withMetrics(metrics)
                    .withMatrixCount(config.getDimensions().size())
                    .build();
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
        System.out.println("Default format: " + solution);
        System.out.println("Compact format: " + solution.format(new CompactFormatter()));
        System.out.println("JSON format: " + solution.format(new JsonFormatter()));
    }
}
