package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the read-only behaviour of a built {@link Graph}. */
class GraphTest {

    @Test
    void exposesItsVertexCount() {
        Graph graph = new GraphBuilder(4).build();
        assertEquals(4, graph.vertexCount());
    }

    @Test
    void emptyGraphHasNoVertices() {
        assertEquals(0, new GraphBuilder(0).build().vertexCount());
    }

    @Test
    void neighboursViewIsUnmodifiable() {
        Graph graph = new GraphBuilder(2).addEdge(0, 1).build();
        assertThrows(UnsupportedOperationException.class, () -> graph.neighbours(0).add(99));
    }

    @Test
    void neighboursRejectsOutOfRangeVertex() {
        Graph graph = new GraphBuilder(2).build();
        assertThrows(IndexOutOfBoundsException.class, () -> graph.neighbours(5));
    }

    @Test
    void verticesWithoutEdgesHaveNoNeighbours() {
        Graph graph = new GraphBuilder(3).build();
        for (int v = 0; v < 3; v++) {
            assertTrue(graph.neighbours(v).isEmpty());
        }
    }
}
