package maxflow.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ResidualGraph")
class ResidualGraphTest {

    private static FlowNetwork sample() {
        return FlowNetwork.builder(3)
                .addEdge(0, 1, 5)
                .addEdge(1, 2, 3)
                .build();
    }

    @Test
    @DisplayName("starts with residual capacity equal to network capacity")
    void initialResidualMatchesCapacity() {
        ResidualGraph residual = new ResidualGraph(sample());
        assertEquals(Capacity.of(5), residual.residualCapacity(0, 1));
        assertEquals(Capacity.of(3), residual.residualCapacity(1, 2));
        assertEquals(Capacity.ZERO, residual.residualCapacity(0, 2));
        assertEquals(3, residual.vertexCount());
    }

    @Test
    @DisplayName("hasResidualCapacity reflects remaining capacity")
    void hasResidualCapacity() {
        ResidualGraph residual = new ResidualGraph(sample());
        assertTrue(residual.hasResidualCapacity(0, 1));
        assertFalse(residual.hasResidualCapacity(0, 2));
    }

    @Test
    @DisplayName("pushing flow moves capacity from the forward to the reverse edge")
    void pushFlowUpdatesBothDirections() {
        ResidualGraph residual = new ResidualGraph(sample());
        residual.pushFlow(0, 1, Capacity.of(2));
        assertEquals(Capacity.of(3), residual.residualCapacity(0, 1), "forward decreases");
        assertEquals(Capacity.of(2), residual.residualCapacity(1, 0), "reverse increases");
    }

    @Test
    @DisplayName("flowOn reports net forward flow and clamps reverse edges to zero")
    void flowOnReportsNetFlow() {
        ResidualGraph residual = new ResidualGraph(sample());
        residual.pushFlow(0, 1, Capacity.of(2));
        assertEquals(Capacity.of(2), residual.flowOn(0, 1));
        assertEquals(Capacity.ZERO, residual.flowOn(1, 0), "reverse residual is not reported as negative flow");
    }

    @Test
    @DisplayName("rejects a zero flow amount")
    void rejectsZeroAmount() {
        ResidualGraph residual = new ResidualGraph(sample());
        assertThrows(IllegalArgumentException.class, () -> residual.pushFlow(0, 1, Capacity.ZERO));
    }

    @Test
    @DisplayName("rejects pushing more than the residual capacity")
    void rejectsOverCapacity() {
        ResidualGraph residual = new ResidualGraph(sample());
        assertThrows(IllegalArgumentException.class, () -> residual.pushFlow(0, 1, Capacity.of(6)));
    }

    @Test
    @DisplayName("forEachResidualEdge visits exactly the edges with positive residual capacity")
    void iteratesPositiveResidualEdges() {
        ResidualGraph residual = new ResidualGraph(sample());
        List<String> visited = new ArrayList<>();
        residual.forEachResidualEdge(0, (to, cap) -> visited.add(to + ":" + cap));
        assertEquals(List.of("1:5"), visited);

        // After pushing flow, the reverse edge becomes traversable.
        residual.pushFlow(0, 1, Capacity.of(2));
        List<String> fromOne = new ArrayList<>();
        residual.forEachResidualEdge(1, (to, cap) -> fromOne.add(to + ":" + cap));
        assertTrue(fromOne.contains("0:2"), "reverse edge 1->0 should now have residual capacity");
        assertTrue(fromOne.contains("2:3"), "forward edge 1->2 should remain");
    }
}
