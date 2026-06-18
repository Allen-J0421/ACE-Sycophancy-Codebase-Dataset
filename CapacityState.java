final class CapacityState {
    private final int optimalValue;
    private final SelectionPath selectionPath;

    private CapacityState(int optimalValue, SelectionPath selectionPath) {
        this.optimalValue = optimalValue;
        this.selectionPath = selectionPath;
    }

    static CapacityState empty() {
        return new CapacityState(0, SelectionPath.empty());
    }

    CapacityState withItem(Item item) {
        return new CapacityState(optimalValue + item.value(), selectionPath.append(item));
    }

    boolean hasGreaterValueThan(CapacityState other) {
        return optimalValue > other.optimalValue;
    }

    KnapsackSolution toSolution(Problem problem) {
        return new KnapsackSolution(problem, selectionPath.toItems(), optimalValue);
    }
}
