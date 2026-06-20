public final class FordFulkersonMaxFlow {
    private FordFulkersonMaxFlow() {
    }

    public static void main(String[] args) {
        int[][] graph = {
            {0, 16, 13, 0, 0, 0},
            {0, 0, 10, 12, 0, 0},
            {0, 4, 0, 0, 14, 0},
            {0, 0, 9, 0, 0, 20},
            {0, 0, 0, 7, 0, 4},
            {0, 0, 0, 0, 0, 0}
        };

        MaxFlowSolver solver = new MaxFlowSolver();
        int maxFlow = solver.calculateMaxFlow(graph, 0, 5);

        System.out.println("The maximum possible flow is " + maxFlow);
    }
}
