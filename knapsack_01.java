interface KnapsackStrategy {
    int solve(int W, int[] val, int[] wt);
}

class DPKnapsackStrategy implements KnapsackStrategy {
    public int solve(int W, int[] val, int[] wt) {
        int[] dp = new int[W + 1];
        for (int i = 1; i <= wt.length; i++) {
            for (int j = W; j >= wt[i - 1]; j--) {
                dp[j] = Math.max(dp[j], dp[j - wt[i - 1]] + val[i - 1]);
            }
        }
        return dp[W];
    }
}

class Knapsack {
    private final KnapsackStrategy strategy;

    Knapsack(KnapsackStrategy strategy) {
        this.strategy = strategy;
    }

    int solve(int W, int[] val, int[] wt) {
        return strategy.solve(W, val, wt);
    }

    public static void main(String[] args) {
        int[] val = {1, 2, 3};
        int[] wt = {4, 5, 1};
        int W = 4;

        Knapsack knapsack = new Knapsack(new DPKnapsackStrategy());
        System.out.println(knapsack.solve(W, val, wt));
    }
}
