class Knapsack {

    record Item(int weight, int value) {}

    static int solve(int capacity, Item[] items) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be non-negative");
        if (items == null) throw new IllegalArgumentException("Items must not be null");

        int[] dp = new int[capacity + 1];

        for (Item item : items) {
            for (int remaining = capacity; remaining >= item.weight(); remaining--) {
                dp[remaining] = Math.max(dp[remaining], dp[remaining - item.weight()] + item.value());
            }
        }

        return dp[capacity];
    }

    public static void main(String[] args) {
        Item[] items = {
            new Item(4, 1),
            new Item(5, 2),
            new Item(1, 3)
        };
        int capacity = 4;

        System.out.println(solve(capacity, items));
    }
}
