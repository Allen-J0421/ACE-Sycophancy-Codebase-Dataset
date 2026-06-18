final class Knapsack {
    private static final int[] SAMPLE_VALUES = {1, 2, 3};
    private static final int[] SAMPLE_WEIGHTS = {4, 5, 1};
    private static final int SAMPLE_CAPACITY = 4;

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        CapacityTable capacityTable = new CapacityTable(capacity);

        for (Item item : toItems(values, weights)) {
            includeItem(capacityTable, item);
        }
        return capacityTable.maxValueAt(capacity);
    }

    private static Item[] toItems(int[] values, int[] weights) {
        Item[] items = new Item[weights.length];

        for (int itemIndex = 0; itemIndex < weights.length; itemIndex++) {
            items[itemIndex] = new Item(weights[itemIndex], values[itemIndex]);
        }
        return items;
    }

    private static void includeItem(CapacityTable capacityTable, Item item) {
        for (int currentCapacity = capacityTable.capacity(); item.fitsWithin(currentCapacity); currentCapacity--) {
            capacityTable.setMaxValueAt(
                    currentCapacity,
                    bestValueForCapacity(capacityTable, currentCapacity, item)
            );
        }
    }

    private static int bestValueForCapacity(
            CapacityTable capacityTable,
            int currentCapacity,
            Item item
    ) {
        int valueWithoutItem = capacityTable.maxValueAt(currentCapacity);
        int valueWithItem = item.valueIfIncluded(capacityTable, currentCapacity);
        return Math.max(valueWithoutItem, valueWithItem);
    }

    private static final class CapacityTable {
        private final int[] maxValueByCapacity;

        private CapacityTable(int capacity) {
            maxValueByCapacity = new int[capacity + 1];
        }

        private int capacity() {
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
