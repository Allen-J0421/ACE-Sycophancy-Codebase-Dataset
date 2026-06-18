final class Knapsack {

    private static final class Item {
        private final int weight;
        private final int value;

        private Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }
    }

    private Knapsack() {
    }

    static int solve(int capacity, int[] values, int[] weights) {
        return solve(capacity, buildItems(capacity, values, weights));
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int[] values = {1, 2, 3};
        int[] weights = {4, 5, 1};
        int capacity = 4;

        System.out.println(solve(capacity, values, weights));
    }

    private static Item[] buildItems(int capacity, int[] values, int[] weights) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }
        if (values == null || weights == null) {
            throw new IllegalArgumentException("values and weights must not be null");
        }
        if (values.length != weights.length) {
            throw new IllegalArgumentException("values and weights must have the same length");
        }

        Item[] items = new Item[values.length];
        for (int index = 0; index < values.length; index++) {
            int weight = weights[index];
            if (weight < 0) {
                throw new IllegalArgumentException("weights must be non-negative");
            }
            items[index] = new Item(weight, values[index]);
        }
        return items;
    }

    private static int solve(int capacity, Item[] items) {
        int[] bestValueByCapacity = new int[capacity + 1];

        for (Item item : items) {
            applyItem(bestValueByCapacity, item);
        }

        return bestValueByCapacity[capacity];
    }

    private static void applyItem(int[] bestValueByCapacity, Item item) {
        for (int currentCapacity = bestValueByCapacity.length - 1; currentCapacity >= item.weight; currentCapacity--) {
            bestValueByCapacity[currentCapacity] = Math.max(
                bestValueByCapacity[currentCapacity],
                bestValueByCapacity[currentCapacity - item.weight] + item.value
            );
        }
    }
}
