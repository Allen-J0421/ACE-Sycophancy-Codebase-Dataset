import java.util.ArrayList;
import java.util.List;

final class Knapsack {

    private record Item(int weight, int value) {
    }

    private record Problem(int capacity, List<Item> items) {
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

            ArrayList<Item> items = new ArrayList<>(values.length);
            for (int index = 0; index < values.length; index++) {
                int weight = weights[index];
                if (weight < 0) {
                    throw new IllegalArgumentException("weights must be non-negative");
                }
                if (weight <= capacity) {
                    items.add(new Item(weight, values[index]));
                }
            }
            return new Problem(capacity, List.copyOf(items));
        }

        private int solve() {
            int[] bestValues = new int[capacity + 1];

            for (Item item : items) {
                consider(bestValues, item);
            }

            return bestValues[capacity];
        }

        private static void consider(int[] bestValues, Item item) {
            for (int currentCapacity = bestValues.length - 1; currentCapacity >= item.weight(); currentCapacity--) {
                bestValues[currentCapacity] = Math.max(
                    bestValues[currentCapacity],
                    bestValues[currentCapacity - item.weight()] + item.value()
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
