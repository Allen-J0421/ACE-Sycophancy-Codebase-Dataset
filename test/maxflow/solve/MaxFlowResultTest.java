package maxflow.solve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import maxflow.graph.Edge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MaxFlowResult")
class MaxFlowResultTest {

    @Test
    @DisplayName("exposes value, source, sink and flow edges")
    void exposesState() {
        List<Edge> edges = List.of(new Edge(0, 1, 7));
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
                () -> result.flowEdges().add(new Edge(0, 1, 1)));
    }
}
