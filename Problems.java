import java.util.List;
import java.util.Objects;

final class Problems {
    private Problems() {
    }

    static Problem fromParallelArrays(int capacity, int[] values, int[] weights) {
        validateParallelArrays(values, weights);
        return Problem.of(capacity, toItems(values, weights));
    }

    static Problem sampleProblem() {
        return Problem.of(4,
                new Item(4, 1),
                new Item(5, 2),
                new Item(1, 3)
        );
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
