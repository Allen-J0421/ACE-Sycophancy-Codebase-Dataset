import java.util.Arrays;
import java.util.Objects;

class Knapsack {

    record Item(int weight, int value) {
        Item {
            requireNonNegative(weight, "weight");
            requireNonNegative(value, "value");
        }
    }

    record Problem(int capacity, Item[] items) {
        Problem {
            requireNonNegative(capacity, "capacity");
            items = copyItems(items);
        }

        static Problem fromParallelArrays(int capacity, int[] values, int[] weights) {
            validateParallelArrays(values, weights);
            return new Problem(capacity, itemsFrom(values, weights));
        }

        int solve() {
            return new Solver(this).solve();
        }

        @Override
        public Item[] items() {
            return Arrays.copyOf(items, items.length);
        }
    }

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        return Problem.fromParallelArrays(capacity, values, weights).solve();
    }

    static int maxValue(int capacity, Item[] items) {
        return new Problem(capacity, items).solve();
    }

    private static Item[] itemsFrom(int[] values, int[] weights) {
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

    private static Item[] copyItems(Item[] items) {
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

    private static Problem sampleProblem() {
        return new Problem(4, new Item[] {
                new Item(4, 1),
                new Item(5, 2),
                new Item(1, 3)
        });
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
