final class Knapsack {

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        int[] maxValueByCapacity = new int[capacity + 1];

        for (int itemIndex = 0; itemIndex < weights.length; itemIndex++) {
            int itemWeight = weights[itemIndex];
            int itemValue = values[itemIndex];

            for (int currentCapacity = capacity; currentCapacity >= itemWeight; currentCapacity--) {
                int valueWithoutItem = maxValueByCapacity[currentCapacity];
                int valueWithItem = maxValueByCapacity[currentCapacity - itemWeight] + itemValue;
                maxValueByCapacity[currentCapacity] = Math.max(valueWithoutItem, valueWithItem);
            }
        }
        return maxValueByCapacity[capacity];
    }

    public static void main(String[] args) {
        int[] values = {1, 2, 3};
        int[] weights = {4, 5, 1};
        int capacity = 4;

        System.out.println(knapsack(capacity, values, weights));
    }
}
