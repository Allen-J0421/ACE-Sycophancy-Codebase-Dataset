class Main {
    public static void main(String[] args) {
        Graph graph = GraphBuilder.withVertexCount(5)
            .addEdge(0, 1, 4)
            .addEdge(0, 2, 8)
            .addEdge(1, 4, 6)
            .addEdge(1, 2, 3)
            .addEdge(2, 3, 2)
            .addEdge(3, 4, 10)
            .build();

        AlgorithmConfig config = AlgorithmConfig.create(0);
        DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
        ShortestPathResult result = solver.solve(graph, 0);

        ResultFormatter formatter = new ResultFormatter(config);
        formatter.printDistances(result);
    }
}
