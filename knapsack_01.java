final class Knapsack {

    private static final class Item {
        private final int weight;
        private final int value;

        private Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }
    }

    private static final class Problem {
        private final int capacity;
        private final Item[] items;

        private Problem(int capacity, Item[] items) {
            this.capacity = capacity;
            this.items = items;
        }

        private static Problem from(int capacity, int[] values, int[] weights) {
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
            return new Problem(capacity, items);
        }

        private int solve() {
            int[] bestValues = new int[capacity + 1];

            for (Item item : items) {
                consider(bestValues, item);
            }

            return bestValues[bestValues.length - 1];
        }

        private static void consider(int[] bestValues, Item item) {
            for (int currentCapacity = bestValues.length - 1; currentCapacity >= item.weight; currentCapacity--) {
                bestValues[currentCapacity] = Math.max(
                    bestValues[currentCapacity],
                    bestValues[currentCapacity - item.weight] + item.value
                );
            }
        }
    }

    private Knapsack() {
    }

    static int solve(int capacity, int[] values, int[] weights) {
        return Problem.from(capacity, values, weights).solve();
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

}
