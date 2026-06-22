package maxflow;

import maxflow.graph.Edge;
import maxflow.graph.FlowNetwork;
import maxflow.solve.FordFulkersonSolver;
import maxflow.solve.MaxFlowResult;
import maxflow.solve.MaxFlowSolver;

/**
 * Demonstrates the {@code maxflow} library on the classic six-vertex network,
 * reproducing the behaviour of the original single-file program (maximum flow 23)
 * and additionally printing the flow routed along each edge.
 */
public final class FordFulkersonDemo {

    private FordFulkersonDemo() {
    }

    public static void main(String[] args) {
        FlowNetwork network = FlowNetwork.fromMatrix(new int[][] {
                { 0, 16, 13, 0, 0, 0 },
                { 0, 0, 10, 12, 0, 0 },
                { 0, 4, 0, 0, 14, 0 },
                { 0, 0, 9, 0, 0, 20 },
                { 0, 0, 0, 7, 0, 4 },
                { 0, 0, 0, 0, 0, 0 },
        });

        int source = 0;
        int sink = 5;

        MaxFlowSolver solver = new FordFulkersonSolver();
        MaxFlowResult result = solver.solve(network, source, sink);

        System.out.println("The maximum possible flow is " + result.value());
        System.out.println("Flow per edge:");
        for (Edge edge : result.flowEdges()) {
            System.out.println("  " + edge);
        }
    }
}
