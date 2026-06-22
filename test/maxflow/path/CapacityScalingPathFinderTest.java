package maxflow.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import maxflow.graph.Capacity;
import maxflow.graph.FlowNetwork;
import maxflow.graph.ResidualGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CapacityScalingPathFinder")
class CapacityScalingPathFinderTest {

    private final CapacityScalingPathFinder finder = new CapacityScalingPathFinder();

    @Test
    @DisplayName("prefers the high-capacity path when several routes exist")
    void prefersHighCapacityPath() {
        // A fat route 0->1->3 (capacity 100) competes with a thin 0->2->3 (capacity 1).
        FlowNetwork network = FlowNetwork.fromMatrix(new int[][] {
                {0, 100, 1, 0},
                {0, 0, 0, 100},
                {0, 0, 0, 1},
                {0, 0, 0, 0},
        });

        AugmentingPath path = finder.findPath(new ResidualGraph(network), 0, 3).orElseThrow();

        assertEquals(List.of(0, 1, 3), path.vertices(), "should route through the fat edges");
        assertEquals(Capacity.of(100), path.bottleneck());
    }

    @Test
    @DisplayName("falls back to thinner edges once the fat ones are exhausted")
    void fallsBackToThinEdges() {
        // The only route is thin; capacity scaling must still find it after Δ shrinks.
        FlowNetwork network = FlowNetwork.builder(3)
                .addEdge(0, 1, 1)
                .addEdge(1, 2, 1)
                .build();

        AugmentingPath path = finder.findPath(new ResidualGraph(network), 0, 2).orElseThrow();

        assertEquals(List.of(0, 1, 2), path.vertices());
        assertEquals(Capacity.of(1), path.bottleneck());
    }

    @Test
    @DisplayName("returns empty when the sink is unreachable")
    void emptyWhenUnreachable() {
        FlowNetwork network = FlowNetwork.builder(3).addEdge(0, 1, 5).build();
        assertTrue(finder.findPath(new ResidualGraph(network), 0, 2).isEmpty());
    }

    @Test
    @DisplayName("returns empty for a saturated (no residual) graph")
    void emptyWhenSaturated() {
        FlowNetwork network = FlowNetwork.builder(2).build();
        assertTrue(finder.findPath(new ResidualGraph(network), 0, 1).isEmpty());
    }

    @Test
    @DisplayName("exposes a human-readable strategy name")
    void exposesName() {
        assertEquals("capacity-scaling", finder.name());
    }
}
