package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class GraphTest {

    @Test
    void newGraphHasGivenVertexCountAndNoEdges() {
        Graph graph = new Graph(4);
        assertEquals(4, graph.vertexCount());
        for (int v = 0; v < 4; v++) {
            assertTrue(graph.neighbours(v).isEmpty());
        }
    }

    @Test
    void emptyGraphIsAllowed() {
        assertEquals(0, new Graph(0).vertexCount());
    }

    @Test
    void negativeVertexCountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Graph(-1));
    }

    @Test
    void addEdgeIsUndirected() {
        Graph graph = new Graph(2);
        graph.addEdge(0, 1);
        assertEquals(List.of(1), graph.neighbours(0));
        assertEquals(List.of(0), graph.neighbours(1));
    }

    @Test
    void selfLoopRecordsBothEndpoints() {
        Graph graph = new Graph(1);
        graph.addEdge(0, 0);
        assertEquals(List.of(0, 0), graph.neighbours(0));
    }

    @Test
    void addEdgeRejectsOutOfRangeEndpoints() {
        Graph graph = new Graph(2);
        assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(0, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(-1, 0));
    }

    @Test
    void neighboursViewIsUnmodifiable() {
        Graph graph = new Graph(2);
        graph.addEdge(0, 1);
        assertThrows(UnsupportedOperationException.class, () -> graph.neighbours(0).add(99));
    }

    @Test
    void neighboursRejectsOutOfRangeVertex() {
        Graph graph = new Graph(2);
        assertThrows(IndexOutOfBoundsException.class, () -> graph.neighbours(5));
    }
}
