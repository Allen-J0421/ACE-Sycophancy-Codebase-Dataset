/**
 * Fluent API and entry point for matrix chain multiplication solving.
 */
public class MatrixChainMultiplication {

    /**
     * Event published during solver execution.
     */
    public static abstract class ExecutionEvent {
        private final long timestamp = System.currentTimeMillis();

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Fired when solver starts execution.
     */
    public static class SolverStartedEvent extends ExecutionEvent {
        private final int matrixCount;

        public SolverStartedEvent(int matrixCount) {
            this.matrixCount = matrixCount;
        }

        public int getMatrixCount() {
            return matrixCount;
        }
    }

    /**
     * Fired when an iteration completes.
     */
    public static class IterationCompleteEvent extends ExecutionEvent {
        private final int chainLength;
        private final int cellsComputed;

        public IterationCompleteEvent(int chainLength, int cellsComputed) {
            this.chainLength = chainLength;
            this.cellsComputed = cellsComputed;
        }

        public int getChainLength() {
            return chainLength;
        }

        public int getCellsComputed() {
            return cellsComputed;
        }
    }

    /**
     * Fired when solver completes execution.
     */
    public static class SolverCompletedEvent extends ExecutionEvent {
        private final Solution solution;
        private final long totalTimeMs;

        public SolverCompletedEvent(Solution solution, long totalTimeMs) {
            this.solution = solution;
            this.totalTimeMs = totalTimeMs;
        }

        public Solution getSolution() {
            return solution;
        }

        public long getTotalTimeMs() {
            return totalTimeMs;
        }
    }

    /**
     * Event bus for publishing and subscribing to execution events.
     */
    public interface EventBus {
        void subscribe(ExecutionListener listener);

        void unsubscribe(ExecutionListener listener);

        void publish(SolverStartedEvent event);

        void publish(IterationCompleteEvent event);

        void publish(SolverCompletedEvent event);

        void publishError(Exception error);
    }

    /**
     * Simple in-memory event bus implementation.
     */
    public static class SimpleEventBus implements EventBus {
        private final java.util.List<ExecutionListener> listeners = new java.util.ArrayList<>();

        @Override
        public void subscribe(ExecutionListener listener) {
            listeners.add(listener);
        }

        @Override
        public void unsubscribe(ExecutionListener listener) {
            listeners.remove(listener);
        }

        @Override
        public void publish(SolverStartedEvent event) {
            for (ExecutionListener listener : listeners) {
                listener.onSolverStarted(event);
            }
        }

        @Override
        public void publish(IterationCompleteEvent event) {
            for (ExecutionListener listener : listeners) {
                listener.onIterationComplete(event);
            }
        }

        @Override
        public void publish(SolverCompletedEvent event) {
            for (ExecutionListener listener : listeners) {
                listener.onSolverCompleted(event);
            }
        }

        @Override
        public void publishError(Exception error) {
            for (ExecutionListener listener : listeners) {
                listener.onError(error);
            }
        }
    }

    /**
     * Listener for execution events.
     */
    public interface ExecutionListener {
        void onSolverStarted(SolverStartedEvent event);

        void onIterationComplete(IterationCompleteEvent event);

        void onSolverCompleted(SolverCompletedEvent event);

        void onError(Exception error);
    }

    /**
     * Default no-op listener.
     */
    public static class NoOpExecutionListener implements ExecutionListener {
        @Override
        public void onSolverStarted(SolverStartedEvent event) {
        }

        @Override
        public void onIterationComplete(IterationCompleteEvent event) {
        }

        @Override
        public void onSolverCompleted(SolverCompletedEvent event) {
        }

        @Override
        public void onError(Exception error) {
        }
    }

    /**
     * Collects and aggregates metrics during computation.
     */
    public static class MetricsCollector {
        private long totalIterations = 0;
        private long totalCellsComputed = 0;
        private long startTime;
        private long endTime;

        public void recordStart() {
            this.startTime = System.currentTimeMillis();
        }

        public void recordIterationComplete(IterationCompleteEvent event) {
            totalIterations++;
            totalCellsComputed += event.getCellsComputed();
        }

        public void recordEnd() {
            this.endTime = System.currentTimeMillis();
        }

        public long getTotalTime() {
            return endTime - startTime;
        }

        public long getTotalIterations() {
            return totalIterations;
        }

        public long getTotalCellsComputed() {
            return totalCellsComputed;
        }

        public String getSummary() {
            return String.format("%d iterations, %d cells, %dms total",
                    totalIterations, totalCellsComputed, getTotalTime());
        }
    }

    /**
     * Computation context encapsulating state during execution.
     */
    public static class ComputationContext {
        private final MatrixDimensions dimensions;
        private final DPTable table;
        private final MetricsCollector metricsCollector;
        private final CostCalculator costCalculator;
        private final EventBus eventBus;

        ComputationContext(MatrixDimensions dimensions, CostCalculator costCalculator, EventBus eventBus) {
            this.dimensions = dimensions;
            this.table = new DPTable(dimensions.size());
            this.metricsCollector = new MetricsCollector();
            this.costCalculator = costCalculator;
            this.eventBus = eventBus;
        }

        public MatrixDimensions getDimensions() {
            return dimensions;
        }

        public DPTable getTable() {
            return table;
        }

        public MetricsCollector getMetricsCollector() {
            return metricsCollector;
        }

        public CostCalculator getCostCalculator() {
            return costCalculator;
        }

        public EventBus getEventBus() {
            return eventBus;
        }
    }

    /**
     * Pluggable cost calculation strategy.
     */
    public interface CostCalculator {
        int calculateCost(int leftCost, int rightCost, int leftDim, int commonDim, int rightDim);
    }

    /**
     * Standard cost calculator for matrix chain multiplication.
     */
    public static class StandardCostCalculator implements CostCalculator {
        @Override
        public int calculateCost(int leftCost, int rightCost, int leftDim, int commonDim, int rightDim) {
            return leftCost + rightCost + leftDim * commonDim * rightDim;
        }
    }

    /**
     * Input validator abstraction.
     */
    public interface InputValidator {
        void validate(MatrixDimensions dimensions) throws IllegalArgumentException;
    }

    /**
     * Composite validator that chains multiple validators.
     */
    public static class CompositeValidator implements InputValidator {
        private final InputValidator[] validators;

        public CompositeValidator(InputValidator... validators) {
            this.validators = validators;
        }

        @Override
        public void validate(MatrixDimensions dimensions) throws IllegalArgumentException {
            for (InputValidator validator : validators) {
                validator.validate(dimensions);
            }
        }
    }

    /**
     * Validates minimum matrix count.
     */
    public static class MinimumMatrixCountValidator implements InputValidator {
        private static final int MIN_MATRICES = 2;

        @Override
        public void validate(MatrixDimensions dimensions) throws IllegalArgumentException {
            if (dimensions.size() < MIN_MATRICES) {
                throw new IllegalArgumentException("At least " + MIN_MATRICES + " matrices required");
            }
        }
    }

    /**
     * Validates that all dimensions are positive.
     */
    public static class PositiveDimensionsValidator implements InputValidator {
        @Override
        public void validate(MatrixDimensions dimensions) throws IllegalArgumentException {
            for (int i = 0; i < dimensions.size(); i++) {
                if (dimensions.get(i) <= 0) {
                    throw new IllegalArgumentException("All dimensions must be positive, but got " + dimensions.get(i) + " at index " + i);
                }
            }
        }
    }

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
     * Configuration for solver behavior.
     */
    public static class SolverOptions {
        private final EventBus eventBus;
        private final CostCalculator costCalculator;
        private final InputValidator inputValidator;
        private final boolean cachingEnabled;

        private SolverOptions(EventBus eventBus, CostCalculator costCalculator,
                              InputValidator inputValidator, boolean cachingEnabled) {
            this.eventBus = eventBus;
            this.costCalculator = costCalculator;
            this.inputValidator = inputValidator;
            this.cachingEnabled = cachingEnabled;
        }

        public EventBus getEventBus() {
            return eventBus;
        }

        public CostCalculator getCostCalculator() {
            return costCalculator;
        }

        public InputValidator getInputValidator() {
            return inputValidator;
        }

        public boolean isCachingEnabled() {
            return cachingEnabled;
        }
    }

    /**
     * Fluent builder for configuring and creating solvers.
     */
    public static class SolverBuilder {
        private MatrixDimensions dimensions;
        private boolean cachingEnabled = false;
        private EventBus eventBus = new SimpleEventBus();
        private CostCalculator costCalculator = new StandardCostCalculator();
        private InputValidator inputValidator = new CompositeValidator(
                new MinimumMatrixCountValidator(),
                new PositiveDimensionsValidator()
        );

        public SolverBuilder withDimensions(int[] dimensions) {
            this.dimensions = new MatrixDimensions(dimensions);
            return this;
        }

        public SolverBuilder enableCaching() {
            this.cachingEnabled = true;
            return this;
        }

        public SolverBuilder withEventBus(EventBus bus) {
            this.eventBus = bus;
            return this;
        }

        public SolverBuilder subscribe(ExecutionListener listener) {
            this.eventBus.subscribe(listener);
            return this;
        }

        public SolverBuilder withCostCalculator(CostCalculator calculator) {
            this.costCalculator = calculator;
            return this;
        }

        public SolverBuilder withInputValidator(InputValidator validator) {
            this.inputValidator = validator;
            return this;
        }

        public Solver build() {
            inputValidator.validate(dimensions);
            SolverOptions options = new SolverOptions(eventBus, costCalculator,
                    inputValidator, cachingEnabled);
            SolverConfig config = new SolverConfig(dimensions, cachingEnabled, "DP");
            Solver solver = new BottomUpDynamicProgrammingSolver(config, options);
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
        private final SolverOptions options;

        BottomUpDynamicProgrammingSolver(SolverConfig config, SolverOptions options) {
            this.config = config;
            this.options = options;
        }

        @Override
        public Solution solve() {
            ComputationContext context = new ComputationContext(
                    config.getDimensions(),
                    options.getCostCalculator(),
                    options.getEventBus()
            );

            try {
                context.getMetricsCollector().recordStart();
                int n = config.getDimensions().size();
                context.getEventBus().publish(new SolverStartedEvent(n));

                computeOptimalCosts(context);

                context.getMetricsCollector().recordEnd();
                long totalTime = context.getMetricsCollector().getTotalTime();

                PerformanceMetrics metrics = new PerformanceMetrics(totalTime, n);
                Solution solution = new SolutionBuilder()
                        .withMinimumCost(context.getTable().getMinimumCost())
                        .withSplitPoints(context.getTable().getSplits())
                        .withMetrics(metrics)
                        .withMatrixCount(n)
                        .build();

                context.getEventBus().publish(new SolverCompletedEvent(solution, totalTime));
                return solution;
            } catch (Exception e) {
                options.getEventBus().publishError(e);
                throw e;
            }
        }

        private void computeOptimalCosts(ComputationContext context) {
            int n = context.getDimensions().size();
            int[] dims = context.getDimensions().toArray();
            DPTable table = context.getTable();
            CostCalculator calculator = context.getCostCalculator();
            EventBus eventBus = context.getEventBus();

            for (int chainLength = 2; chainLength < n; chainLength++) {
                for (int start = 0; start < n - chainLength; start++) {
                    int end = start + chainLength;
                    table.setCost(start, end, Integer.MAX_VALUE);

                    for (int split = start + 1; split < end; split++) {
                        int cost = calculator.calculateCost(
                                table.getCost(start, split),
                                table.getCost(split, end),
                                dims[start],
                                dims[split],
                                dims[end]
                        );

                        if (cost < table.getCost(start, end)) {
                            table.setCost(start, end, cost);
                            table.recordSplit(start, end, split);
                        }
                    }
                }
                IterationCompleteEvent event = new IterationCompleteEvent(chainLength, n - chainLength);
                eventBus.publish(event);
                context.getMetricsCollector().recordIterationComplete(event);
            }
        }
    }

    public static void main(String[] args) {
        ExecutionListener loggingListener = new ExecutionListener() {
            @Override
            public void onSolverStarted(SolverStartedEvent event) {
                System.out.println("→ Solver started with " + event.getMatrixCount() + " matrices");
            }

            @Override
            public void onIterationComplete(IterationCompleteEvent event) {
                System.out.println("  ✓ Chain length " + event.getChainLength() + " complete (" + event.getCellsComputed() + " cells)");
            }

            @Override
            public void onSolverCompleted(SolverCompletedEvent event) {
                System.out.println("← Solver completed in " + event.getTotalTimeMs() + "ms");
            }

            @Override
            public void onError(Exception error) {
                System.err.println("✗ Error: " + error.getMessage());
            }
        };

        ExecutionListener metricsListener = new ExecutionListener() {
            private final MetricsCollector collector = new MetricsCollector();

            @Override
            public void onSolverStarted(SolverStartedEvent event) {
                collector.recordStart();
            }

            @Override
            public void onIterationComplete(IterationCompleteEvent event) {
                collector.recordIterationComplete(event);
            }

            @Override
            public void onSolverCompleted(SolverCompletedEvent event) {
                collector.recordEnd();
                System.out.println("Metrics: " + collector.getSummary());
            }

            @Override
            public void onError(Exception error) {
            }
        };

        Solution solution = builder()
                .withDimensions(new int[] { 2, 1, 3, 4 })
                .enableCaching()
                .subscribe(loggingListener)
                .subscribe(metricsListener)
                .withCostCalculator(new StandardCostCalculator())
                .solve();

        System.out.println("\n" + solution);
        System.out.println("Compact: " + solution.format(new CompactFormatter()));
        System.out.println("JSON: " + solution.format(new JsonFormatter()));
    }
}
