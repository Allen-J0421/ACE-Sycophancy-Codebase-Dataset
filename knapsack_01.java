record Item(int weight, int value) {}

class KnapsackInput {
    final int capacity;
    final Item[] items;

    KnapsackInput(int capacity, Item[] items) {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity must be non-negative");
        this.capacity = capacity;
        this.items = items;
    }
}

interface KnapsackStrategy {
    int solve(KnapsackInput problem);
}

class KnapsackSolver {
    private final int[] dp;

    KnapsackSolver(int capacity) {
        this.dp = new int[capacity + 1];
    }

    void processItem(Item item) {
        for (int remaining = dp.length - 1; remaining >= item.weight(); remaining--) {
            dp[remaining] = Math.max(dp[remaining], dp[remaining - item.weight()] + item.value());
        }
    }

    int optimalValue() {
        return dp[dp.length - 1];
    }
}

class DPKnapsackStrategy implements KnapsackStrategy {
    public int solve(KnapsackInput problem) {
        KnapsackSolver table = new KnapsackSolver(problem.capacity);
        for (Item item : problem.items) {
            table.processItem(item);
        }
        return table.optimalValue();
    }
}

class Knapsack {
    private final KnapsackStrategy strategy;

    Knapsack(KnapsackStrategy strategy) {
        this.strategy = strategy;
    }

    int solve(KnapsackInput problem) {
        return strategy.solve(problem);
    }

    public static void main(String[] args) {
        Item[] items = {new Item(4, 1), new Item(5, 2), new Item(1, 3)};
        KnapsackInput problem = new KnapsackInput(4, items);

        Knapsack knapsack = new Knapsack(new DPKnapsackStrategy());
        System.out.println(knapsack.solve(problem));
    }
}
