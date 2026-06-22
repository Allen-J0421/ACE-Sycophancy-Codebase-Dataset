package maxflow.solve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import maxflow.solve.MaxFlowResult.FlowEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MaxFlowResult")
class MaxFlowResultTest {

    @Test
    @DisplayName("exposes value, source, sink and flow edges")
    void exposesState() {
        List<FlowEdge> edges = List.of(new FlowEdge(0, 1, 7));
        MaxFlowResult result = new MaxFlowResult(0, 5, 7, edges);

        assertEquals(7, result.value());
        assertEquals(0, result.source());
        assertEquals(5, result.sink());
        assertEquals(edges, result.flowEdges());
    }

    @Test
    @DisplayName("rejects a negative flow value")
    void rejectsNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new MaxFlowResult(0, 1, -1, List.of()));
    }

    @Test
    @DisplayName("holds an immutable copy of its flow edges")
    void flowEdgesAreImmutable() {
        MaxFlowResult result = new MaxFlowResult(0, 1, 0, List.of());
        assertThrows(UnsupportedOperationException.class,
                () -> result.flowEdges().add(new FlowEdge(0, 1, 1)));
    }

    @Test
    @DisplayName("FlowEdge renders readably")
    void flowEdgeToString() {
        assertEquals("0 -> 1: 5", new FlowEdge(0, 1, 5).toString());
    }
}
