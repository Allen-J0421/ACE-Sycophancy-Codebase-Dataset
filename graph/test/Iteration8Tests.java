package graph.test;

import graph.analysis.BiconnectedComponentsAnalyzer;
import graph.analysis.BridgesAndArticulationFinder;
import graph.builder.GraphGenerator;
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.metrics.AdvancedMetrics;

import java.util.List;

public class Iteration8Tests {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║    ITERATION 8: ADVANCED CONNECTIVITY & METRICS           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        testBridgesAndArticulation();
        testBiconnectedComponents();
        testAdvancedMetrics();

        printResults();
    }

    private static void testBridgesAndArticulation() {
        System.out.println("─── BRIDGES AND ARTICULATION POINTS TESTS ───\n");

        test("Find bridges in simple graph", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();

            BridgesAndArticulationFinder finder = new BridgesAndArticulationFinder(g);
            assert finder.getBridgeCount() > 0;
        });

        test("Find articulation points", () -> {
            Graph g = new GraphBuilder(5)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .addEdge(3, 4)
                    .build();

            BridgesAndArticulationFinder finder = new BridgesAndArticulationFinder(g);
            List<Integer> points = finder.getArticulationPoints();
            assert points.size() > 0;
        });

        test("No bridges in cycle", () -> {
            Graph g = GraphGenerator.cycleGraph(4);
            BridgesAndArticulationFinder finder = new BridgesAndArticulationFinder(g);
            assert finder.getBridgeCount() == 0;
        });

        test("Check specific bridge", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();

            BridgesAndArticulationFinder finder = new BridgesAndArticulationFinder(g);
            assert finder.isBridge(0, 1);
            assert finder.isBridge(1, 2);
        });
    }

    private static void testBiconnectedComponents() {
        System.out.println("\n─── BICONNECTED COMPONENTS TESTS ───\n");

        test("Single biconnected component", () -> {
            Graph g = GraphGenerator.cycleGraph(4);
            BiconnectedComponentsAnalyzer analyzer = new BiconnectedComponentsAnalyzer(g);
            assert analyzer.getComponentCount() > 0;
        });

        test("Multiple biconnected components", () -> {
            Graph g = new GraphBuilder(6)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(0, 2)
                    .addEdge(2, 3)
                    .addEdge(3, 4)
                    .addEdge(3, 5)
                    .addEdge(4, 5)
                    .build();

            BiconnectedComponentsAnalyzer analyzer = new BiconnectedComponentsAnalyzer(g);
            List<List<Integer>> components = analyzer.getComponents();
            assert components.size() >= 2;
        });
    }

    private static void testAdvancedMetrics() {
        System.out.println("\n─── ADVANCED METRICS TESTS ───\n");

        test("Clustering coefficient calculation", () -> {
            Graph g = GraphGenerator.completeGraph(4);
            AdvancedMetrics metrics = new AdvancedMetrics(g);
            assert metrics.getClusteringCoefficient() > 0;
        });

        test("Average path length", () -> {
            Graph g = GraphGenerator.pathGraph(5);
            AdvancedMetrics metrics = new AdvancedMetrics(g);
            assert metrics.getAveragePathLength() > 0;
        });

        test("Assortativity calculation", () -> {
            Graph g = GraphGenerator.completeGraph(5);
            AdvancedMetrics metrics = new AdvancedMetrics(g);
            double assortativity = metrics.getAssortativity();
            assert !Double.isNaN(assortativity);
        });

        test("Metrics report generation", () -> {
            Graph g = GraphGenerator.cycleGraph(5);
            AdvancedMetrics metrics = new AdvancedMetrics(g);
            String report = metrics.getReport();
            assert report.contains("Clustering Coefficient");
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
