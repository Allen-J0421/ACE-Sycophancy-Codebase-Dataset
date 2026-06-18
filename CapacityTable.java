final class CapacityTable {
    private final CapacityState[] states;

    CapacityTable(int capacity) {
        this.states = new CapacityState[capacity + 1];

        for (int index = 0; index < states.length; index++) {
            states[index] = CapacityState.empty();
        }
    }

    private CapacityState stateAt(int capacity) {
        return states[capacity];
    }

    void consider(Item item) {
        for (int capacity = states.length - 1; capacity >= item.weight(); capacity--) {
            CapacityState candidateState =
                    stateAt(capacity - item.weight()).withItem(item);

            if (candidateState.hasGreaterValueThan(stateAt(capacity))) {
                states[capacity] = candidateState;
            }
        }
    }

    KnapsackSolution solutionFor(Problem problem) {
        return stateAt(problem.capacity()).toSolution(problem);
    }
}
