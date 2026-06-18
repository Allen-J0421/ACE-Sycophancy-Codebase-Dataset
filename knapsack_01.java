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
}

class Solution {
    final int maxValue;
    final Set<Integer> selectedIds;
    final String solverName;
    final long executionTimeNanos;

    Solution(int maxValue, Set<Integer> selectedIds, String solverName, long executionTimeNanos) {
        this.maxValue = maxValue;
        this.selectedIds = Collections.unmodifiableSet(new HashSet<>(selectedIds));
        this.solverName = solverName;
        this.executionTimeNanos = executionTimeNanos;
    }

    @Override
    public String toString() {
        return String.format("Solution(solver=%s, value=%d, selected=%s, time=%.2fμs)",
            solverName, maxValue, selectedIds, executionTimeNanos / 1000.0);
    }
}

interface KnapsackSolver {
    Solution solve(Problem problem);
    String getName();
}

class DynamicProgrammingSolver implements KnapsackSolver {
    @Override
    public String getName() { return "DynamicProgramming"; }

    @Override
    public Solution solve(Problem problem) {
        long start = System.nanoTime();
        int n = problem.items.size();
        int W = problem.capacity;

        int[][] dp = new int[n + 1][W + 1];
        for (int i = 1; i <= n; i++) {
            Item item = problem.items.get(i - 1);
            for (int w = W; w >= item.weight; w--) {
                dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - item.weight] + item.value);
            }
        }

        Set<Integer> selected = backtrackSelection(problem, dp);
        long elapsed = System.nanoTime() - start;
        return new Solution(dp[n][W], selected, getName(), elapsed);
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

class SpaceOptimizedDPSolver implements KnapsackSolver {
    @Override
    public String getName() { return "SpaceOptimizedDP"; }

    @Override
    public Solution solve(Problem problem) {
        long start = System.nanoTime();
        int n = problem.items.size();
        int W = problem.capacity;

        int[] dp = new int[W + 1];
        for (int i = 0; i < n; i++) {
            Item item = problem.items.get(i);
            for (int w = W; w >= item.weight; w--) {
                dp[w] = Math.max(dp[w], dp[w - item.weight] + item.value);
            }
        }

        Set<Integer> selected = new HashSet<>();
        long elapsed = System.nanoTime() - start;
        return new Solution(dp[W], selected, getName(), elapsed);
    }
}

class GreedySolver implements KnapsackSolver {
    @Override
    public String getName() { return "Greedy"; }

    @Override
    public Solution solve(Problem problem) {
        long start = System.nanoTime();
        List<Item> sorted = new ArrayList<>(problem.items);
        sorted.sort((a, b) -> Double.compare((double) b.value / b.weight, (double) a.value / a.weight));

        Set<Integer> selected = new HashSet<>();
        int totalWeight = 0;
        int totalValue = 0;

        for (Item item : sorted) {
            if (totalWeight + item.weight <= problem.capacity) {
                selected.add(item.id);
                totalWeight += item.weight;
                totalValue += item.value;
            }
        }
        long elapsed = System.nanoTime() - start;
        return new Solution(totalValue, selected, getName(), elapsed);
    }
}

abstract class SolverDecorator implements KnapsackSolver {
    protected final KnapsackSolver delegate;

    protected SolverDecorator(KnapsackSolver delegate) {
        this.delegate = delegate;
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

class CompositeSolver implements KnapsackSolver {
    private final List<KnapsackSolver> solvers;

    CompositeSolver(KnapsackSolver... solvers) {
        this.solvers = Arrays.asList(solvers);
    }

    @Override
    public String getName() { return "Composite"; }

    @Override
    public Solution solve(Problem problem) {
        Solution best = null;
        for (KnapsackSolver solver : solvers) {
            Solution solution = solver.solve(problem);
            if (best == null || solution.maxValue > best.maxValue) {
                best = solution;
            }
        }
        return best;
    }
}

class SolverFactory {
    static KnapsackSolver create(String type) {
        return switch (type.toLowerCase()) {
            case "dp", "dynamic" -> new DynamicProgrammingSolver();
            case "sopt", "space-opt" -> new SpaceOptimizedDPSolver();
            case "greedy" -> new GreedySolver();
            case "composite" -> new CompositeSolver(
                new DynamicProgrammingSolver(),
                new GreedySolver()
            );
            default -> throw new IllegalArgumentException("Unknown solver type: " + type);
        };
    }
}

class ResultComparison {
    final List<Solution> solutions;

    ResultComparison(List<Solution> solutions) {
        this.solutions = new ArrayList<>(solutions);
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
        System.out.println("=== Comparison ===");
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
        System.out.println();

        List<Solution> solutions = new ArrayList<>();
        for (String type : solverTypes) {
            KnapsackSolver solver = SolverFactory.create(type);
            Solution solution = solver.solve(problem);
            System.out.println(solution);
            solutions.add(solution);
        }

        new ResultComparison(solutions).report();
    }
}

class Knapsack {
    public static void main(String[] args) {
        Problem problem = Problem.builder()
            .addItem(Item.builder().id(1).value(1).weight(4).build())
            .addItem(Item.builder().id(2).value(2).weight(5).build())
            .addItem(Item.builder().id(3).value(3).weight(1).build())
            .capacity(4)
            .build();

        Benchmark.compare(problem, "dp", "sopt", "greedy");
    }
}
