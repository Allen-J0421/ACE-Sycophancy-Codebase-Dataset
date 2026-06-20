public final class FordFulkersonMaxFlow {
    private FordFulkersonMaxFlow() {
    }

    public static void main(String[] args) {
        FlowNetwork network = new FlowNetwork(new int[][] {
            {0, 16, 13, 0, 0, 0},
            {0, 0, 10, 12, 0, 0},
            {0, 4, 0, 0, 14, 0},
            {0, 0, 9, 0, 0, 20},
            {0, 0, 0, 7, 0, 4},
            {0, 0, 0, 0, 0, 0}
        });

        MaxFlowSolver solver = new MaxFlowSolver();
        FlowProblem problem = new FlowProblem(network, 0, 5);
        MaxFlowResult result = solver.solve(problem);

        System.out.println("The maximum possible flow is " + result.getMaxFlow());
    }
}
