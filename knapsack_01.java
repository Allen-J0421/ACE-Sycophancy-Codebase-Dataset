record Item(int weight, int value) {}

record KnapsackInput(int capacity, Item[] items) {
    void validate() {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity must be non-negative");
    }
}

interface KnapsackStrategy {
    int solve(KnapsackInput problem);
}

class KnapsackSolver {
    private KnapsackSolver() {}

    static int compute(int capacity, Item[] items) {
        int[] dp = new int[capacity + 1];
        for (Item item : items) {
            processItem(dp, item);
        }
        return dp[capacity];
    }

    private static void processItem(int[] dp, Item item) {
        for (int remaining = dp.length - 1; remaining >= item.weight(); remaining--) {
            dp[remaining] = Math.max(dp[remaining], dp[remaining - item.weight()] + item.value());
        }
    }
}

class DPKnapsackStrategy implements KnapsackStrategy {
    public int solve(KnapsackInput problem) {
        return KnapsackSolver.compute(problem.capacity(), problem.items());
    }
}

class Knapsack {
    private final KnapsackStrategy strategy;

    Knapsack(KnapsackStrategy strategy) {
        this.strategy = strategy;
    }

    int solve(KnapsackInput problem) {
        problem.validate();
        return strategy.solve(problem);
    }

    public static void main(String[] args) {
        Item[] items = {new Item(4, 1), new Item(5, 2), new Item(1, 3)};
        KnapsackInput problem = new KnapsackInput(4, items);

        Knapsack knapsack = new Knapsack(new DPKnapsackStrategy());
        System.out.println(knapsack.solve(problem));
    }
}
