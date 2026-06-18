import java.util.Arrays;
import java.util.Objects;

class Knapsack {

    record Item(int weight, int value) {
        Item {
            requireNonNegative(weight, "weight");
            requireNonNegative(value, "value");
        }
    }

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        return maxValue(capacity, itemsFrom(values, weights));
    }

    static int maxValue(int capacity, Item[] items) {
        return new Solver(capacity, items).solve();
    }

    private static Item[] itemsFrom(int[] values, int[] weights) {
        validateParallelArrays(values, weights);

        Item[] items = new Item[values.length];

        for (int index = 0; index < values.length; index++) {
            items[index] = new Item(weights[index], values[index]);
        }

        return items;
    }

    private static void validateParallelArrays(int[] values, int[] weights) {
        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(weights, "weights must not be null");

        if (values.length != weights.length) {
            throw new IllegalArgumentException("values and weights must have the same length");
        }
    }

    private static Item[] validateAndCopyItems(Item[] items) {
        Objects.requireNonNull(items, "items must not be null");

        for (int index = 0; index < items.length; index++) {
            Objects.requireNonNull(items[index], "item at index " + index + " must not be null");
        }

        return Arrays.copyOf(items, items.length);
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }

    private static Item[] sampleItems() {
        return new Item[] {
                new Item(4, 1),
                new Item(5, 2),
                new Item(1, 3)
        };
    }

    private static final class Solver {
        private final int capacity;
        private final Item[] items;
        private final CapacityTable capacityTable;

        private Solver(int capacity, Item[] items) {
            requireNonNegative(capacity, "capacity");
            this.capacity = capacity;
            this.items = validateAndCopyItems(items);
            this.capacityTable = new CapacityTable(capacity);
        }

        private int solve() {
            for (Item item : items) {
                consider(item);
            }

            return capacityTable.bestValueAt(capacity);
        }

        private void consider(Item item) {
            for (int currentCapacity = capacity; currentCapacity >= item.weight(); currentCapacity--) {
                int candidateValue =
                        capacityTable.bestValueAt(currentCapacity - item.weight()) + item.value();
                capacityTable.update(currentCapacity, candidateValue);
            }
        }
    }

    private static final class CapacityTable {
        private final int[] bestValues;

        private CapacityTable(int capacity) {
            this.bestValues = new int[capacity + 1];
        }

        private int bestValueAt(int capacity) {
            return bestValues[capacity];
        }

        private void update(int capacity, int candidateValue) {
            bestValues[capacity] = Math.max(bestValues[capacity], candidateValue);
        }
    }

    public static void main(String[] args) {
        Item[] items = sampleItems();
        int capacity = 4;

        System.out.println(maxValue(capacity, items));
    }
}
