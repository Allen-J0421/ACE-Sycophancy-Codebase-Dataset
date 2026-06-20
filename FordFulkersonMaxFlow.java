public final class FordFulkersonMaxFlow {
    private FordFulkersonMaxFlow() {
    }

    public static void main(String[] args) {
        FlowProblem problem = FlowProblem.fromCapacities(new int[][] {
            {0, 16, 13, 0, 0, 0},
            {0, 0, 10, 12, 0, 0},
            {0, 4, 0, 0, 14, 0},
            {0, 0, 9, 0, 0, 20},
            {0, 0, 0, 7, 0, 4},
            {0, 0, 0, 0, 0, 0}
        }, 0, 5);

        MaxFlowAlgorithm algorithm = new FordFulkersonSolver();
        MaxFlowResult result = algorithm.solve(problem);

        System.out.println("The maximum possible flow is " + result.getMaxFlow());
    }
}
