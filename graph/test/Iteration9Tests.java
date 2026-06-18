package graph.test;

import graph.algorithm.FlowNetwork;
import graph.algorithm.MaximumFlow;
import graph.algorithm.MinimumCut;

public class Iteration9Tests {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║    ITERATION 9: FLOW NETWORKS & MAXIMUM FLOW             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        testMaximumFlow();
        testMinimumCut();

        printResults();
    }

    private static void testMaximumFlow() {
        System.out.println("─── MAXIMUM FLOW TESTS ───\n");

        test("Simple path maximum flow", () -> {
            FlowNetwork net = new FlowNetwork(4);
            net.addEdge(0, 1, 10);
            net.addEdge(1, 2, 5);
            net.addEdge(2, 3, 10);

            MaximumFlow mf = new MaximumFlow(net, 0, 3);
            assert mf.getMaxFlow() == 5;
        });

        test("Multiple paths maximum flow", () -> {
            FlowNetwork net = new FlowNetwork(4);
            net.addEdge(0, 1, 10);
            net.addEdge(0, 2, 10);
            net.addEdge(1, 3, 10);
            net.addEdge(2, 3, 10);

            MaximumFlow mf = new MaximumFlow(net, 0, 3);
            assert mf.getMaxFlow() == 20;
        });

        test("Complex network maximum flow", () -> {
            FlowNetwork net = new FlowNetwork(6);
            net.addEdge(0, 1, 16);
            net.addEdge(0, 2, 13);
            net.addEdge(1, 2, 10);
            net.addEdge(1, 3, 12);
            net.addEdge(2, 1, 9);
            net.addEdge(2, 4, 14);
            net.addEdge(3, 2, 9);
            net.addEdge(3, 5, 20);
            net.addEdge(4, 3, 7);
            net.addEdge(4, 5, 4);

            MaximumFlow mf = new MaximumFlow(net, 0, 5);
            assert mf.getMaxFlow() == 23;
        });

        test("Single edge network", () -> {
            FlowNetwork net = new FlowNetwork(2);
            net.addEdge(0, 1, 15);

            MaximumFlow mf = new MaximumFlow(net, 0, 1);
            assert mf.getMaxFlow() == 15;
        });

        test("Flow edges returned", () -> {
            FlowNetwork net = new FlowNetwork(3);
            net.addEdge(0, 1, 10);
            net.addEdge(1, 2, 10);

            MaximumFlow mf = new MaximumFlow(net, 0, 2);
            assert mf.getFlowEdges().size() > 0;
        });
    }

    private static void testMinimumCut() {
        System.out.println("\n─── MINIMUM CUT TESTS ───\n");

        test("Identify source partition", () -> {
            FlowNetwork net = new FlowNetwork(4);
            net.addEdge(0, 1, 10);
            net.addEdge(1, 2, 5);
            net.addEdge(2, 3, 10);

            MaximumFlow mf = new MaximumFlow(net, 0, 3);
            MinimumCut cut = new MinimumCut(net, 0, 3);
            assert cut.getSourcePartition().contains(0);
        });

        test("Cut value equals maximum flow", () -> {
            FlowNetwork net = new FlowNetwork(4);
            net.addEdge(0, 1, 10);
            net.addEdge(0, 2, 10);
            net.addEdge(1, 3, 10);
            net.addEdge(2, 3, 10);

            MaximumFlow mf = new MaximumFlow(net, 0, 3);
            MinimumCut cut = new MinimumCut(net, 0, 3);
            assert cut.getCutValue() == mf.getMaxFlow();
        });

        test("Partitions cover all vertices", () -> {
            FlowNetwork net = new FlowNetwork(5);
            net.addEdge(0, 1, 10);
            net.addEdge(1, 2, 10);
            net.addEdge(2, 3, 10);
            net.addEdge(3, 4, 10);

            MaximumFlow mf = new MaximumFlow(net, 0, 4);
            MinimumCut cut = new MinimumCut(net, 0, 4);
            int total = cut.getSourcePartition().size() + cut.getSinkPartition().size();
            assert total == 5;
        });

        test("Cut edges are correct", () -> {
            FlowNetwork net = new FlowNetwork(4);
            net.addEdge(0, 1, 10);
            net.addEdge(0, 2, 10);
            net.addEdge(1, 3, 8);
            net.addEdge(2, 3, 8);

            MaximumFlow mf = new MaximumFlow(net, 0, 3);
            MinimumCut cut = new MinimumCut(net, 0, 3);
            assert cut.getCutEdges().size() > 0;
            assert cut.getCutSize() == cut.getCutEdges().size();
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
