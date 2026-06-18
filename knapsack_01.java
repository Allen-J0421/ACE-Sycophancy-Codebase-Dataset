import java.util.*;

class Validation {
    static void requireNonNegative(int value, String name) {
        if (value < 0) throw new IllegalArgumentException(name + " must be non-negative, got: " + value);
    }

    static void requireNonEmpty(Collection<?> col, String name) {
        if (col == null || col.isEmpty()) throw new IllegalArgumentException(name + " cannot be null or empty");
    }
}

class Item {
    final int id;
    final int value;
    final int weight;

    private Item(int id, int value, int weight) {
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private int id;
        private int value;
        private int weight;

        Builder id(int id) { this.id = id; return this; }
        Builder value(int value) { this.value = value; return this; }
        Builder weight(int weight) { this.weight = weight; return this; }

        Item build() {
            Validation.requireNonNegative(value, "value");
            Validation.requireNonNegative(weight, "weight");
            return new Item(id, value, weight);
        }
    }

    @Override
    public String toString() {
        return String.format("Item(id=%d, value=%d, weight=%d)", id, value, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return id == item.id && value == item.value && weight == item.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, weight);
    }
}

class Problem {
    final List<Item> items;
    final int capacity;

    private Problem(List<Item> items, int capacity) {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.capacity = capacity;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final List<Item> items = new ArrayList<>();
        private int capacity;

        Builder addItem(Item item) {
            items.add(item);
            return this;
        }

        Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        Problem build() {
            Validation.requireNonNegative(capacity, "capacity");
            Validation.requireNonEmpty(items, "items");
            return new Problem(items, capacity);
        }
    }

    @Override
    public String toString() {
        return String.format("Problem(items=%d, capacity=%d)", items.size(), capacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Problem problem)) return false;
        return capacity == problem.capacity && items.equals(problem.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, capacity);
    }
}

class Solution {
    final int maxValue;
    final Set<Integer> selectedIds;
    final String solverName;
    final long executionTimeNanos;
    final boolean isValid;
    final int totalWeight;

    Solution(int maxValue, Set<Integer> selectedIds, String solverName, long executionTimeNanos) {
        this.maxValue = maxValue;
        this.selectedIds = Collections.unmodifiableSet(new HashSet<>(selectedIds));
        this.solverName = solverName;
        this.executionTimeNanos = executionTimeNanos;
        this.isValid = true;
        this.totalWeight = 0;
    }

    Solution(int maxValue, Set<Integer> selectedIds, String solverName, long executionTimeNanos,
             boolean isValid, int totalWeight) {
        this.maxValue = maxValue;
        this.selectedIds = Collections.unmodifiableSet(new HashSet<>(selectedIds));
        this.solverName = solverName;
        this.executionTimeNanos = executionTimeNanos;
        this.isValid = isValid;
        this.totalWeight = totalWeight;
    }

    @Override
    public String toString() {
        return String.format("Solution(solver=%s, value=%d, weight=%d, selected=%d, valid=%s, time=%.2fμs)",
            solverName, maxValue, totalWeight, selectedIds.size(), isValid, executionTimeNanos / 1000.0);
    }
}

class ResultValidator {
    static Result validate(Solution solution, Problem problem) {
        int totalWeight = 0;
        int totalValue = 0;
        for (Item item : problem.items) {
            if (solution.selectedIds.contains(item.id)) {
                totalWeight += item.weight;
                totalValue += item.value;
            }
        }
        boolean valid = totalWeight <= problem.capacity && totalValue == solution.maxValue;
        return new Result(solution, valid, totalWeight, totalValue);
    }

    static class Result {
        final Solution solution;
        final boolean isValid;
        final int actualWeight;
        final int actualValue;

        Result(Solution solution, boolean isValid, int actualWeight, int actualValue) {
            this.solution = solution;
            this.isValid = isValid;
            this.actualWeight = actualWeight;
            this.actualValue = actualValue;
        }

        boolean exceedsCapacity(Problem problem) {
            return actualWeight > problem.capacity;
        }

        void report() {
            System.out.printf("    Validation: %s (weight=%d, value=%d)%n",
                isValid ? "✓ VALID" : "✗ INVALID", actualWeight, actualValue);
        }
    }
}

class Snapshot {
    final Problem problem;
    final Solution solution;

    Snapshot(Problem problem, Solution solution) {
        this.problem = problem;
        this.solution = solution;
    }

    void restore() {
        System.out.printf("Restored: %s -> %s%n", problem, solution);
    }
}

enum SolverState {
    IDLE, SOLVING, COMPLETED, FAILED
}

interface SolverListener {
    void onSolveStart(String solverName, Problem problem);
    void onSolveEnd(String solverName, Solution solution);
    void onStateChange(String solverName, SolverState oldState, SolverState newState);
    void onError(String solverName, Exception error);
}

interface KnapsackSolver {
    Solution solve(Problem problem);
    String getName();
    void addListener(SolverListener listener);
}

class ConstraintValidator {
    static void validateProblem(Problem problem) {
        if (problem.capacity < 0) throw new IllegalArgumentException("Capacity must be non-negative");
        if (problem.items.isEmpty()) throw new IllegalArgumentException("Problem must have at least one item");
        for (Item item : problem.items) {
            if (item.value < 0) throw new IllegalArgumentException("Item value cannot be negative");
            if (item.weight < 0) throw new IllegalArgumentException("Item weight cannot be negative");
        }
    }

    static void validateSolution(Solution solution, Problem problem) {
        int totalWeight = 0;
        int totalValue = 0;
        for (Item item : problem.items) {
            if (solution.selectedIds.contains(item.id)) {
                totalWeight += item.weight;
                totalValue += item.value;
            }
        }
        if (totalWeight > problem.capacity) {
            throw new IllegalStateException("Solution exceeds capacity: " + totalWeight + " > " + problem.capacity);
        }
        if (totalValue != solution.maxValue) {
            throw new IllegalStateException("Solution value mismatch: " + totalValue + " != " + solution.maxValue);
        }
    }
}

abstract class BaseSolver implements KnapsackSolver {
    protected final List<SolverListener> listeners = new ArrayList<>();
    protected SolverState state = SolverState.IDLE;

    @Override
    public void addListener(SolverListener listener) {
        listeners.add(listener);
    }

    protected void setState(SolverState newState) {
        if (state != newState) {
            SolverState oldState = state;
            state = newState;
            listeners.forEach(l -> l.onStateChange(getName(), oldState, newState));
        }
    }

    protected void notifyStart(Problem problem) {
        setState(SolverState.SOLVING);
        listeners.forEach(l -> l.onSolveStart(getName(), problem));
    }

    protected void notifyEnd(Solution solution) {
        setState(SolverState.COMPLETED);
        listeners.forEach(l -> l.onSolveEnd(getName(), solution));
    }

    protected void notifyError(Exception error) {
        setState(SolverState.FAILED);
        listeners.forEach(l -> l.onError(getName(), error));
    }

    protected Solution executeSafely(Problem problem, java.util.function.Function<Problem, Solution> solver) {
        try {
            ConstraintValidator.validateProblem(problem);
            notifyStart(problem);
            Solution solution = solver.apply(problem);
            notifyEnd(solution);
            return solution;
        } catch (Exception e) {
            notifyError(e);
            throw e;
        }
    }
}

class DynamicProgrammingSolver extends BaseSolver {
    @Override
    public String getName() { return "DynamicProgramming"; }

    @Override
    public Solution solve(Problem problem) {
        return executeSafely(problem, p -> {
            long start = System.nanoTime();
            int n = p.items.size();
            int W = p.capacity;

            int[][] dp = new int[n + 1][W + 1];
            for (int i = 1; i <= n; i++) {
                Item item = p.items.get(i - 1);
                for (int w = W; w >= item.weight; w--) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - item.weight] + item.value);
                }
            }

            Set<Integer> selected = backtrackSelection(p, dp);
            long elapsed = System.nanoTime() - start;
            return new Solution(dp[n][W], selected, getName(), elapsed);
        });
    }

    private Set<Integer> backtrackSelection(Problem problem, int[][] dp) {
        Set<Integer> selected = new HashSet<>();
        int n = problem.items.size();
        int w = problem.capacity;

        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Item item = problem.items.get(i - 1);
                selected.add(item.id);
                w -= item.weight;
            }
        }
        return selected;
    }
}

class SpaceOptimizedDPSolver extends BaseSolver {
    @Override
    public String getName() { return "SpaceOptimizedDP"; }

    @Override
    public Solution solve(Problem problem) {
        return executeSafely(problem, p -> {
            long start = System.nanoTime();
            int n = p.items.size();
            int W = p.capacity;

            int[] dp = new int[W + 1];
            for (int i = 0; i < n; i++) {
                Item item = p.items.get(i);
                for (int w = W; w >= item.weight; w--) {
                    dp[w] = Math.max(dp[w], dp[w - item.weight] + item.value);
                }
            }

            Set<Integer> selected = new HashSet<>();
            long elapsed = System.nanoTime() - start;
            return new Solution(dp[W], selected, getName(), elapsed);
        });
    }
}

class GreedySolver extends BaseSolver {
    @Override
    public String getName() { return "Greedy"; }

    @Override
    public Solution solve(Problem problem) {
        return executeSafely(problem, p -> {
            long start = System.nanoTime();
            List<Item> sorted = new ArrayList<>(p.items);
            sorted.sort((a, b) -> Double.compare((double) b.value / b.weight, (double) a.value / a.weight));

            Set<Integer> selected = new HashSet<>();
            int totalWeight = 0;
            int totalValue = 0;

            for (Item item : sorted) {
                if (totalWeight + item.weight <= p.capacity) {
                    selected.add(item.id);
                    totalWeight += item.weight;
                    totalValue += item.value;
                }
            }
            long elapsed = System.nanoTime() - start;
            return new Solution(totalValue, selected, getName(), elapsed);
        });
    }
}

class ProblemAnalyzer {
    private final Problem problem;

    ProblemAnalyzer(Problem problem) {
        this.problem = problem;
    }

    int itemCount() { return problem.items.size(); }
    int capacity() { return problem.capacity; }
    double avgValuePerWeight() {
        return problem.items.stream()
            .mapToDouble(i -> (double) i.value / i.weight)
            .average().orElse(0);
    }
    int maxWeight() {
        return problem.items.stream().mapToInt(i -> i.weight).max().orElse(0);
    }
    boolean isLargeCapacity() { return capacity() > 1000; }
    boolean isManyItems() { return itemCount() > 100; }

    void report() {
        System.out.printf("Analysis: %d items, capacity=%d, avgRatio=%.2f, maxWeight=%d%n",
            itemCount(), capacity(), avgValuePerWeight(), maxWeight());
    }
}

class PerformanceMetrics {
    final String solverName;
    final long executionTimeNanos;
    final int value;
    final int itemsSelected;

    PerformanceMetrics(Solution solution) {
        this.solverName = solution.solverName;
        this.executionTimeNanos = solution.executionTimeNanos;
        this.value = solution.maxValue;
        this.itemsSelected = solution.selectedIds.size();
    }

    double getEfficiency() {
        return executionTimeNanos > 0 ? (double) value / executionTimeNanos * 1e6 : 0;
    }

    void report() {
        System.out.printf("  %s: value=%d, items=%d, time=%.2fμs, efficiency=%.2f val/ms%n",
            solverName, value, itemsSelected, executionTimeNanos / 1000.0, getEfficiency());
    }
}

abstract class SolverDecorator extends BaseSolver {
    protected final KnapsackSolver delegate;

    protected SolverDecorator(KnapsackSolver delegate) {
        this.delegate = delegate;
        if (delegate instanceof BaseSolver bs) {
            bs.listeners.forEach(this::addListener);
        }
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}

class LoggingSolver extends SolverDecorator {
    LoggingSolver(KnapsackSolver delegate) { super(delegate); }

    @Override
    public Solution solve(Problem problem) {
        System.err.printf("[%s] Solving: %s%n", getName(), problem);
        Solution solution = delegate.solve(problem);
        System.err.printf("[%s] Result: value=%d, time=%.2fμs%n", getName(), solution.maxValue, solution.executionTimeNanos / 1000.0);
        return solution;
    }
}

class CachingSolver extends SolverDecorator {
    private final Map<Problem, Solution> cache = new HashMap<>();

    CachingSolver(KnapsackSolver delegate) { super(delegate); }

    @Override
    public Solution solve(Problem problem) {
        return cache.computeIfAbsent(problem, p -> delegate.solve(p));
    }
}

class CompositeSolver extends BaseSolver {
    private final List<KnapsackSolver> solvers;

    CompositeSolver(KnapsackSolver... solvers) {
        this.solvers = Arrays.asList(solvers);
    }

    @Override
    public String getName() { return "Composite"; }

    @Override
    public Solution solve(Problem problem) {
        notifyStart(problem);
        Solution best = null;
        for (KnapsackSolver solver : solvers) {
            Solution solution = solver.solve(problem);
            if (best == null || solution.maxValue > best.maxValue) {
                best = solution;
            }
        }
        notifyEnd(best);
        return best;
    }
}

class AdaptiveSolver extends BaseSolver {
    @Override
    public String getName() { return "Adaptive"; }

    @Override
    public Solution solve(Problem problem) {
        notifyStart(problem);
        ProblemAnalyzer analyzer = new ProblemAnalyzer(problem);
        KnapsackSolver chosen = selectSolver(analyzer);

        Solution solution = chosen.solve(problem);
        notifyEnd(solution);
        return solution;
    }

    private KnapsackSolver selectSolver(ProblemAnalyzer analyzer) {
        if (analyzer.isLargeCapacity() || analyzer.isManyItems()) {
            return new SpaceOptimizedDPSolver();
        }
        return new DynamicProgrammingSolver();
    }
}

class SolverRegistry {
    private static final Map<String, java.util.function.Supplier<KnapsackSolver>> REGISTRY = new HashMap<>();

    static {
        register("dp", DynamicProgrammingSolver::new);
        register("dynamic", DynamicProgrammingSolver::new);
        register("sopt", SpaceOptimizedDPSolver::new);
        register("space-opt", SpaceOptimizedDPSolver::new);
        register("greedy", GreedySolver::new);
        register("composite", () -> new CompositeSolver(
            new DynamicProgrammingSolver(),
            new GreedySolver()
        ));
        register("adaptive", AdaptiveSolver::new);
    }

    static void register(String name, java.util.function.Supplier<KnapsackSolver> factory) {
        REGISTRY.put(name.toLowerCase(), factory);
    }

    static KnapsackSolver create(String type) {
        var factory = REGISTRY.get(type.toLowerCase());
        if (factory == null) throw new IllegalArgumentException("Unknown solver type: " + type);
        return factory.get();
    }

    static Set<String> availableSolvers() {
        return REGISTRY.keySet();
    }
}

class SolverFactory {
    static KnapsackSolver create(String type) {
        return SolverRegistry.create(type);
    }
}

class SolverStatistics {
    private final String solverName;
    private int solveCount = 0;
    private int successCount = 0;
    private int failureCount = 0;
    private long totalTimeNanos = 0;
    private int maxValue = 0;

    SolverStatistics(String solverName) {
        this.solverName = solverName;
    }

    void recordSolve(Solution solution) {
        solveCount++;
        successCount++;
        totalTimeNanos += solution.executionTimeNanos;
        maxValue = Math.max(maxValue, solution.maxValue);
    }

    void recordFailure() {
        solveCount++;
        failureCount++;
    }

    void report() {
        System.out.printf("%s: %d solves (%d success, %d failed), avg=%.2fμs, maxValue=%d%n",
            solverName, solveCount, successCount, failureCount,
            successCount > 0 ? totalTimeNanos / (double) successCount / 1000.0 : 0, maxValue);
    }
}

class StatisticsListener implements SolverListener {
    private final Map<String, SolverStatistics> stats = new HashMap<>();

    @Override
    public void onSolveStart(String solverName, Problem problem) {}

    @Override
    public void onSolveEnd(String solverName, Solution solution) {
        stats.computeIfAbsent(solverName, SolverStatistics::new).recordSolve(solution);
    }

    @Override
    public void onStateChange(String solverName, SolverState oldState, SolverState newState) {}

    @Override
    public void onError(String solverName, Exception error) {
        stats.computeIfAbsent(solverName, SolverStatistics::new).recordFailure();
    }

    void report() {
        System.out.println("\n=== Solver Statistics ===");
        stats.values().forEach(SolverStatistics::report);
    }
}

class LoggingListener implements SolverListener {
    @Override
    public void onSolveStart(String solverName, Problem problem) {
        System.err.printf("[%s] Starting solve: %s%n", solverName, problem);
    }

    @Override
    public void onSolveEnd(String solverName, Solution solution) {
        System.err.printf("[%s] Completed: value=%d, time=%.2fμs%n",
            solverName, solution.maxValue, solution.executionTimeNanos / 1000.0);
    }

    @Override
    public void onStateChange(String solverName, SolverState oldState, SolverState newState) {
        System.err.printf("[%s] State change: %s -> %s%n", solverName, oldState, newState);
    }

    @Override
    public void onError(String solverName, Exception error) {
        System.err.printf("[%s] Error: %s%n", solverName, error.getMessage());
    }
}

class SolverConfig {
    private final Map<String, Object> config = new HashMap<>();
    private boolean validateResults = true;
    private boolean enableLogging = false;
    private boolean enableCaching = false;

    SolverConfig withValidation(boolean enabled) {
        this.validateResults = enabled;
        return this;
    }

    SolverConfig withLogging(boolean enabled) {
        this.enableLogging = enabled;
        return this;
    }

    SolverConfig withCaching(boolean enabled) {
        this.enableCaching = enabled;
        return this;
    }

    SolverConfig set(String key, Object value) {
        config.put(key, value);
        return this;
    }

    Object get(String key) { return config.get(key); }
    boolean isValidationEnabled() { return validateResults; }
    boolean isLoggingEnabled() { return enableLogging; }
    boolean isCachingEnabled() { return enableCaching; }
}

class BatchProcessingPipeline {
    private final List<Problem> problems = new ArrayList<>();
    private final SolverConfig config;
    private final StatisticsListener stats = new StatisticsListener();

    BatchProcessingPipeline(SolverConfig config) {
        this.config = config;
    }

    BatchProcessingPipeline addProblem(Problem problem) {
        try {
            ConstraintValidator.validateProblem(problem);
            problems.add(problem);
        } catch (IllegalArgumentException e) {
            System.err.printf("Invalid problem: %s%n", e.getMessage());
        }
        return this;
    }

    void execute(String... solverTypes) {
        System.out.println("=== Batch Processing Pipeline ===");
        System.out.printf("Processing %d problems with %d solvers%n", problems.size(), solverTypes.length);
        System.out.println();

        for (int i = 0; i < problems.size(); i++) {
            Problem problem = problems.get(i);
            System.out.printf("Problem %d: %s%n", i + 1, problem);

            List<Solution> solutions = new ArrayList<>();
            for (String type : solverTypes) {
                try {
                    KnapsackSolver solver = SolverRegistry.create(type);
                    solver.addListener(stats);

                    if (config.isLoggingEnabled()) {
                        solver.addListener(new LoggingListener());
                    }
                    if (config.isCachingEnabled()) {
                        solver = new CachingSolver(solver);
                    }

                    Solution solution = solver.solve(problem);
                    solutions.add(solution);

                    if (config.isValidationEnabled()) {
                        ResultValidator.Result result = ResultValidator.validate(solution, problem);
                        System.out.printf("  %s%n", solution);
                        result.report();
                    } else {
                        System.out.printf("  %s%n", solution);
                    }
                } catch (Exception e) {
                    System.err.printf("  Error with %s: %s%n", type, e.getMessage());
                }
            }
            System.out.println();
        }

        stats.report();
    }
}

class ResultComparison {
    final List<Solution> solutions;
    final ProblemAnalyzer analyzer;

    ResultComparison(List<Solution> solutions, Problem problem) {
        this.solutions = new ArrayList<>(solutions);
        this.analyzer = new ProblemAnalyzer(problem);
    }

    int maxValue() {
        return solutions.stream().mapToInt(s -> s.maxValue).max().orElse(0);
    }

    boolean allAgree() {
        int target = maxValue();
        return solutions.stream().allMatch(s -> s.maxValue == target);
    }

    void report() {
        System.out.println();
        System.out.println("=== Problem Analysis ===");
        analyzer.report();

        System.out.println("\n=== Performance Comparison ===");
        solutions.stream()
            .map(PerformanceMetrics::new)
            .forEach(PerformanceMetrics::report);

        System.out.println();
        System.out.printf("Consensus value: %d%n", maxValue());
        System.out.printf("All solvers agree: %s%n", allAgree());
        System.out.printf("Fastest: %s (%.2fμs)%n",
            solutions.stream().min(Comparator.comparingLong(s -> s.executionTimeNanos)).get().solverName,
            solutions.stream().mapToLong(s -> s.executionTimeNanos).min().getAsLong() / 1000.0);
    }
}

class Benchmark {
    static void compare(Problem problem, String... solverTypes) {
        System.out.println("=== Benchmark Results ===");
        System.out.println("Problem: " + problem);

        List<Solution> solutions = new ArrayList<>();
        for (String type : solverTypes) {
            KnapsackSolver solver = SolverFactory.create(type);
            Solution solution = solver.solve(problem);
            solutions.add(solution);
        }

        new ResultComparison(solutions, problem).report();
    }
}

class SnapshotManager {
    private final List<Snapshot> snapshots = new ArrayList<>();

    void save(Problem problem, Solution solution) {
        snapshots.add(new Snapshot(problem, solution));
    }

    void restoreAll() {
        System.out.println("\n=== Snapshot History ===");
        snapshots.forEach(Snapshot::restore);
    }

    int size() { return snapshots.size(); }
}

class TestSuite {
    static void runAll() {
        System.out.println("=== Test Suite ===\n");
        testSmallProblem();
        testEmptySelection();
        testSolverAgreement();
        testEquality();
        System.out.println("\nAll tests passed!");
    }

    private static void testSmallProblem() {
        Problem problem = Problem.builder()
            .addItem(Item.builder().id(1).value(1).weight(4).build())
            .addItem(Item.builder().id(2).value(2).weight(5).build())
            .addItem(Item.builder().id(3).value(3).weight(1).build())
            .capacity(4)
            .build();

        KnapsackSolver dp = new DynamicProgrammingSolver();
        Solution solution = dp.solve(problem);
        assert solution.maxValue == 3 : "Expected value 3, got " + solution.maxValue;
        System.out.println("✓ Small problem: value=" + solution.maxValue);
    }

    private static void testEmptySelection() {
        Problem problem = Problem.builder()
            .addItem(Item.builder().id(1).value(10).weight(100).build())
            .capacity(5)
            .build();

        KnapsackSolver solver = new SpaceOptimizedDPSolver();
        Solution solution = solver.solve(problem);
        assert solution.maxValue == 0 : "Expected value 0 (no items fit), got " + solution.maxValue;
        System.out.println("✓ Empty selection: value=" + solution.maxValue);
    }

    private static void testSolverAgreement() {
        Problem problem = Problem.builder()
            .addItem(Item.builder().id(1).value(60).weight(10).build())
            .addItem(Item.builder().id(2).value(100).weight(20).build())
            .capacity(30)
            .build();

        KnapsackSolver dp = new DynamicProgrammingSolver();
        KnapsackSolver greedy = new GreedySolver();

        int dpValue = dp.solve(problem).maxValue;
        int greedyValue = greedy.solve(problem).maxValue;
        assert dpValue == greedyValue : "DP and Greedy disagreed: " + dpValue + " vs " + greedyValue;
        System.out.println("✓ Solver agreement: DP=" + dpValue + ", Greedy=" + greedyValue);
    }

    private static void testEquality() {
        Item item1 = Item.builder().id(1).value(5).weight(3).build();
        Item item2 = Item.builder().id(1).value(5).weight(3).build();
        assert item1.equals(item2) : "Items should be equal";

        Problem p1 = Problem.builder().addItem(item1).capacity(10).build();
        Problem p2 = Problem.builder().addItem(item2).capacity(10).build();
        assert p1.equals(p2) : "Problems should be equal";
        System.out.println("✓ Equality: Items and Problems compare correctly");
    }
}

class Knapsack {
    public static void main(String[] args) {
        TestSuite.runAll();

        System.out.println();

        Problem problem1 = Problem.builder()
            .addItem(Item.builder().id(1).value(1).weight(4).build())
            .addItem(Item.builder().id(2).value(2).weight(5).build())
            .addItem(Item.builder().id(3).value(3).weight(1).build())
            .capacity(4)
            .build();

        Problem problem2 = Problem.builder()
            .addItem(Item.builder().id(1).value(60).weight(10).build())
            .addItem(Item.builder().id(2).value(100).weight(20).build())
            .addItem(Item.builder().id(3).value(120).weight(30).build())
            .capacity(50)
            .build();

        SolverConfig config = new SolverConfig()
            .withValidation(true)
            .withLogging(false)
            .withCaching(false);

        BatchProcessingPipeline pipeline = new BatchProcessingPipeline(config)
            .addProblem(problem1)
            .addProblem(problem2);

        pipeline.execute("dp", "greedy");

        System.out.println("\n=== Traditional Benchmark ===");
        Benchmark.compare(problem1, "dp", "sopt", "greedy", "adaptive");
    }
}
