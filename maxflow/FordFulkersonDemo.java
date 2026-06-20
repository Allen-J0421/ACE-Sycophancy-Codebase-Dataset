package maxflow;

public final class FordFulkersonDemo {
    private FordFulkersonDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[][] graph = {
            { 0, 16, 13, 0, 0, 0 },
            { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },
            { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },
            { 0, 0, 0, 0, 0, 0 }
        };

        FlowNetwork network = FlowNetwork.fromMatrix(graph);
        FlowProblem problem = FlowProblem.of(network, 0, 5);
        MaxFlowSolver solver = new FordFulkersonSolver(problem);
        MaxFlowResult result = solver.solve();
        System.out.println("The maximum possible flow is " + result.value());
    }
}
