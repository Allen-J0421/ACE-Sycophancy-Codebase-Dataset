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

        int[][] dp = fillTable(capacity, items);
        List<Item> selected = backtrack(dp, items, capacity);
        return new Solution(dp[items.size()][capacity], selected);
    }

    private static int[][] fillTable(int capacity, List<Item> items) {
        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 0; i < n; i++) {
            Item item = items.get(i);
            for (int w = 0; w <= capacity; w++) {
                dp[i + 1][w] = dp[i][w];
                if (item.weight() <= w) {
                    dp[i + 1][w] = Math.max(dp[i + 1][w], dp[i][w - item.weight()] + item.value());
                }
            }
        }
        return dp;
    }

    private static List<Item> backtrack(int[][] dp, List<Item> items, int capacity) {
        List<Item> selected = new ArrayList<>();
        int w = capacity;
        for (int i = items.size() - 1; i >= 0; i--) {
            if (dp[i + 1][w] != dp[i][w]) {
                Item item = items.get(i);
                selected.add(item);
                w -= item.weight();
            }
        }
        return selected;
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
