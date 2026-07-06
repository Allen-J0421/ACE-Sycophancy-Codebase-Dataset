class KnapsackProblem {
    final int capacity;
    final int[] values;
    final int[] weights;

    KnapsackProblem(int capacity, int[] values, int[] weights) {
        this.capacity = capacity;
        this.values = values;
        this.weights = weights;
    }
}

interface KnapsackStrategy {
    int solve(KnapsackProblem problem);
}

class DPKnapsackStrategy implements KnapsackStrategy {
    public int solve(KnapsackProblem problem) {
        int[] dp = new int[problem.capacity + 1];
        for (int i = 1; i <= problem.weights.length; i++) {
            for (int j = problem.capacity; j >= problem.weights[i - 1]; j--) {
                dp[j] = Math.max(dp[j], dp[j - problem.weights[i - 1]] + problem.values[i - 1]);
            }
        }
        return dp[problem.capacity];
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
