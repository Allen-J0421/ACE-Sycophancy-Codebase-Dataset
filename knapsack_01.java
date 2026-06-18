class Knapsack {

    static int knapsack(int capacity, int[] values, int[] weights) {
        validateInputs(capacity, values, weights);

        int[] bestByCapacity = new int[capacity + 1];

        for (int itemIndex = 0; itemIndex < weights.length; itemIndex++) {
            int itemWeight = weights[itemIndex];
            int itemValue = values[itemIndex];

            for (int currentCapacity = capacity; currentCapacity >= itemWeight; currentCapacity--) {
                bestByCapacity[currentCapacity] = Math.max(
                    bestByCapacity[currentCapacity],
                    bestByCapacity[currentCapacity - itemWeight] + itemValue
                );
            }
        }

        return bestByCapacity[capacity];
    }

    public static void main(String[] args) {
        int[] values = {1, 2, 3};
        int[] weights = {4, 5, 1};
        int capacity = 4;

        System.out.println(knapsack(capacity, values, weights));
    }

    private static void validateInputs(int capacity, int[] values, int[] weights) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }
        if (values == null || weights == null) {
            throw new IllegalArgumentException("values and weights must not be null");
        }
        if (values.length != weights.length) {
            throw new IllegalArgumentException("values and weights must have the same length");
        }
    }
}
