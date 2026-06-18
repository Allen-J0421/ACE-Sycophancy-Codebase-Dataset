final class CapacityTable {
    private final int[] bestValues;

    CapacityTable(int capacity) {
        this.bestValues = new int[capacity + 1];
    }

    private int bestValueAt(int capacity) {
        return bestValues[capacity];
    }

    void consider(Item item) {
        for (int capacity = bestValues.length - 1; capacity >= item.weight(); capacity--) {
            int candidateValue = bestValueAt(capacity - item.weight()) + item.value();
            bestValues[capacity] = Math.max(bestValues[capacity], candidateValue);
        }
    }

    KnapsackSolution solutionFor(Problem problem) {
        return new KnapsackSolution(problem, bestValueAt(problem.capacity()));
    }
}
