import java.util.List;
import java.util.Objects;

class Knapsack {

    record Item(int weight, int value) {
        Item {
            requireNonNegative(weight, "weight");
            requireNonNegative(value, "value");
        }
    }

    record Problem(int capacity, List<Item> items) {
        Problem {
            requireNonNegative(capacity, "capacity");
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

        int solve() {
            return new Solver(this).solve();
        }
    }

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        return Problem.fromParallelArrays(capacity, values, weights).solve();
    }

    static int maxValue(int capacity, Item[] items) {
        return Problem.of(capacity, items).solve();
    }

    private static List<Item> toItems(int[] values, int[] weights) {
        Item[] items = new Item[values.length];

        for (int index = 0; index < values.length; index++) {
            items[index] = new Item(weights[index], values[index]);
        }

        return List.of(items);
    }

    private static void validateParallelArrays(int[] values, int[] weights) {
        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(weights, "weights must not be null");

        if (values.length != weights.length) {
            throw new IllegalArgumentException("values and weights must have the same length");
        }
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }

    private static Problem sampleProblem() {
        return Problem.of(4,
                new Item(4, 1),
                new Item(5, 2),
                new Item(1, 3)
        );
    }

    private static final class Solver {
        private final Problem problem;
        private final CapacityTable capacityTable;

        private Solver(Problem problem) {
            this.problem = problem;
            this.capacityTable = new CapacityTable(problem.capacity());
        }

        private int solve() {
            for (Item item : problem.items()) {
                capacityTable.consider(item);
            }

            return capacityTable.bestValueAt(problem.capacity());
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

        private void consider(Item item) {
            for (int capacity = bestValues.length - 1; capacity >= item.weight(); capacity--) {
                int candidateValue = bestValueAt(capacity - item.weight()) + item.value();
                bestValues[capacity] = Math.max(bestValues[capacity], candidateValue);
            }
        }
    }

    public static void main(String[] args) {
        Problem problem = sampleProblem();

        System.out.println(problem.solve());
    }
}
