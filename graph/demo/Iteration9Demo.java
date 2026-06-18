package graph.demo;

import graph.algorithm.FlowNetwork;
import graph.algorithm.MaximumFlow;
import graph.algorithm.MinimumCut;

public class Iteration9Demo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          ITERATION 9: FLOW NETWORKS & MAXIMUM FLOW           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        demonstrateSimpleFlow();
        demonstrateMultiPathFlow();
        demonstrateComplexNetwork();
        demonstrateMinimumCut();
    }

    private static void demonstrateSimpleFlow() {
        System.out.println("┌─ SIMPLE FLOW NETWORK ──────────────────────────────────────────┐\n");

        FlowNetwork net = new FlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(1, 2, 5);
        net.addEdge(2, 3, 10);

        System.out.println("Network: 0 --[10]--> 1 --[5]--> 2 --[10]--> 3");
        System.out.println("Bottleneck capacity: 5\n");

        MaximumFlow mf = new MaximumFlow(net, 0, 3);
        System.out.println("Maximum Flow: " + String.format("%.1f", mf.getMaxFlow()));
        System.out.println("Flow edges:");
        for (String edge : mf.getFlowEdges()) {
            System.out.println("  " + edge);
        }
        System.out.println();
    }

    private static void demonstrateMultiPathFlow() {
        System.out.println("┌─ MULTI-PATH FLOW NETWORK ─────────────────────────────────────┐\n");

        FlowNetwork net = new FlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 3, 10);
        net.addEdge(2, 3, 10);

        System.out.println("Network structure:");
        System.out.println("        1 --[10]--> 3");
        System.out.println("       /             ^");
        System.out.println("      /               \\");
        System.out.println("  0 --[10]           10");
        System.out.println("      \\               /");
        System.out.println("       \\             /");
        System.out.println("        2 --[10]--> 3\n");

        MaximumFlow mf = new MaximumFlow(net, 0, 3);
        System.out.println("Maximum Flow: " + String.format("%.1f", mf.getMaxFlow()));
        System.out.println("Parallel paths allow flow distribution");
        System.out.println();
    }

    private static void demonstrateComplexNetwork() {
        System.out.println("┌─ COMPLEX NETWORK ANALYSIS ────────────────────────────────────┐\n");

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

        System.out.println("Complex network with multiple paths and cycles");
        System.out.println("Vertices: 0 (source) -> 5 (sink)\n");

        MaximumFlow mf = new MaximumFlow(net, 0, 5);
        System.out.println("Maximum Flow: " + String.format("%.1f", mf.getMaxFlow()));
        System.out.println("Total edges: 10");
        System.out.println("Active flow edges: " + mf.getFlowEdges().size());
        System.out.println();
    }

    private static void demonstrateMinimumCut() {
        System.out.println("┌─ MINIMUM CUT ANALYSIS ────────────────────────────────────────┐\n");

        FlowNetwork net = new FlowNetwork(4);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 3, 8);
        net.addEdge(2, 3, 8);

        MaximumFlow mf = new MaximumFlow(net, 0, 3);
        MinimumCut cut = new MinimumCut(net, 0, 3);

        System.out.println("Ford-Fulkerson Max-Flow Min-Cut Theorem");
        System.out.println("The maximum flow equals the minimum cut capacity\n");

        System.out.println("Maximum Flow: " + String.format("%.1f", mf.getMaxFlow()));
        System.out.println("Minimum Cut Value: " + String.format("%.1f", cut.getCutValue()));
        System.out.println("\nThey are equal (as proven by the theorem)");

        System.out.println("\nSource partition vertices:");
        for (int v : cut.getSourcePartition()) {
            System.out.println("  " + v);
        }

        System.out.println("\nSink partition vertices:");
        for (int v : cut.getSinkPartition()) {
            System.out.println("  " + v);
        }

        System.out.println("\nCut edges (edges from source to sink partition):");
        for (String edge : cut.getCutEdges()) {
            System.out.println("  " + edge);
        }
        System.out.println();
    }
}
