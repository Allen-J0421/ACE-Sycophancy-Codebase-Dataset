final class DynamicProgrammingSolver implements KnapsackSolver {

    @Override
    public KnapsackSolution solve(Problem problem) {
        CapacityTable capacityTable = new CapacityTable(problem.capacity());

        for (Item item : problem.items()) {
            capacityTable.consider(item);
        }

        return capacityTable.solutionFor(problem);
    }
}
