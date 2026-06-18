public class Main {
    public static void main(String[] args) {
        try {
            Logger.setLevel(Logger.Level.INFO);
            Logger.info("Starting Enterprise Graph Library - Level 7 Demo");

            System.out.println("╔═══════════════════════════════════════════════════╗");
            System.out.println("║   ENTERPRISE GRAPH LIBRARY - LEVEL 7 DEMO       ║");
            System.out.println("║  (Configuration + Metrics + Resilience + Plugins)║");
            System.out.println("╚═══════════════════════════════════════════════════╝");

            demonstrateLevel7Features();

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

            System.out.println("\n--- Logging & Health Checks ---");
            demonstrateLoggingAndHealth(graph);

            System.out.println("\n--- Benchmarking ---");
            demonstrateBenchmarking(graph);

            System.out.println("\n--- Serialization ---");
            try {
                demonstrateSerialization(graph);
            } catch (java.io.IOException e) {
                Logger.error("Serialization failed", e);
            }

            System.out.println("\n--- Concurrent Traversal ---");
            demonstrateConcurrency(graph);

            System.out.println("\n--- Error Handling ---");
            demonstrateErrorHandling();

        } catch (GraphException e) {
            Logger.error("Graph error: " + e.getMessage());
            System.err.println("Graph error: " + e.getMessage());
        }
    }

    private static void demonstrateLevel7Features() {
        System.out.println("\n--- Configuration Management ---");
        demonstrateConfiguration();

        System.out.println("\n--- Metrics Collection ---");
        demonstrateMetrics();

        System.out.println("\n--- Retry Policy ---");
        demonstrateRetryPolicy();

        System.out.println("\n--- Circuit Breaker ---");
        demonstrateCircuitBreaker();

        System.out.println("\n--- Plugin Architecture ---");
        demonstratePlugins();
    }

    private static void demonstrateConfiguration() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        System.out.println("Cache enabled: " + config.getBoolean("cache.enabled", false));
        System.out.println("Cache TTL: " + config.getLong("cache.ttl.millis", 0) + " ms");
        System.out.println("Logging level: " + config.getString("logging.level", "INFO"));
        System.out.println("Concurrent threads: " + config.getInt("concurrent.threads", 0));

        config.set("custom.setting", "custom_value");
        System.out.println("Custom setting: " + config.getString("custom.setting", "N/A"));
    }

    private static void demonstrateMetrics() {
        MetricsCollector metrics = MetricsCollector.getInstance();
        metrics.reset();

        metrics.startTiming("operation1");
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        metrics.recordTiming("operation1");

        metrics.incrementCounter("requests");
        metrics.addToCounter("requests", 5);

        System.out.println("Operation1 count: " + metrics.getCounter("operation1.count"));
        System.out.println("Total requests: " + metrics.getCounter("requests"));
        System.out.printf("Avg operation1 time: %.3f ms%n", metrics.getAverageTimingMillis("operation1"));
    }

    private static void demonstrateRetryPolicy() {
        RetryPolicy policy = new RetryPolicy.Builder()
            .maxRetries(3)
            .retryDelayMillis(100)
            .exponentialBackoff(2.0, 1000)
            .build();

        int attempts = 0;
        try {
            policy.executeWithRetry(() -> {
                System.out.println("Attempting operation...");
                return "Success";
            });
            System.out.println("Retry policy executed successfully");
        } catch (Exception e) {
            Logger.error("Retry policy failed", e);
        }
    }

    private static void demonstrateCircuitBreaker() {
        CircuitBreaker breaker = new CircuitBreaker("demo", 3, 1000);

        System.out.println("Initial state: " + breaker.getState());

        for (int i = 0; i < 5; i++) {
            final int attempt = i;
            try {
                breaker.execute(() -> {
                    if (attempt < 3) throw new Exception("Simulated failure");
                    return "Success";
                });
                System.out.println("Attempt " + (attempt + 1) + ": Success");
            } catch (Exception e) {
                System.out.println("Attempt " + (attempt + 1) + ": Failed - " + breaker.getState());
            }
        }

        breaker.reset();
        System.out.println("After reset: " + breaker.getState());
    }

    private static void demonstratePlugins() {
        PluginRegistry registry = PluginRegistry.getInstance();

        GraphPlugin loggingPlugin = new LoggingPlugin();
        registry.register(loggingPlugin);

        System.out.println("Registered plugins: " + registry.getPluginNames());
        System.out.println("Plugin count: " + registry.getPluginCount());

        Graph graph = GraphBuilder.undirected(3).addEdge(0, 1).addEdge(1, 2).build();
        GraphService service = new GraphService(graph);
        registry.executeAll(service);
    }

    private static void demonstrateLoggingAndHealth(Graph graph) {
        Logger.debug("Starting health check on graph");
        GraphHealthCheck health = new GraphHealthCheck(graph);
        health.check().print();
    }

    private static void demonstrateBenchmarking(Graph graph) {
        GraphBenchmark benchmark = new GraphBenchmark(graph);
        benchmark.benchmarkTraversal(new BreadthFirstSearch(), "BFS", 100);
        benchmark.benchmarkTraversal(new DepthFirstSearch(), "DFS", 100);
        benchmark.printResults();
    }

    private static void demonstrateSerialization(Graph graph) throws java.io.IOException {
        Logger.info("Serializing graph to JSON format");
        String json = GraphSerializer.toJSON(graph);
        System.out.println("JSON representation (first 200 chars):");
        System.out.println(json.substring(0, Math.min(200, json.length())) + "...");

        Logger.info("Serializing graph to DOT format");
        String dot = GraphSerializer.toDOT(graph);
        System.out.println("\nDOT representation:");
        System.out.println(dot);
    }

    private static void demonstrateConcurrency(Graph graph) {
        Logger.info("Testing concurrent traversal with 4 threads");
        ConcurrentGraphTraversal concurrent = new ConcurrentGraphTraversal(4);
        TraversalResult result = concurrent.traverse(graph);
        System.out.println("Concurrent traversal result: " + result.getVertices());
        System.out.println("Components: " + result.getComponentCount());
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
