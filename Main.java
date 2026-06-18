public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Graph Service with Event-Driven Architecture ===");
            Graph graph = GraphBuilder.undirected(6)
                    .addEdge(1, 2)
                    .addEdge(2, 0)
                    .addEdge(0, 3)
                    .addEdge(4, 5)
                    .build();

            GraphService service = new GraphService(graph);

            System.out.println("\n--- BFS with Events and Caching ---");
            TraversalConfig config = TraversalConfig.builder()
                    .withBFS()
                    .withCache(true)
                    .withEvents(true)
                    .addEventListener(event -> {
                        if (event.getType() == TraversalEvent.EventType.VERTEX_VISITED) {
                            System.out.println("  Event: Visited vertex " + event.getVertex()
                                    + " in component " + event.getComponentId());
                        }
                    })
                    .build();

            TraversalResult result = service.traverse(config);
            result.print();

            System.out.println("\n--- DFS Traversal ---");
            TraversalConfig dfsConfig = TraversalConfig.builder()
                    .withDFS()
                    .withCache(true)
                    .build();
            TraversalResult dfsResult = service.traverse(dfsConfig);
            dfsResult.print();

            System.out.println("\n--- Connectivity Queries ---");
            ConnectivityQuery connectivity = service.connectivity();
            System.out.println("Vertices 0 and 3 connected? " + connectivity.areConnected(0, 3));
            System.out.println("Vertices 0 and 5 connected? " + connectivity.areConnected(0, 5));
            System.out.println("Total components: " + connectivity.getTotalComponents());

            System.out.println("\n--- Degree Queries ---");
            GraphQuery.DegreeQuery degreeQuery = service.degree();
            System.out.println("Max degree: " + degreeQuery.getMaxDegree());
            System.out.println("Min degree: " + degreeQuery.getMinDegree());
            System.out.printf("Average degree: %.2f%n", degreeQuery.getAverageDegree());
            System.out.println("Highest degree vertices: " + degreeQuery.getHighestDegreeVertices(2));

            System.out.println("\n--- Vertex Queries ---");
            GraphQuery.VertexQuery vertexQuery = service.getQuery().vertices();
            System.out.println("All vertices: " + vertexQuery.allVertices());
            System.out.println("Leaf vertices (degree 0): " + vertexQuery.leafVertices());

            System.out.println("\n--- Fluent Query API ---");
            TraversalResult fluentResult = service.getQuery().traverse()
                    .usingBFS()
                    .withoutCache()
                    .execute();
            System.out.println("Traversal result: " + fluentResult.getVertices());

            System.out.println("\n--- Graph Analysis ---");
            GraphAnalyzer analyzer = new GraphAnalyzer(graph);
            GraphMetrics metrics = analyzer.analyze();
            metrics.print();

            System.out.println("\n--- Directed Graph Example ---");
            Graph directedGraph = GraphBuilder.directed(5)
                    .addEdge(0, 1)
                    .addEdge(0, 2)
                    .addEdge(1, 3)
                    .addEdge(2, 4)
                    .build();

            GraphService directedService = new GraphService(directedGraph);
            TraversalResult directedResult = directedService.traverse(
                    TraversalConfig.builder().withBFS().build()
            );
            directedResult.print();

            System.out.println("\n--- Cache Statistics ---");
            System.out.println("Cached items: " + service.getQuery().getCache().getSize());

            System.out.println("\n--- Error Handling ---");
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
