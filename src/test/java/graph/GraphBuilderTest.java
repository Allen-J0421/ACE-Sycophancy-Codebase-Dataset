package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests construction and validation performed by {@link GraphBuilder}. */
class GraphBuilderTest {

    @Test
    void negativeVertexCountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GraphBuilder(-1));
        assertThrows(IllegalArgumentException.class, () -> GraphBuilder.of(-1));
    }

    @Test
    void addEdgeCreatesAnUndirectedEdge() {
        Graph graph = new GraphBuilder(2).addEdge(0, 1).build();
        assertEquals(List.of(1), graph.neighbours(0));
        assertEquals(List.of(0), graph.neighbours(1));
    }

    @Test
    void addEdgeIsChainable() {
        GraphBuilder builder = new GraphBuilder(2);
        assertSame(builder, builder.addEdge(0, 1));
    }

    @Test
    void addEdgesAppliesEveryEdgeInTheList() {
        Graph graph = new GraphBuilder(3)
            .addEdges(new int[][] {{0, 1}, {1, 2}})
            .build();
        assertEquals(List.of(1), graph.neighbours(0));
        assertEquals(List.of(0, 2), graph.neighbours(1));
        assertEquals(List.of(1), graph.neighbours(2));
    }

    @Test
    void selfLoopRecordsBothEndpoints() {
        Graph graph = new GraphBuilder(1).addEdge(0, 0).build();
        assertEquals(List.of(0, 0), graph.neighbours(0));
    }

    @Test
    void addEdgeRejectsOutOfRangeEndpoints() {
        GraphBuilder builder = new GraphBuilder(2);
        assertThrows(IndexOutOfBoundsException.class, () -> builder.addEdge(0, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.addEdge(-1, 0));
    }

    @Test
    void addEdgesRejectsMalformedEdges() {
        GraphBuilder builder = new GraphBuilder(3);
        assertThrows(IllegalArgumentException.class, () -> builder.addEdges(new int[][] {{0, 1, 2}}));
        assertThrows(IllegalArgumentException.class, () -> builder.addEdges(new int[][] {{0}}));
    }

    @Test
    void builderIsReusableAndProducesIndependentGraphs() {
        GraphBuilder builder = new GraphBuilder(2).addEdge(0, 1);
        Graph first = builder.build();
        builder.addEdge(1, 0);
        Graph second = builder.build();

        // The earlier graph is unaffected by edges added after it was built.
        assertEquals(List.of(1), first.neighbours(0));
        assertEquals(List.of(1, 1), second.neighbours(0));
    }
}
