final class Knapsack {
    private static final int[] SAMPLE_VALUES = {1, 2, 3};
    private static final int[] SAMPLE_WEIGHTS = {4, 5, 1};
    private static final int SAMPLE_CAPACITY = 4;

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        return new Solver(capacity, Item.from(values, weights)).solve();
    }

    private static final class Solver {
        private final CapacityTable capacityTable;
        private final Item[] items;

        private Solver(int capacity, Item[] items) {
            capacityTable = new CapacityTable(capacity);
            this.items = items;
        }

        private int solve() {
            for (Item item : items) {
                include(item);
            }
            return capacityTable.maxValueAt(capacityTable.maximumCapacity());
        }

        private void include(Item item) {
            for (
                    int currentCapacity = capacityTable.maximumCapacity();
                    item.fitsWithin(currentCapacity);
                    currentCapacity--
            ) {
                capacityTable.setMaxValueAt(
                        currentCapacity,
                        bestValueForCapacity(currentCapacity, item)
                );
            }
        }

        private int bestValueForCapacity(int currentCapacity, Item item) {
            int valueWithoutItem = capacityTable.maxValueAt(currentCapacity);
            int valueWithItem = item.valueIfIncluded(capacityTable, currentCapacity);
            return Math.max(valueWithoutItem, valueWithItem);
        }
    }

    private static final class CapacityTable {
        private final int[] maxValueByCapacity;

        private CapacityTable(int capacity) {
            maxValueByCapacity = new int[capacity + 1];
        }

        private int maximumCapacity() {
            return maxValueByCapacity.length - 1;
        }

        private int maxValueAt(int capacity) {
            return maxValueByCapacity[capacity];
        }

        private void setMaxValueAt(int capacity, int value) {
            maxValueByCapacity[capacity] = value;
        }
    }

    private static final class Item {
        private final int weight;
        private final int value;

        private Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }

        private static Item[] from(int[] values, int[] weights) {
            Item[] items = new Item[weights.length];

            for (int itemIndex = 0; itemIndex < weights.length; itemIndex++) {
                items[itemIndex] = new Item(weights[itemIndex], values[itemIndex]);
            }
            return items;
        }

        private boolean fitsWithin(int capacity) {
            return weight <= capacity;
        }

        private int valueIfIncluded(CapacityTable capacityTable, int capacity) {
            return capacityTable.maxValueAt(capacity - weight) + value;
        }
    }

    public static void main(String[] args) {
        System.out.println(knapsack(SAMPLE_CAPACITY, SAMPLE_VALUES, SAMPLE_WEIGHTS));
    }
}
