package graph.test;

import graph.algorithm.CycleFinder;
import graph.algorithm.TopologicalSort;
import graph.analysis.BipartiteChecker;
import graph.analysis.GraphComparator;
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.core.ImmutableGraph;
import graph.exception.InvalidGraphException;
import graph.io.GraphFormat;
import graph.io.GraphSerializer;
import graph.metrics.GraphMetrics;

import java.util.List;

public class AdvancedGraphTests {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        ADVANCED GRAPH FRAMEWORK TEST SUITE                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        testImmutability();
        testSerialization();
        testMetrics();
        testCycleFinding();
        testBipartite();
        testGraphComparison();

        printResults();
    }

    private static void testImmutability() {
        System.out.println("─── IMMUTABILITY TESTS ───\n");

        test("Create immutable graph", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            ImmutableGraph immutable = new ImmutableGraph(g);
            assert immutable.getVertexCount() == 4;
        });

        test("Reject modifications on immutable", () -> {
            Graph g = new GraphBuilder(3).build();
            ImmutableGraph immutable = new ImmutableGraph(g);
            try {
                immutable.addEdge(0, 1);
                assert false : "Should reject modification";
            } catch (UnsupportedOperationException e) {
                assert true;
            }
        });
    }

    private static void testSerialization() {
        System.out.println("\n─── SERIALIZATION TESTS ───\n");

        test("Serialize to adjacency list", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            String serialized = GraphSerializer.serialize(g, GraphFormat.ADJACENCY_LIST);
            assert serialized.contains("0:");
            assert serialized.contains("Vertices: 3");
        });

        test("Serialize to edge list", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            String serialized = GraphSerializer.serialize(g, GraphFormat.EDGE_LIST);
            assert serialized.contains("0 1");
            assert serialized.contains("1 2");
        });

        test("Serialize to matrix", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .build();
            String serialized = GraphSerializer.serialize(g, GraphFormat.MATRIX);
            assert serialized.contains("Adjacency Matrix");
        });
    }

    private static void testMetrics() {
        System.out.println("\n─── METRICS TESTS ───\n");

        test("Calculate graph metrics", () -> {
            Graph g = new GraphBuilder(5)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .addEdge(3, 4)
                    .build();
            GraphMetrics metrics = new GraphMetrics(g);
            assert metrics.getAverageDegree() > 0;
            assert metrics.getDensity() > 0;
        });

        test("Get diameter and radius", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            GraphMetrics metrics = new GraphMetrics(g);
            assert metrics.getDiameter() >= metrics.getRadius();
        });

        test("Get degree sequence", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            GraphMetrics metrics = new GraphMetrics(g);
            int[] sequence = metrics.getDegreeSequence();
            assert sequence.length == 3;
        });
    }

    private static void testCycleFinding() {
        System.out.println("\n─── CYCLE FINDING TESTS ───\n");

        test("Detect cycle in cyclic graph", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 0)
                    .build();
            CycleFinder finder = new CycleFinder(g);
            assert finder.hasCycle();
        });

        test("No cycle in acyclic graph", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            CycleFinder finder = new CycleFinder(g);
            assert !finder.hasCycle();
        });
    }

    private static void testBipartite() {
        System.out.println("\n─── BIPARTITE TESTS ───\n");

        test("Detect bipartite graph", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            BipartiteChecker checker = new BipartiteChecker(g);
            assert checker.isBipartite();
        });

        test("Reject non-bipartite graph", () -> {
            Graph g = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 0)
                    .build();
            BipartiteChecker checker = new BipartiteChecker(g);
            assert !checker.isBipartite();
        });

        test("Get bipartite partitions", () -> {
            Graph g = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(2, 3)
                    .build();
            BipartiteChecker checker = new BipartiteChecker(g);
            if (checker.isBipartite()) {
                List<Integer> partition = checker.getPartition(0);
                assert partition.size() > 0;
            }
        });
    }

    private static void testGraphComparison() {
        System.out.println("\n─── GRAPH COMPARISON TESTS ───\n");

        test("Check graph equality", () -> {
            Graph g1 = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            Graph g2 = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            assert GraphComparator.isEqual(g1, g2);
        });

        test("Check isomorphism (degree sequence)", () -> {
            Graph g1 = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            Graph g2 = new GraphBuilder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 3)
                    .build();
            assert GraphComparator.isIsomorphic(g1, g2);
        });

        test("Calculate Jaccard similarity", () -> {
            Graph g1 = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build();
            Graph g2 = new GraphBuilder(3)
                    .addEdge(0, 1)
                    .build();
            double similarity = GraphComparator.jaccardSimilarity(g1, g2);
            assert similarity >= 0 && similarity <= 1;
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
        System.out.println("Failed: " + (testsRun - testsPassed));
        System.out.println();
    }

    @FunctionalInterface
    interface TestCase {
        void run() throws Exception;
    }
}
