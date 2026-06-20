import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("builds fluently, equivalent to from(...)")
        void fluentEquivalentToFrom() {
            DirectedGraph built = DirectedGraph.builder(4)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .addEdge(2, 0)
                    .addEdge(2, 3)
                    .build();
            assertEquals(4, built.vertices());
            assertEquals(List.of(1), built.neighbors(0));
            assertEquals(List.of(0, 3), built.neighbors(2));
        }

        @Test
        @DisplayName("addEdges adds a batch of pairs")
        void addEdgesBatch() {
            DirectedGraph graph = DirectedGraph.builder(3)
                    .addEdges(new int[][] {{0, 1}, {0, 2}, {1, 2}})
                    .build();
            assertEquals(List.of(1, 2), graph.neighbors(0));
            assertEquals(List.of(2), graph.neighbors(1));
        }

        @Test
        @DisplayName("a fixed-size builder rejects an out-of-range endpoint")
        void fixedSizeRejectsOutOfRange() {
            assertThrows(IndexOutOfBoundsException.class,
                    () -> DirectedGraph.builder(2).addEdge(0, 5));
        }

        @Test
        @DisplayName("a growing builder expands to fit referenced vertices")
        void growingBuilderExpands() {
            DirectedGraph graph = DirectedGraph.builder()
                    .addEdge(0, 3)
                    .addEdge(3, 7)
                    .build();
            assertEquals(8, graph.vertices());          // highest index 7 -> 8 vertices
            assertEquals(List.of(3), graph.neighbors(0));
            assertEquals(List.of(), graph.neighbors(7));
        }

        @Test
        @DisplayName("ensureVertices reserves isolated trailing vertices")
        void ensureVerticesReserves() {
            DirectedGraph graph = DirectedGraph.builder()
                    .addEdge(0, 1)
                    .ensureVertices(5)
                    .build();
            assertEquals(5, graph.vertices());
            assertEquals(List.of(), graph.neighbors(4));
        }

        @Test
        @DisplayName("a growing builder still rejects a negative endpoint")
        void growingBuilderRejectsNegative() {
            assertThrows(IndexOutOfBoundsException.class,
                    () -> DirectedGraph.builder().addEdge(0, -1));
        }

        @Test
        @DisplayName("a fixed-size builder cannot be grown past its vertex count")
        void ensureVerticesCannotExceedFixedSize() {
            assertThrows(IndexOutOfBoundsException.class,
                    () -> DirectedGraph.builder(2).ensureVertices(5));
        }

        @Test
        @DisplayName("the builder is single-use")
        void singleUse() {
            DirectedGraph.Builder builder = DirectedGraph.builder(2).addEdge(0, 1);
            builder.build();
            assertThrows(IllegalStateException.class, () -> builder.addEdge(1, 0));
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        @DisplayName("rejects a negative vertex count")
        void negativeVertexCount() {
            assertThrows(IllegalArgumentException.class, () -> DirectedGraph.builder(-1));
        }
    }
}
