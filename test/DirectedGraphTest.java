import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit 5 test suite for {@link DirectedGraph} construction and access.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("DirectedGraph")
class DirectedGraphTest {

    @Test
    @DisplayName("exposes vertex count and out-neighbors in insertion order")
    void neighborsInOrder() {
        DirectedGraph graph = DirectedGraph.from(3, new int[][] {{0, 2}, {0, 1}});
        assertEquals(3, graph.vertices());
        assertEquals(List.of(2, 1), graph.neighbors(0));
        assertEquals(List.of(), graph.neighbors(1));
    }

    @Test
    @DisplayName("preserves parallel edges")
    void parallelEdges() {
        DirectedGraph graph = DirectedGraph.from(2, new int[][] {{0, 1}, {0, 1}});
        assertEquals(List.of(1, 1), graph.neighbors(0));
    }

    @Test
    @DisplayName("rejects a negative vertex count")
    void negativeVertexCount() {
        assertThrows(IllegalArgumentException.class,
                () -> DirectedGraph.from(-1, new int[][] {}));
    }

    @Test
    @DisplayName("rejects an edge endpoint outside the vertex range")
    void edgeOutOfRange() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> DirectedGraph.from(2, new int[][] {{0, 5}}));
    }

    @Test
    @DisplayName("rejects a malformed edge that is not a {from, to} pair")
    void malformedEdge() {
        assertThrows(IllegalArgumentException.class,
                () -> DirectedGraph.from(2, new int[][] {{0}}));
    }

    @Test
    @DisplayName("rejects querying neighbors of an invalid vertex")
    void neighborsOutOfRange() {
        DirectedGraph graph = DirectedGraph.from(2, new int[][] {});
        assertThrows(IndexOutOfBoundsException.class, () -> graph.neighbors(2));
    }

    @Test
    @DisplayName("returns an unmodifiable neighbor view")
    void neighborsUnmodifiable() {
        DirectedGraph graph = DirectedGraph.from(2, new int[][] {{0, 1}});
        assertThrows(UnsupportedOperationException.class,
                () -> graph.neighbors(0).add(99));
    }
}
