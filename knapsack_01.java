class KnapsackProblem {
    final int capacity;
    final int[] values;
    final int[] weights;

    KnapsackProblem(int capacity, int[] values, int[] weights) {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity must be non-negative");
        if (values.length != weights.length)
            throw new IllegalArgumentException("Values and weights arrays must have equal length");
        this.capacity = capacity;
        this.values = values;
        this.weights = weights;
    }
}

interface KnapsackStrategy {
    int solve(KnapsackProblem problem);
}

class KnapsackDPTable {
    private final int[] dp;

    KnapsackDPTable(int capacity) {
        this.dp = new int[capacity + 1];
    }

    void processItem(int weight, int value) {
        for (int remaining = dp.length - 1; remaining >= weight; remaining--) {
            dp[remaining] = Math.max(dp[remaining], dp[remaining - weight] + value);
        }
    }

    int optimalValue() {
        return dp[dp.length - 1];
    }
}

class DPKnapsackStrategy implements KnapsackStrategy {
    public int solve(KnapsackProblem problem) {
        KnapsackDPTable table = new KnapsackDPTable(problem.capacity);
        for (int i = 0; i < problem.weights.length; i++) {
            table.processItem(problem.weights[i], problem.values[i]);
        }
        return table.optimalValue();
    }
}

class Knapsack {
    private final KnapsackStrategy strategy;

    Knapsack(KnapsackStrategy strategy) {
        this.strategy = strategy;
    }

    int solve(KnapsackProblem problem) {
        return strategy.solve(problem);
    }

    public static void main(String[] args) {
        KnapsackProblem problem = new KnapsackProblem(4, new int[]{1, 2, 3}, new int[]{4, 5, 1});

        Knapsack knapsack = new Knapsack(new DPKnapsackStrategy());
        System.out.println(knapsack.solve(problem));
    }
}
