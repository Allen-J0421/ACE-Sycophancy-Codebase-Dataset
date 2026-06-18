public class GraphTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        Logger.setLevel(Logger.Level.INFO);
        Logger.info("Starting Graph Library Tests");

        testGraphCreation();
        testBFSTraversal();
        testDFSTraversal();
        testConnectivity();
        testGraphOperations();
        testValidation();
        testGraphHealth();

        printResults();
    }

    private static void testGraphCreation() {
        Logger.info("Testing graph creation...");
        try {
            Graph g = new UndirectedGraph(5);
            assertEquals(g.getVertexCount(), 5, "Graph vertex count");
            assertTrue(g.isValidVertex(0), "Vertex 0 valid");
            assertTrue(g.isValidVertex(4), "Vertex 4 valid");
            assertFalse(g.isValidVertex(5), "Vertex 5 invalid");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testGraphCreation failed: " + e.getMessage());
            failed++;
        }
    }

    private static void testBFSTraversal() {
        Logger.info("Testing BFS traversal...");
        try {
            Graph g = GraphBuilder.undirected(4)
                .addEdge(0, 1)
                .addEdge(0, 2)
                .addEdge(1, 3)
                .build();

            BreadthFirstSearch bfs = new BreadthFirstSearch();
            TraversalResult result = bfs.traverse(g);

            assertEquals(result.getVertices().size(), 4, "All vertices visited");
            assertEquals(result.getComponentCount(), 1, "Single component");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testBFSTraversal failed: " + e.getMessage());
            failed++;
        }
    }

    private static void testDFSTraversal() {
        Logger.info("Testing DFS traversal...");
        try {
            Graph g = GraphBuilder.undirected(4)
                .addEdge(0, 1)
                .addEdge(0, 2)
                .addEdge(1, 3)
                .build();

            DepthFirstSearch dfs = new DepthFirstSearch();
            TraversalResult result = dfs.traverse(g);

            assertEquals(result.getVertices().size(), 4, "All vertices visited");
            assertEquals(result.getComponentCount(), 1, "Single component");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testDFSTraversal failed: " + e.getMessage());
            failed++;
        }
    }

    private static void testConnectivity() {
        Logger.info("Testing connectivity...");
        try {
            Graph g = GraphBuilder.undirected(6)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(3, 4)
                .addEdge(4, 5)
                .build();

            ConnectivityQuery conn = new ConnectivityQuery(g);
            assertTrue(conn.areConnected(0, 2), "0 and 2 connected");
            assertFalse(conn.areConnected(0, 5), "0 and 5 not connected");
            assertEquals(conn.getTotalComponents(), 2, "Two components");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testConnectivity failed: " + e.getMessage());
            failed++;
        }
    }

    private static void testGraphOperations() {
        Logger.info("Testing graph operations...");
        try {
            Graph g = GraphBuilder.directed(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .build();

            GraphOperations ops = new GraphOperations(g);
            assertFalse(ops.hasCycle(), "No cycle in directed path");
            assertTrue(ops.isConnected(), "All vertices reachable from 0");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testGraphOperations failed: " + e.getMessage());
            failed++;
        }
    }

    private static void testValidation() {
        Logger.info("Testing validation...");
        try {
            Graph g = GraphBuilder.undirected(3)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .build();

            BreadthFirstSearch bfs = new BreadthFirstSearch();
            TraversalResult result = bfs.traverse(g);

            ResultValidator validator = new ResultValidator(result, g);
            ResultValidator.ValidationReport report = validator.validate();
            assertTrue(report.isValid(), "Result is valid");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testValidation failed: " + e.getMessage());
            failed++;
        }
    }

    private static void testGraphHealth() {
        Logger.info("Testing graph health checks...");
        try {
            Graph g = GraphBuilder.undirected(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .build();

            GraphHealthCheck health = new GraphHealthCheck(g);
            GraphHealthCheck.HealthReport report = health.check();
            assertTrue(report.isHealthy(), "Graph is healthy");
            passed++;
        } catch (AssertionError e) {
            Logger.error("testGraphHealth failed: " + e.getMessage());
            failed++;
        }
    }

    private static void assertEquals(Object actual, Object expected, String testName) {
        if (!actual.equals(expected)) {
            throw new AssertionError(testName + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(double actual, double expected, double delta, String testName) {
        if (Math.abs(actual - expected) > delta) {
            throw new AssertionError(testName + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (!condition) {
            throw new AssertionError(testName + " failed: expected true but got false");
        }
    }

    private static void assertFalse(boolean condition, String testName) {
        if (condition) {
            throw new AssertionError(testName + " failed: expected false but got true");
        }
    }

    private static void printResults() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           TEST RESULTS                 ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║ Passed: %-33d ║%n", passed);
        System.out.printf("║ Failed: %-33d ║%n", failed);
        System.out.printf("║ Total:  %-33d ║%n", passed + failed);
        System.out.println("╚════════════════════════════════════════╝");
        System.exit(failed > 0 ? 1 : 0);
    }
}
