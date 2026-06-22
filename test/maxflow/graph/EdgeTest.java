package maxflow.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Edge")
class EdgeTest {

    @Test
    @DisplayName("exposes its endpoints and value")
    void exposesState() {
        Edge edge = new Edge(0, 3, 16);
        assertEquals(0, edge.from());
        assertEquals(3, edge.to());
        assertEquals(Capacity.of(16), edge.value());
    }

    @Test
    @DisplayName("rejects negative endpoints")
    void rejectsNegativeEndpoints() {
        assertThrows(IllegalArgumentException.class, () -> new Edge(-1, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Edge(0, -1, 5));
    }

    @Test
    @DisplayName("rejects self-loops")
    void rejectsSelfLoops() {
        assertThrows(IllegalArgumentException.class, () -> new Edge(2, 2, 5));
    }

    @Test
    @DisplayName("rejects a negative value")
    void rejectsNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new Edge(0, 1, -1));
    }

    @Test
    @DisplayName("withValue keeps the endpoints and replaces the value")
    void withValueReplacesValue() {
        Edge capacity = new Edge(1, 2, 10);
        Edge flow = capacity.withValue(4);
        assertEquals(new Edge(1, 2, 4), flow);
        assertEquals(Capacity.of(10), capacity.value(), "the original edge is unchanged");
    }

    @Test
    @DisplayName("value equality follows record semantics")
    void valueEquality() {
        assertEquals(new Edge(0, 1, 7), new Edge(0, 1, 7));
    }

    @Test
    @DisplayName("renders a readable description")
    void toStringIsReadable() {
        assertEquals("0 -> 1: 5", new Edge(0, 1, 5).toString());
    }
}
