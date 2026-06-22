package maxflow.solve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import maxflow.graph.FlowNetwork;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MaxFlowProblem")
class MaxFlowProblemTest {

    private static FlowNetwork network() {
        return FlowNetwork.builder(6).addEdge(0, 5, 1).build();
    }

    @Test
    @DisplayName("exposes the network, source and sink")
    void exposesComponents() {
        FlowNetwork network = network();
        MaxFlowProblem problem = new MaxFlowProblem(network, 0, 5);
        assertSame(network, problem.network());
        assertEquals(0, problem.source());
        assertEquals(5, problem.sink());
    }

    @Test
    @DisplayName("rejects a null network")
    void rejectsNullNetwork() {
        assertThrows(NullPointerException.class, () -> new MaxFlowProblem(null, 0, 1));
    }

    @Test
    @DisplayName("rejects a source that is not a vertex of the network")
    void rejectsOutOfRangeSource() {
        assertThrows(IllegalArgumentException.class, () -> new MaxFlowProblem(network(), -1, 5));
    }

    @Test
    @DisplayName("rejects a sink that is not a vertex of the network")
    void rejectsOutOfRangeSink() {
        assertThrows(IllegalArgumentException.class, () -> new MaxFlowProblem(network(), 0, 6));
    }

    @Test
    @DisplayName("rejects equal source and sink")
    void rejectsEqualEndpoints() {
        assertThrows(IllegalArgumentException.class, () -> new MaxFlowProblem(network(), 3, 3));
    }

    @Test
    @DisplayName("renders a readable description")
    void toStringIsReadable() {
        assertEquals("max-flow problem: source 0 -> sink 5 over 6 vertices",
                new MaxFlowProblem(network(), 0, 5).toString());
    }
}
