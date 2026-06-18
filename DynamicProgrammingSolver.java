final class DynamicProgrammingSolver implements KnapsackSolver {

    @Override
    public int solve(Problem problem) {
        CapacityTable capacityTable = new CapacityTable(problem.capacity());

        for (Item item : problem.items()) {
            capacityTable.consider(item);
        }

        return capacityTable.bestValueAt(problem.capacity());
    }
}
