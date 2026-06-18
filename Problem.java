import java.util.List;
import java.util.Objects;

record Problem(int capacity, List<Item> items) {
    Problem {
        Validation.requireNonNegative(capacity, "capacity");
        Objects.requireNonNull(items, "items must not be null");
        items = List.copyOf(items);
    }

    static Problem of(int capacity, Item... items) {
        Objects.requireNonNull(items, "items must not be null");
        return new Problem(capacity, List.of(items));
    }

    static Problem fromParallelArrays(int capacity, int[] values, int[] weights) {
        validateParallelArrays(values, weights);
        return new Problem(capacity, toItems(values, weights));
    }

    private static void validateParallelArrays(int[] values, int[] weights) {
        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(weights, "weights must not be null");

        if (values.length != weights.length) {
            throw new IllegalArgumentException("values and weights must have the same length");
        }
    }

    private static List<Item> toItems(int[] values, int[] weights) {
        Item[] items = new Item[values.length];

        for (int index = 0; index < values.length; index++) {
            items[index] = new Item(weights[index], values[index]);
        }

        return List.of(items);
    }
}
