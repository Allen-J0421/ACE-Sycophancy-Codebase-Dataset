final class Knapsack {
    private static final int[] SAMPLE_VALUES = {1, 2, 3};
    private static final int[] SAMPLE_WEIGHTS = {4, 5, 1};
    private static final int SAMPLE_CAPACITY = 4;

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        int[] maxValueByCapacity = new int[capacity + 1];

        for (int itemIndex = 0; itemIndex < weights.length; itemIndex++) {
            includeItem(maxValueByCapacity, capacity, weights[itemIndex], values[itemIndex]);
        }
        return maxValueByCapacity[capacity];
    }

    private static void includeItem(
            int[] maxValueByCapacity,
            int capacity,
            int itemWeight,
            int itemValue
    ) {
        for (int currentCapacity = capacity; currentCapacity >= itemWeight; currentCapacity--) {
            int valueWithoutItem = maxValueByCapacity[currentCapacity];
            int valueWithItem = maxValueByCapacity[currentCapacity - itemWeight] + itemValue;
            maxValueByCapacity[currentCapacity] = Math.max(valueWithoutItem, valueWithItem);
        }
    }

    public static void main(String[] args) {
        System.out.println(knapsack(SAMPLE_CAPACITY, SAMPLE_VALUES, SAMPLE_WEIGHTS));
    }
}
