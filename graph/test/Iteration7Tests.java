package graph.test;

import graph.algorithm.BellmanFord;
import graph.algorithm.StronglyConnectedComponents;
import graph.algorithm.TopologicalSorter;
import graph.builder.AdvancedGraphBuilder;
import graph.model.DirectedGraph;
import graph.model.WeightedGraph;

import java.util.List;

public class Iteration7Tests {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     ITERATION 7: DIRECTED GRAPHS & ADVANCED ALGORITHMS      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        testDirectedGraphs();
        testSCC();
        testBellmanFord();
        testTopologicalSort();
        testAdvancedBuilders();

        printResults();
    }

    private static void testDirectedGraphs() {
        System.out.println("─── DIRECTED GRAPH TESTS ───\n");

        test("Create directed graph", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 2.0);
            assert g.hasEdge(0, 1);
            assert !g.hasEdge(1, 0);
        });

        test("In-degree and out-degree", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(0, 2, 1.0);
            g.addDirectedEdge(3, 0, 1.0);
            assert g.getOutDegree(0) == 2;
            assert g.getInDegree(0) == 1;
        });

        test("Reverse graph", () -> {
            DirectedGraph g = new DirectedGraph(3);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, 2.0);
            DirectedGraph rev = (DirectedGraph) g.reverse();
            assert rev.hasEdge(1, 0);
            assert rev.hasEdge(2, 1);
        });
    }

    private static void testSCC() {
        System.out.println("\n─── STRONGLY CONNECTED COMPONENTS TESTS ───\n");

        test("Single SCC", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, 1.0);
            g.addDirectedEdge(2, 0, 1.0);
            g.addDirectedEdge(2, 3, 1.0);

            StronglyConnectedComponents scc = new StronglyConnectedComponents(g);
            assert scc.isSCC(0, 1);
            assert scc.isSCC(1, 2);
            assert !scc.isSCC(2, 3);
        });

        test("Multiple SCCs", () -> {
            DirectedGraph g = new DirectedGraph(5);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, 1.0);
            g.addDirectedEdge(2, 0, 1.0);
            g.addDirectedEdge(2, 3, 1.0);
            g.addDirectedEdge(3, 4, 1.0);

            StronglyConnectedComponents scc = new StronglyConnectedComponents(g);
            List<List<Integer>> sccs = scc.getSCCs();
            assert sccs.size() >= 2;
        });
    }

    private static void testBellmanFord() {
        System.out.println("\n─── BELLMAN-FORD ALGORITHM TESTS ───\n");

        test("Shortest paths (positive weights)", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, 2.0);
            g.addDirectedEdge(0, 3, 5.0);

            BellmanFord bf = new BellmanFord(g);
            bf.computeShortestPaths(0);

            assert bf.getDistance(1) == 1.0;
            assert bf.getDistance(2) == 3.0;
        });

        test("Negative weights", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, -2.0);
            g.addDirectedEdge(0, 3, 4.0);

            BellmanFord bf = new BellmanFord(g);
            bf.computeShortestPaths(0);

            assert bf.getDistance(2) == -1.0;
        });

        test("Path retrieval", () -> {
            DirectedGraph g = new DirectedGraph(3);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, 2.0);

            BellmanFord bf = new BellmanFord(g);
            bf.computeShortestPaths(0);
            List<Integer> path = bf.getPath(2);

            assert path.size() == 3;
            assert path.get(0) == 0;
            assert path.get(2) == 2;
        });
    }

    private static void testTopologicalSort() {
        System.out.println("\n─── TOPOLOGICAL SORT TESTS ───\n");

        test("DFS topological sort", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(0, 2, 1.0);
            g.addDirectedEdge(1, 3, 1.0);
            g.addDirectedEdge(2, 3, 1.0);

            TopologicalSorter sorter = new TopologicalSorter(g);
            List<Integer> order = sorter.sort();

            assert order.size() == 4;
            assert order.get(0) == 0;
        });

        test("Kahn topological sort", () -> {
            DirectedGraph g = new DirectedGraph(4);
            g.addDirectedEdge(0, 1, 1.0);
            g.addDirectedEdge(1, 2, 1.0);
            g.addDirectedEdge(2, 3, 1.0);

            TopologicalSorter sorter = new TopologicalSorter(g);
            List<Integer> order = sorter.sortKahn();

            assert order.size() == 4;
            assert order.get(0) == 0;
            assert order.get(3) == 3;
        });
    }

    private static void testAdvancedBuilders() {
        System.out.println("\n─── ADVANCED BUILDER TESTS ───\n");

        test("Directed DAG generator", () -> {
            DirectedGraph g = AdvancedGraphBuilder.directedDAG(5, 10);
            assert g.getVertexCount() == 5;
        });

        test("Directed cycle generator", () -> {
            DirectedGraph g = AdvancedGraphBuilder.directedCycle(5);
            assert g.getVertexCount() == 5;
            assert g.getEdgeCount() == 5;
        });

        test("Directed path generator", () -> {
            DirectedGraph g = AdvancedGraphBuilder.directedPath(5);
            assert g.getVertexCount() == 5;
            assert g.getEdgeCount() == 4;
        });

        test("Complete weighted graph", () -> {
            WeightedGraph g = AdvancedGraphBuilder.completeWeightedGraph(4);
            assert g.getVertexCount() == 4;
            assert g.getEdgeCount() == 6;
        });

        test("Grid graph generator", () -> {
            WeightedGraph g = AdvancedGraphBuilder.gridGraph(3, 3);
            assert g.getVertexCount() == 9;
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
