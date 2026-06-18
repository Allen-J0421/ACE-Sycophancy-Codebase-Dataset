public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Undirected Graph with BFS ===");
            Graph undirectedGraph = GraphBuilder.undirected(6)
                    .addEdge(1, 2)
                    .addEdge(2, 0)
                    .addEdge(0, 3)
                    .addEdge(4, 5)
                    .build();

            TraversalResult bfsResult = new TraversalBuilder(undirectedGraph)
                    .withStrategy(new BreadthFirstSearch())
                    .addVisitor(v -> System.out.println("  Visiting vertex: " + v))
                    .execute();
            bfsResult.print();

            System.out.println("\n=== Undirected Graph with DFS ===");
            TraversalResult dfsResult = new TraversalBuilder(undirectedGraph)
                    .withStrategy(new DepthFirstSearch())
                    .execute();
            dfsResult.print();

            System.out.println("\n=== Directed Graph with BFS ===");
            Graph directedGraph = GraphBuilder.directed(5)
                    .addEdge(0, 1)
                    .addEdge(0, 2)
                    .addEdge(1, 3)
                    .addEdge(2, 4)
                    .build();

            TraversalResult directedBfsResult = new TraversalBuilder(directedGraph)
                    .withStrategy(new BreadthFirstSearch())
                    .execute();
            directedBfsResult.print();

            System.out.println("\n=== Graph Analysis ===");
            GraphAnalyzer analyzer = new GraphAnalyzer(undirectedGraph);
            GraphMetrics metrics = analyzer.analyze();
            metrics.print();

            System.out.println("\n=== Error Handling Demo ===");
            demonstrateErrorHandling();
        } catch (GraphException e) {
            System.err.println("Graph error: " + e.getMessage());
        }
    }

    private static void demonstrateErrorHandling() {
        try {
            Graph graph = new UndirectedGraph(3);
            graph.addEdge(0, 5);
        } catch (GraphException.InvalidVertexException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }
}
