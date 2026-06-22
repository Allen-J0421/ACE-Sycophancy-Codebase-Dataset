package maxflow.solve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import maxflow.graph.FlowNetwork;
import maxflow.path.BreadthFirstPathFinder;
import maxflow.path.DepthFirstPathFinder;
import maxflow.solve.MaxFlowResult.FlowEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FordFulkersonSolver")
class FordFulkersonSolverTest {

    /** The classic six-vertex network whose maximum flow from 0 to 5 is 23. */
    private static FlowNetwork classicNetwork() {
        return FlowNetwork.fromMatrix(new int[][] {
                {0, 16, 13, 0, 0, 0},
                {0, 0, 10, 12, 0, 0},
                {0, 4, 0, 0, 14, 0},
                {0, 0, 9, 0, 0, 20},
                {0, 0, 0, 7, 0, 4},
                {0, 0, 0, 0, 0, 0},
        });
    }

    private static List<MaxFlowSolver> strategies() {
        return List.of(
                new FordFulkersonSolver(new BreadthFirstPathFinder()),
                new FordFulkersonSolver(new DepthFirstPathFinder()));
    }

    @Test
    @DisplayName("computes the textbook maximum flow of 23, regardless of strategy")
    void classicMaxFlow() {
        for (MaxFlowSolver solver : strategies()) {
            assertEquals(23, solver.solve(classicNetwork(), 0, 5).value());
        }
    }

    @Test
    @DisplayName("records the source and sink it was solved for")
    void recordsSourceAndSink() {
        MaxFlowResult result = new FordFulkersonSolver().solve(classicNetwork(), 0, 5);
        assertEquals(0, result.source());
        assertEquals(5, result.sink());
    }

    @Test
    @DisplayName("the reported flow conserves at every vertex")
    void flowIsConserved() {
        int source = 0;
        int sink = 5;
        for (MaxFlowSolver solver : strategies()) {
            MaxFlowResult result = solver.solve(classicNetwork(), source, sink);
            int[] net = new int[6]; // outflow - inflow per vertex
            for (FlowEdge edge : result.flowEdges()) {
                net[edge.from()] += edge.flow();
                net[edge.to()] -= edge.flow();
            }
            for (int v = 0; v < net.length; v++) {
                int expected = v == source ? result.value() : v == sink ? -result.value() : 0;
                assertEquals(expected, net[v], "conservation at vertex " + v);
            }
        }
    }

    @Test
    @DisplayName("every reported edge carries a positive flow within its capacity")
    void flowEdgesRespectCapacity() {
        FlowNetwork network = classicNetwork();
        MaxFlowResult result = new FordFulkersonSolver().solve(network, 0, 5);
        for (FlowEdge edge : result.flowEdges()) {
            assertTrue(edge.flow() > 0, "flow is positive on " + edge);
            assertTrue(edge.flow() <= network.capacity(edge.from(), edge.to()),
                    "flow within capacity on " + edge);
        }
    }

    @Test
    @DisplayName("resolves the antiparallel-cancellation network (needs reverse edges)")
    void antiparallelCancellation() {
        FlowNetwork network = FlowNetwork.fromMatrix(new int[][] {
                {0, 3, 3, 0},
                {0, 0, 2, 2},
                {0, 0, 0, 3},
                {0, 0, 0, 0},
        });
        for (MaxFlowSolver solver : strategies()) {
            assertEquals(5, solver.solve(network, 0, 3).value());
        }
    }

    @Test
    @DisplayName("returns zero flow when the sink is disconnected from the source")
    void disconnectedHasZeroFlow() {
        FlowNetwork network = FlowNetwork.builder(3).addEdge(0, 1, 5).build();
        MaxFlowResult result = new FordFulkersonSolver().solve(network, 0, 2);
        assertEquals(0, result.value());
        assertTrue(result.flowEdges().isEmpty());
    }

    @Test
    @DisplayName("saturates a single direct edge")
    void singleEdge() {
        FlowNetwork network = FlowNetwork.builder(2).addEdge(0, 1, 7).build();
        assertEquals(7, new FordFulkersonSolver().solve(network, 0, 1).value());
    }

    @Test
    @DisplayName("rejects an invalid source, sink, or equal endpoints")
    void rejectsInvalidEndpoints() {
        FlowNetwork network = classicNetwork();
        MaxFlowSolver solver = new FordFulkersonSolver();
        assertThrows(IllegalArgumentException.class, () -> solver.solve(network, -1, 5));
        assertThrows(IllegalArgumentException.class, () -> solver.solve(network, 0, 6));
        assertThrows(IllegalArgumentException.class, () -> solver.solve(network, 3, 3));
    }

    @Test
    @DisplayName("rejects a null path-finder strategy")
    void rejectsNullStrategy() {
        assertThrows(NullPointerException.class, () -> new FordFulkersonSolver(null));
    }

    @Test
    @DisplayName("defaults to the breadth-first (Edmonds-Karp) strategy")
    void defaultsToBreadthFirst() {
        assertInstanceOf(BreadthFirstPathFinder.class, new FordFulkersonSolver().pathFinder());
    }
}
