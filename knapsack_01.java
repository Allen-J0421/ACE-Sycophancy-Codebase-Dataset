class Knapsack {
    private static final KnapsackSolver SOLVER = new DynamicProgrammingSolver();

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        return maxValue(Problem.fromParallelArrays(capacity, values, weights));
    }

    static int maxValue(int capacity, Item[] items) {
        return maxValue(Problem.of(capacity, items));
    }

    static int maxValue(Problem problem) {
        return SOLVER.solve(problem);
    }

    private static Problem sampleProblem() {
        return Problem.of(4,
                new Item(4, 1),
                new Item(5, 2),
                new Item(1, 3)
        );
    }

    public static void main(String[] args) {
        Problem problem = sampleProblem();

        System.out.println(maxValue(problem));
    }
}
