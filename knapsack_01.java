import java.util.List;

class Knapsack {
    private static final KnapsackSolver SOLVER = new DynamicProgrammingSolver();

    private Knapsack() {
    }

    static int knapsack(int capacity, int[] values, int[] weights) {
        return maxValue(Problems.fromParallelArrays(capacity, values, weights));
    }

    static int maxValue(int capacity, Item... items) {
        return solve(capacity, items).optimalValue();
    }

    static int maxValue(int capacity, List<Item> items) {
        return solve(capacity, items).optimalValue();
    }

    static int maxValue(Problem problem) {
        return solve(problem).optimalValue();
    }

    static KnapsackSolution solve(int capacity, Item... items) {
        return solve(Problem.of(capacity, items));
    }

    static KnapsackSolution solve(int capacity, List<Item> items) {
        return solve(Problem.of(capacity, items));
    }

    static KnapsackSolution solve(Problem problem) {
        return SOLVER.solve(problem);
    }

    public static void main(String[] args) {
        Problem problem = Problems.sampleProblem();

        System.out.println(solve(problem).optimalValue());
    }
}
