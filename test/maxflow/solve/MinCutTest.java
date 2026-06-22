package maxflow.solve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import maxflow.graph.Capacity;
import maxflow.graph.Edge;
import maxflow.graph.FlowNetwork;
import maxflow.path.AugmentingPathFinder;
import maxflow.path.BreadthFirstPathFinder;
import maxflow.path.CapacityScalingPathFinder;
import maxflow.path.DepthFirstPathFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MinCut")
class MinCutTest {

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

    private static MinCut cutOf(FlowNetwork network, int source, int sink) {
        return MinCut.of(network, new FordFulkersonSolver().solve(new MaxFlowProblem(network, source, sink)));
    }

    @Test
    @DisplayName("identifies the textbook partition of the classic network")
    void classicPartition() {
        FlowNetwork network = classicNetwork();
        MinCut cut = cutOf(network, 0, 5);

        assertEquals(Set.of(0, 1, 2, 4), cut.sourceSide());
        assertEquals(Set.of(3, 5), cut.sinkSide());
        assertEquals(Capacity.of(23), cut.capacity());
        assertEquals(Set.of(new Edge(1, 3, 12), new Edge(4, 3, 7), new Edge(4, 5, 4)),
                new HashSet<>(cut.cutEdges()));
    }

    @Test
    @DisplayName("cut capacity equals the maximum flow value, for every strategy")
    void capacityEqualsMaxFlow() {
        FlowNetwork network = classicNetwork();
        for (AugmentingPathFinder finder : java.util.List.of(
                new BreadthFirstPathFinder(), new DepthFirstPathFinder(), new CapacityScalingPathFinder())) {
            MaxFlowResult result = new FordFulkersonSolver(finder).solve(new MaxFlowProblem(network, 0, 5));
            MinCut cut = MinCut.of(network, result);
            assertEquals(result.value(), cut.capacity(), finder.name());
        }
    }

    @Test
    @DisplayName("the two sides partition the vertices: disjoint and covering")
    void sidesPartitionVertices() {
        FlowNetwork network = classicNetwork();
        MinCut cut = cutOf(network, 0, 5);

        Set<Integer> union = new HashSet<>(cut.sourceSide());
        union.addAll(cut.sinkSide());
        assertEquals(network.vertexCount(), union.size(), "every vertex is on exactly one side");

        Set<Integer> overlap = new HashSet<>(cut.sourceSide());
        overlap.retainAll(cut.sinkSide());
        assertTrue(overlap.isEmpty(), "the sides do not overlap");
    }

    @Test
    @DisplayName("the source is on the source side and the sink on the sink side")
    void sourceAndSinkLandOnExpectedSides() {
        MinCut cut = cutOf(classicNetwork(), 0, 5);
        assertTrue(cut.sourceSide().contains(0));
        assertTrue(cut.sinkSide().contains(5));
    }

    @Test
    @DisplayName("every cut edge crosses S->T, is saturated, and the capacities sum to the cut")
    void cutEdgesCrossAndSaturate() {
        FlowNetwork network = classicNetwork();
        MaxFlowResult result = new FordFulkersonSolver().solve(new MaxFlowProblem(network, 0, 5));
        MinCut cut = MinCut.of(network, result);

        Set<Edge> flows = new HashSet<>(result.flowEdges());
        Capacity summed = Capacity.ZERO;
        for (Edge edge : cut.cutEdges()) {
            assertTrue(cut.sourceSide().contains(edge.from()), edge + " leaves the source side");
            assertTrue(cut.sinkSide().contains(edge.to()), edge + " enters the sink side");
            assertTrue(flows.contains(new Edge(edge.from(), edge.to(), edge.value().units())),
                    edge + " must be saturated (flow equals capacity)");
            summed = summed.plus(edge.value());
        }
        assertEquals(cut.capacity(), summed, "cut-edge capacities sum to the cut capacity");
    }

    @Test
    @DisplayName("a disconnected sink yields a zero-capacity cut with no crossing edges")
    void disconnectedNetwork() {
        FlowNetwork network = FlowNetwork.builder(3).addEdge(0, 1, 5).build();
        MinCut cut = cutOf(network, 0, 2);

        assertEquals(Capacity.ZERO, cut.capacity());
        assertTrue(cut.sinkSide().contains(2));
        assertTrue(cut.cutEdges().isEmpty());
    }

    @Test
    @DisplayName("identifies the single bottleneck edge in a chain")
    void bottleneckChain() {
        FlowNetwork network = FlowNetwork.builder(3)
                .addEdge(0, 1, 3)
                .addEdge(1, 2, 1)
                .build();
        MinCut cut = cutOf(network, 0, 2);

        assertEquals(Set.of(0, 1), cut.sourceSide());
        assertEquals(Set.of(2), cut.sinkSide());
        assertEquals(java.util.List.of(new Edge(1, 2, 1)), cut.cutEdges());
        assertEquals(Capacity.of(1), cut.capacity());
    }

    @Test
    @DisplayName("rejects null arguments")
    void rejectsNulls() {
        FlowNetwork network = classicNetwork();
        MaxFlowResult result = new FordFulkersonSolver().solve(new MaxFlowProblem(network, 0, 5));
        assertThrows(NullPointerException.class, () -> MinCut.of(null, result));
        assertThrows(NullPointerException.class, () -> MinCut.of(network, null));
    }

    @Test
    @DisplayName("exposes immutable partitions and cut edges")
    void immutableViews() {
        MinCut cut = cutOf(classicNetwork(), 0, 5);
        assertThrows(UnsupportedOperationException.class, () -> cut.sourceSide().add(99));
        assertThrows(UnsupportedOperationException.class, () -> cut.sinkSide().add(99));
        assertThrows(UnsupportedOperationException.class, () -> cut.cutEdges().add(new Edge(0, 1, 1)));
    }
}
