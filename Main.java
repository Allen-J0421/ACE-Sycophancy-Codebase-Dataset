public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║   ENTERPRISE GRAPH LIBRARY - FULL DEMO    ║");
            System.out.println("╚════════════════════════════════════════════╝");

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

            System.out.println("\n--- Graph Operations ---");
            demonstrateGraphOperations(graph);

            System.out.println("\n--- Traversal Profiles ---");
            demonstrateProfiles(service, graph);

            System.out.println("\n--- Streaming API ---");
            demonstrateStreamingAPI(service);

            System.out.println("\n--- Result Validation ---");
            demonstrateValidation(service, graph);

            System.out.println("\n--- Error Handling ---");
            demonstrateErrorHandling();

        } catch (GraphException e) {
            System.err.println("Graph error: " + e.getMessage());
        }
    }

    private static void demonstrateGraphOperations(Graph graph) {
        GraphOperations ops = new GraphOperations(graph);
        System.out.println("Is connected? " + ops.isConnected());
        System.out.println("Has cycle? " + ops.hasCycle());
        System.out.println("Isolated vertices: " + ops.findIsolatedVertices());
        System.out.println("Maximum distance: " + ops.getMaximumDistance());
        System.out.println("Graph density: " + String.format("%.2f", ops.getDensity()));
        System.out.println("Triangle count: " + ops.countTriangles());
    }

    private static void demonstrateProfiles(GraphService service, Graph graph) {
        for (TraversalProfile.Profile profile : TraversalProfile.Profile.values()) {
            System.out.println("\n  Profile: " + profile.getName());
            service.traverseWithProfile(profile);
            service.getCurrentProfile().printReport();
        }
    }

    private static void demonstrateStreamingAPI(GraphService service) {
        TraversalResult result = service.getQuery().traverse()
                .usingBFS()
                .withoutCache()
                .execute();

        System.out.println("All vertices: " + result.stream().collect());
        System.out.println("Even vertices: " + result.stream()
                .filter(v -> v % 2 == 0)
                .collect());
        System.out.println("First 3 vertices: " + result.stream()
                .limit(3)
                .collect());
        System.out.println("Sum of vertices: " + result.stream().sum());
        System.out.println("Average: " + result.stream().average());
        System.out.println("Max: " + result.stream().max());
        System.out.println("Count: " + result.stream().count());
    }

    private static void demonstrateValidation(GraphService service, Graph graph) {
        TraversalResult result = service.traverse(
                TraversalConfig.builder()
                        .withBFS()
                        .withCache(false)
                        .build()
        );
        ResultValidator validator = new ResultValidator(result, graph);
        validator.validate().print();
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
