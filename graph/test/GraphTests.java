package graph.test;

import graph.algorithm.BreadthFirstSearch;
import graph.algorithm.DepthFirstSearch;
import graph.analysis.ConnectedComponentsAnalyzer;
import graph.analysis.GraphAnalyzer;
import graph.config.GraphConfig;
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.exception.InvalidGraphException;
import graph.exception.InvalidVertexException;
import graph.utility.Logger;

import java.util.List;

public class GraphTests {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              GRAPH FRAMEWORK TEST SUITE                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        testGraphConstruction();
        testGraphOperations();
        testTraversal();
        testConnectivity();
        testExceptionHandling();
        testConfiguration();

        printResults();
    }

    private static void testGraphConstruction() {
        System.out.println("─── GRAPH CONSTRUCTION TESTS ───\n");

        test("Create simple graph", () -> {
            Graph g = new GraphBuilder(5).build();
            assert g.getVertexCount() == 5;
            assert g.getEdgeCount() == 0;
        });

        test("Create graph with config", () -> {
            GraphConfig config = GraphConfig.builder()
                    .enableLogging(false)
                    .cacheAnalysis(true)
                    .build();
            Graph g = new Graph(6, config);
            assert g.getVertexCount() == 6;
        });

        test("Add edges to graph", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            assert g.getEdgeCount() == 3;
        });
    }

    private static void testGraphOperations() {
        System.out.println("\n─── GRAPH OPERATION TESTS ───\n");

        test("Check edge existence", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .build();
            assert g.hasEdge(0, 1);
            assert !g.hasEdge(1, 2);
        });

        test("Get neighbors", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(0, 2)
                    .addEdge(0, 3)
                    .build();
            assert g.getNeighbors(0).size() == 3;
            assert g.getDegree(0) == 3;
        });

        test("Remove edge", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            g.removeEdge(0, 1);
            assert g.getEdgeCount() == 1;
            assert !g.hasEdge(0, 1);
        });

        test("Graph copy", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            Graph copy = (Graph) g.copy();
            assert copy.getVertexCount() == g.getVertexCount();
            assert copy.getEdgeCount() == g.getEdgeCount();
        });
    }

    private static void testTraversal() {
        System.out.println("\n─── TRAVERSAL TESTS ───\n");

        test("DFS Recursive", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            List<Integer> order = DepthFirstSearch.recursive().traverse(g);
            assert order.size() == 4;
        });

        test("DFS Iterative", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            List<Integer> order = DepthFirstSearch.iterative().traverse(g);
            assert order.size() == 4;
        });

        test("BFS", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(0, 2)
                    .addEdge(1, 3)
                    .build();
            List<Integer> order = new BreadthFirstSearch().traverse(g);
            assert order.size() == 4;
            assert order.get(0) == 0;
        });
    }

    private static void testConnectivity() {
        System.out.println("\n─── CONNECTIVITY TESTS ───\n");

        test("Connected graph", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            ConnectedComponentsAnalyzer analyzer = new ConnectedComponentsAnalyzer(g);
            assert analyzer.isConnected();
            assert analyzer.getComponentCount() == 1;
        });

        test("Disconnected graph", () -> {
            Graph g = new GraphBuilder(5)
                    .addEdge(0, 1)
                    .addEdge(2, 3)
                    .build();
            ConnectedComponentsAnalyzer analyzer = new ConnectedComponentsAnalyzer(g);
            assert !analyzer.isConnected();
            assert analyzer.getComponentCount() == 3;
        });

        test("Graph analysis", () -> {
            Graph g = new GraphBuilder(6)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(3, 4)
                    .build();
            GraphAnalyzer analyzer = new GraphAnalyzer(g);
            GraphAnalyzer.AnalysisResult result = analyzer.analyze();
            assert result.getVertexCount() == 6;
            assert result.getEdgeCount() == 3;
            assert !result.isConnected();
        });
    }

    private static void testExceptionHandling() {
        System.out.println("\n─── EXCEPTION HANDLING TESTS ───\n");

        test("Invalid vertex exception", () -> {
            Graph g = new Graph(5);
            try {
                g.addEdge(0, 10);
                assert false : "Should throw InvalidVertexException";
            } catch (InvalidVertexException e) {
                assert e.getVertex() == 10;
            }
        });

        test("Invalid graph exception", () -> {
            try {
                new Graph(-1);
                assert false : "Should throw InvalidGraphException";
            } catch (InvalidGraphException e) {
                assert true;
            }
        });

        test("Self-loop rejection", () -> {
            Graph g = new Graph(3);
            try {
                g.addEdge(0, 0);
                assert false : "Self-loop should be rejected";
            } catch (InvalidGraphException e) {
                assert true;
            }
        });
    }

    private static void testConfiguration() {
        System.out.println("\n─── CONFIGURATION TESTS ───\n");

        test("Custom config", () -> {
            GraphConfig config = GraphConfig.builder()
                    .enableLogging(true)
                    .cacheAnalysis(true)
                    .cacheTtlMs(30000)
                    .enableStats(true)
                    .build();
            assert config.isEnableLogging();
            assert config.isCacheAnalysis();
            assert config.getCacheTtlMs() == 30000;
        });

        test("Default config", () -> {
            GraphConfig config = GraphConfig.defaultConfig();
            assert !config.isAllowSelfLoops();
            assert config.isCacheAnalysis();
        });
    }

    private static void test(String name, TestCase testCase) {
        testsRun++;
        try {
            testCase.run();
            System.out.println("✓ " + name);
            testsPassed++;
        } catch (AssertionError e) {
            System.out.println("✗ " + name + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + name + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void printResults() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              TEST RESULTS                                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Total Tests: " + testsRun);
        System.out.println("Passed: " + testsPassed + " (" + (100 * testsPassed / testsRun) + "%)");
        System.out.println("Failed: " + (testsRun - testsPassed));
        System.out.println();
    }

    @FunctionalInterface
    interface TestCase {
        void run() throws Exception;
    }
}
