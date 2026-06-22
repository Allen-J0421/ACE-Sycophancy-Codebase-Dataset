import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Graph")
class GraphTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("accepts zero vertices")
        void zeroVertices() {
            assertDoesNotThrow(() -> new Graph(0));
            assertEquals(0, new Graph(0).vertexCount());
        }

        @Test
        @DisplayName("accepts positive vertex count")
        void positiveVertexCount() {
            Graph graph = new Graph(5);
            assertEquals(5, graph.vertexCount());
        }

        @Test
        @DisplayName("rejects negative vertex count")
        void negativeVertexCount() {
            assertThrows(IllegalArgumentException.class, () -> new Graph(-1));
        }

        @Test
        @DisplayName("initialises all vertices with no neighbors")
        void allVerticesStartIsolated() {
            Graph graph = new Graph(4);
            for (int i = 0; i < 4; i++) {
                assertTrue(graph.neighborsOf(i).isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("addEdge")
    class AddEdge {

        @Test
        @DisplayName("connects both endpoints bidirectionally")
        void bidirectional() {
            Graph graph = new Graph(3);
            graph.addEdge(0, 1);
            assertTrue(graph.neighborsOf(0).contains(1));
            assertTrue(graph.neighborsOf(1).contains(0));
        }

        @Test
        @DisplayName("does not add unrelated neighbors")
        void doesNotPollute() {
            Graph graph = new Graph(3);
            graph.addEdge(0, 1);
            assertTrue(graph.neighborsOf(2).isEmpty());
        }

        @Test
        @DisplayName("duplicate edge is silently ignored")
        void duplicateEdgeIgnored() {
            Graph graph = new Graph(3);
            graph.addEdge(0, 1);
            graph.addEdge(0, 1);
            assertEquals(1, graph.neighborsOf(0).size());
            assertEquals(1, graph.neighborsOf(1).size());
        }

        @Test
        @DisplayName("rejects self-loop")
        void selfLoop() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(1, 1));
        }

        @Test
        @DisplayName("rejects negative u")
        void negativeU() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(-1, 1));
        }

        @Test
        @DisplayName("rejects negative v")
        void negativeV() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(0, -1));
        }

        @Test
        @DisplayName("rejects u >= vertexCount")
        void uTooLarge() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(3, 1));
        }

        @Test
        @DisplayName("rejects v >= vertexCount")
        void vTooLarge() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(0, 3));
        }

        @Test
        @DisplayName("rejects any vertex on an empty graph")
        void emptyGraph() {
            Graph graph = new Graph(0);
            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(0, 1));
        }
    }

    @Nested
    @DisplayName("neighborsOf")
    class NeighborsOf {

        @Test
        @DisplayName("returns correct neighbors after edges are added")
        void correctNeighbors() {
            Graph graph = new Graph(4);
            graph.addEdge(0, 1);
            graph.addEdge(0, 2);
            Set<Integer> neighbors = graph.neighborsOf(0);
            assertEquals(Set.of(1, 2), neighbors);
        }

        @Test
        @DisplayName("returns empty set for an isolated vertex")
        void isolatedVertex() {
            Graph graph = new Graph(3);
            assertTrue(graph.neighborsOf(2).isEmpty());
        }

        @Test
        @DisplayName("returned set is unmodifiable")
        void unmodifiable() {
            Graph graph = new Graph(3);
            graph.addEdge(0, 1);
            Set<Integer> neighbors = graph.neighborsOf(0);
            assertThrows(UnsupportedOperationException.class, () -> neighbors.add(2));
            assertThrows(UnsupportedOperationException.class, neighbors::clear);
        }

        @Test
        @DisplayName("rejects out-of-bounds vertex")
        void outOfBounds() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.neighborsOf(5));
        }

        @Test
        @DisplayName("rejects negative vertex")
        void negativeVertex() {
            Graph graph = new Graph(3);
            assertThrows(IllegalArgumentException.class, () -> graph.neighborsOf(-1));
        }

        @Test
        @DisplayName("rejects any vertex on an empty graph")
        void emptyGraph() {
            Graph graph = new Graph(0);
            assertThrows(IllegalArgumentException.class, () -> graph.neighborsOf(0));
        }
    }
}
