final class FordFulkersonExample {
    private FordFulkersonExample() {
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
        FordFulkersonSolver solver = new FordFulkersonSolver(network, 0, 5);
        System.out.println("The maximum possible flow is " + solver.computeMaxFlow());
    }
}
