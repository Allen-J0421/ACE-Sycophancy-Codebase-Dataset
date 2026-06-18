package graph.test;

import graph.algorithm.Dijkstra;
import graph.algorithm.Kruskal;
import graph.analysis.CentralityAnalyzer;
import graph.builder.GraphGenerator;
import graph.core.Graph;
import graph.model.Edge;
import graph.model.WeightedGraph;

import java.util.List;
import java.util.Map;

public class Iteration6Tests {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        ITERATION 6: WEIGHTED GRAPHS & ALGORITHMS           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        testWeightedGraphs();
        testDijkstra();
        testKruskal();
        testGraphGenerators();
        testCentrality();

        printResults();
    }

    private static void testWeightedGraphs() {
        System.out.println("─── WEIGHTED GRAPH TESTS ───\n");

        test("Create weighted graph", () -> {
            WeightedGraph g = new WeightedGraph(4);
            g.addWeightedEdge(0, 1, 2.5);
            assert g.hasEdge(0, 1);
            assert g.getEdgeWeight(0, 1) == 2.5;
        });

        test("Edge weight retrieval", () -> {
            WeightedGraph g = new WeightedGraph(3);
            g.addWeightedEdge(0, 1, 1.5);
            g.addWeightedEdge(1, 2, 3.0);
            assert g.getEdgeWeight(0, 1) == 1.5;
            assert g.getEdgeWeight(1, 2) == 3.0;
        });

        test("Get all edges", () -> {
            WeightedGraph g = new WeightedGraph(3);
            g.addWeightedEdge(0, 1, 1.0);
            g.addWeightedEdge(1, 2, 2.0);
            List<Edge> edges = g.getAllEdges();
            assert edges.size() == 2;
        });

        test("Edge equality", () -> {
            Edge e1 = new Edge(0, 1, 5.0);
            Edge e2 = new Edge(1, 0, 5.0);
            assert e1.equals(e2);
        });
    }

    private static void testDijkstra() {
        System.out.println("\n─── DIJKSTRA'S ALGORITHM TESTS ───\n");

        test("Shortest path calculation", () -> {
            WeightedGraph g = new WeightedGraph(4);
            g.addWeightedEdge(0, 1, 2.0);
            g.addWeightedEdge(1, 2, 1.0);
            g.addWeightedEdge(0, 3, 5.0);

            Dijkstra dijkstra = new Dijkstra(g);
            dijkstra.computeShortestPaths(0);

            assert dijkstra.getDistance(1) == 2.0;
            assert dijkstra.getDistance(2) == 3.0;
        });

        test("Path retrieval", () -> {
            WeightedGraph g = new WeightedGraph(4);
            g.addWeightedEdge(0, 1, 1.0);
            g.addWeightedEdge(1, 2, 1.0);

            Dijkstra dijkstra = new Dijkstra(g);
            dijkstra.computeShortestPaths(0);
            List<Integer> path = dijkstra.getPath(2);

            assert path.size() == 3;
            assert path.get(0) == 0;
            assert path.get(2) == 2;
        });

        test("Unreachable vertices", () -> {
            WeightedGraph g = new WeightedGraph(3);
            g.addWeightedEdge(0, 1, 1.0);

            Dijkstra dijkstra = new Dijkstra(g);
            dijkstra.computeShortestPaths(0);

            assert !dijkstra.hasPathTo(2);
        });
    }

    private static void testKruskal() {
        System.out.println("\n─── KRUSKAL'S ALGORITHM TESTS ───\n");

        test("Minimum spanning tree", () -> {
            WeightedGraph g = new WeightedGraph(4);
            g.addWeightedEdge(0, 1, 1.0);
            g.addWeightedEdge(1, 2, 2.0);
            g.addWeightedEdge(2, 3, 1.0);
            g.addWeightedEdge(0, 3, 4.0);

            Kruskal kruskal = new Kruskal(g);
            List<Edge> mst = kruskal.findMST();

            assert mst.size() == 3;
        });

        test("MST weight calculation", () -> {
            WeightedGraph g = new WeightedGraph(3);
            g.addWeightedEdge(0, 1, 2.0);
            g.addWeightedEdge(1, 2, 3.0);
            g.addWeightedEdge(0, 2, 5.0);

            Kruskal kruskal = new Kruskal(g);
            double weight = kruskal.getMSTWeight();

            assert weight == 5.0;
        });
    }

    private static void testGraphGenerators() {
        System.out.println("\n─── GRAPH GENERATOR TESTS ───\n");

        test("Complete graph generation", () -> {
            Graph g = GraphGenerator.completeGraph(4);
            assert g.getVertexCount() == 4;
            assert g.getEdgeCount() == 6;
        });

        test("Cycle graph generation", () -> {
            Graph g = GraphGenerator.cycleGraph(5);
            assert g.getVertexCount() == 5;
            assert g.getEdgeCount() == 5;
        });

        test("Path graph generation", () -> {
            Graph g = GraphGenerator.pathGraph(4);
            assert g.getVertexCount() == 4;
            assert g.getEdgeCount() == 3;
        });

        test("Bipartite graph generation", () -> {
            Graph g = GraphGenerator.bipartiteGraph(3, 3);
            assert g.getVertexCount() == 6;
            assert g.getEdgeCount() == 9;
        });

        test("Wheel graph generation", () -> {
            Graph g = GraphGenerator.wheelGraph(5);
            assert g.getVertexCount() == 5;
        });

        test("Weighted path generation", () -> {
            WeightedGraph g = GraphGenerator.weightedPathGraph(4);
            assert g.getVertexCount() == 4;
            assert g.getEdgeCount() == 3;
        });
    }

    private static void testCentrality() {
        System.out.println("\n─── CENTRALITY ANALYSIS TESTS ───\n");

        test("Degree centrality calculation", () -> {
            Graph g = GraphGenerator.completeGraph(4);
            CentralityAnalyzer analyzer = new CentralityAnalyzer(g);

            double centrality = analyzer.getDegreeCentrality(0);
            assert centrality > 0 && centrality <= 1.0;
        });

        test("Most central vertex by degree", () -> {
            Graph g = GraphGenerator.completeGraph(4);
            CentralityAnalyzer analyzer = new CentralityAnalyzer(g);

            int mostCentral = analyzer.getMostCentralVertexByDegree();
            assert mostCentral >= 0;
        });

        test("Closeness centrality", () -> {
            Graph g = GraphGenerator.pathGraph(4);
            CentralityAnalyzer analyzer = new CentralityAnalyzer(g);

            double closeness = analyzer.getClosenessCentrality(0);
            assert closeness >= 0;
        });

        test("All centralities retrieval", () -> {
            Graph g = GraphGenerator.cycleGraph(5);
            CentralityAnalyzer analyzer = new CentralityAnalyzer(g);

            Map<Integer, Double> degrees = analyzer.getAllDegreeCentralities();
            Map<Integer, Double> closeness = analyzer.getAllClosenessCentralities();

            assert degrees.size() == 5;
            assert closeness.size() == 5;
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
            System.out.println("✗ " + name + ": " + e.getClass().getSimpleName());
        }
    }

    private static void printResults() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              TEST RESULTS                                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Total Tests: " + testsRun);
        System.out.println("Passed: " + testsPassed + " (" + (100 * testsPassed / testsRun) + "%)");
        System.out.println("Failed: " + (testsRun - testsPassed) + "\n");
    }

    @FunctionalInterface
    interface TestCase {
        void run() throws Exception;
    }
}
