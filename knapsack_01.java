import java.util.*;

class Item {
    final int value;
    final int weight;
    final int id;

    Item(int id, int value, int weight) {
        if (value < 0 || weight < 0) throw new IllegalArgumentException("value and weight must be non-negative");
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("Item(id=%d, value=%d, weight=%d)", id, value, weight);
    }
}

class Problem {
    final List<Item> items;
    final int capacity;

    Problem(List<Item> items, int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity must be non-negative");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items list cannot be null or empty");
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format("Problem(items=%d, capacity=%d)", items.size(), capacity);
    }
}

class Solution {
    final int maxValue;
    final Set<Integer> selectedIds;

    Solution(int maxValue, Set<Integer> selectedIds) {
        this.maxValue = maxValue;
        this.selectedIds = Collections.unmodifiableSet(new HashSet<>(selectedIds));
    }

    @Override
    public String toString() {
        return String.format("Solution(maxValue=%d, selected=%s)", maxValue, selectedIds);
    }
}

interface KnapsackSolver {
    Solution solve(Problem problem);
}

class DynamicProgrammingSolver implements KnapsackSolver {
    @Override
    public Solution solve(Problem problem) {
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
        return new Solution(dp[n][W], selected);
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

class GreedySolver implements KnapsackSolver {
    @Override
    public Solution solve(Problem problem) {
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
        return new Solution(totalValue, selected);
    }
}

class Knapsack {
    public static void main(String[] args) {
        List<Item> items = Arrays.asList(
            new Item(1, 1, 4),
            new Item(2, 2, 5),
            new Item(3, 3, 1)
        );
        Problem problem = new Problem(items, 4);

        demonstrateSolvers(problem);
    }

    private static void demonstrateSolvers(Problem problem) {
        System.out.println("Problem: " + problem);
        System.out.println();

        KnapsackSolver[] solvers = {
            new DynamicProgrammingSolver(),
            new GreedySolver()
        };

        for (KnapsackSolver solver : solvers) {
            long start = System.nanoTime();
            Solution solution = solver.solve(problem);
            long elapsed = System.nanoTime() - start;

            System.out.printf("%s%n", solver.getClass().getSimpleName());
            System.out.printf("  Value: %d, Selected: %s (%.2fμs)%n",
                solution.maxValue, solution.selectedIds, elapsed / 1000.0);
        }
    }
}
