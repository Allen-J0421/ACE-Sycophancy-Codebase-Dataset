import java.util.Objects;

class Knapsack {

    static final class Item {
        final int weight;
        final int value;

        Item(int weight, int value) {
            if (weight < 0) {
                throw new IllegalArgumentException("weight must be non-negative");
            }
            if (value < 0) {
                throw new IllegalArgumentException("value must be non-negative");
            }

            this.weight = weight;
            this.value = value;
        }
    }

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        validateParallelArrays(capacity, values, weights);
        return maxValue(capacity, zipItems(values, weights));
    }

    static int maxValue(int capacity, Item[] items) {
        validateItems(capacity, items);

        int[] bestValueByCapacity = new int[capacity + 1];

        for (Item item : items) {
            for (int currentCapacity = capacity; currentCapacity >= item.weight; currentCapacity--) {
                int valueWithItem = bestValueByCapacity[currentCapacity - item.weight] + item.value;
                bestValueByCapacity[currentCapacity] =
                        Math.max(bestValueByCapacity[currentCapacity], valueWithItem);
            }
        }

        return bestValueByCapacity[capacity];
    }

    private static Item[] zipItems(int[] values, int[] weights) {
        Item[] items = new Item[values.length];

        for (int index = 0; index < values.length; index++) {
            items[index] = new Item(weights[index], values[index]);
        }

        return items;
    }

    private static void validateParallelArrays(int capacity, int[] values, int[] weights) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(weights, "weights must not be null");

        if (values.length != weights.length) {
            throw new IllegalArgumentException("values and weights must have the same length");
        }
    }

    private static void validateItems(int capacity, Item[] items) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        Objects.requireNonNull(items, "items must not be null");

        for (int index = 0; index < items.length; index++) {
            Objects.requireNonNull(items[index], "item at index " + index + " must not be null");
        }
    }

    public static void main(String[] args) {
        Item[] items = {
                new Item(4, 1),
                new Item(5, 2),
                new Item(1, 3)
        };
        int capacity = 4;

        System.out.println(maxValue(capacity, items));
    }
}
