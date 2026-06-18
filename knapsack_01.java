import java.util.ArrayList;
import java.util.List;

class Knapsack {

    record Item(int weight, int value) {}

    record Solution(int totalValue, List<Item> selectedItems) {
        int totalWeight() {
            return selectedItems.stream().mapToInt(Item::weight).sum();
        }
    }

    static Solution solve(int capacity, List<Item> items) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be non-negative");
        if (items == null) throw new IllegalArgumentException("Items must not be null");

        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);
            for (int remaining = 0; remaining <= capacity; remaining++) {
                dp[i][remaining] = dp[i - 1][remaining];
                if (item.weight() <= remaining) {
                    dp[i][remaining] = Math.max(dp[i][remaining],
                        dp[i - 1][remaining - item.weight()] + item.value());
                }
            }
        }

        List<Item> selected = new ArrayList<>();
        int remaining = capacity;
        for (int i = n; i > 0; i--) {
            if (dp[i][remaining] != dp[i - 1][remaining]) {
                Item item = items.get(i - 1);
                selected.add(item);
                remaining -= item.weight();
            }
        }

        return new Solution(dp[n][capacity], selected);
    }

    public static void main(String[] args) {
        List<Item> items = List.of(
            new Item(4, 1),
            new Item(5, 2),
            new Item(1, 3)
        );
        int capacity = 4;

        Solution solution = solve(capacity, items);
        System.out.println("Optimal value:  " + solution.totalValue());
        System.out.println("Total weight:   " + solution.totalWeight());
        System.out.println("Items selected: " + solution.selectedItems());
    }
}
