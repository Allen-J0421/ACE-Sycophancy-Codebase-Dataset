import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for the visitor-based traversal: {@link GraphTraversal},
 * {@link GraphVisitor}, and the example visitors.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("Graph traversal (visitor pattern)")
class GraphTraversalTest {

    /** Records the order of visited vertices and edges for assertions. */
    private static final class RecordingVisitor implements GraphVisitor {
        final List<Integer> vertices = new ArrayList<>();
        final List<String> edges = new ArrayList<>();

        @Override
        public void visitVertex(int vertex) {
            vertices.add(vertex);
        }

        @Override
        public void visitEdge(int from, int to) {
            edges.add(from + "->" + to);
        }
    }

    /** 0 -> 1 -> 2 -> 0 (cycle) and 2 -> 3; vertex 3 is a sink. */
    private static DirectedGraph sample() {
        return DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(2, 3)
                .build();
    }

    @Nested
    @DisplayName("GraphTraversal")
    class TraversalTests {

        @Test
        @DisplayName("visitAll visits every vertex and edge once, in order")
        void visitAllCoversGraph() {
            RecordingVisitor visitor = new RecordingVisitor();
            GraphTraversal.visitAll(sample(), visitor);

            assertEquals(List.of(0, 1, 2, 3), visitor.vertices);
            assertEquals(List.of("0->1", "1->2", "2->0", "2->3"), visitor.edges);
        }

        @Test
        @DisplayName("visitAll on an empty graph visits nothing")
        void visitAllEmpty() {
            RecordingVisitor visitor = new RecordingVisitor();
            GraphTraversal.visitAll(DirectedGraph.builder(0).build(), visitor);
            assertTrue(visitor.vertices.isEmpty());
            assertTrue(visitor.edges.isEmpty());
        }

        @Test
        @DisplayName("traverseFrom visits each reachable vertex exactly once")
        void traverseFromVisitsReachableOnce() {
            RecordingVisitor visitor = new RecordingVisitor();
            GraphTraversal.traverseFrom(sample(), 0, visitor);

            // all four are reachable from 0; each appears once despite the cycle
            assertEquals(4, visitor.vertices.size());
            assertEquals(Set.of(0, 1, 2, 3), Set.copyOf(visitor.vertices));
            assertEquals(0, visitor.vertices.get(0), "source is visited first");
        }

        @Test
        @DisplayName("traverseFrom rejects an out-of-range source")
        void traverseFromRejectsBadSource() {
            assertThrows(IndexOutOfBoundsException.class,
                    () -> GraphTraversal.traverseFrom(sample(), 9, new RecordingVisitor()));
        }
    }

    @Nested
    @DisplayName("OutDegreeDistributionVisitor")
    class OutDegreeTests {

        @Test
        @DisplayName("counts how many vertices have each out-degree, including zero")
        void distribution() {
            // out-degrees: 0->1, 1->1, 2->2, 3->0
            OutDegreeDistributionVisitor visitor = new OutDegreeDistributionVisitor();
            GraphTraversal.visitAll(sample(), visitor);

            assertEquals(Map.of(0, 1, 1, 2, 2, 1), visitor.distribution());
        }

        @Test
        @DisplayName("counts sum to the vertex count")
        void countsSumToVertices() {
            OutDegreeDistributionVisitor visitor = new OutDegreeDistributionVisitor();
            DirectedGraph graph = DirectedGraph.builder(5).addEdge(0, 1).build();
            GraphTraversal.visitAll(graph, visitor);

            int total = visitor.distribution().values().stream().mapToInt(Integer::intValue).sum();
            assertEquals(5, total);
        }
    }

    @Nested
    @DisplayName("ReachableVerticesVisitor")
    class ReachableTests {

        @Test
        @DisplayName("includes the source and excludes unreachable vertices")
        void reachableSet() {
            DirectedGraph graph = DirectedGraph.builder(5)
                    .addEdge(0, 1)
                    .addEdge(1, 2)
                    .build(); // 3 and 4 are isolated

            ReachableVerticesVisitor visitor = new ReachableVerticesVisitor();
            GraphTraversal.traverseFrom(graph, 0, visitor);

            assertEquals(Set.of(0, 1, 2), visitor.reachable());
            assertFalse(visitor.reachable().contains(3));
        }

        @Test
        @DisplayName("a sink vertex reaches only itself")
        void sinkReachesItself() {
            ReachableVerticesVisitor visitor = new ReachableVerticesVisitor();
            GraphTraversal.traverseFrom(sample(), 3, visitor);
            assertEquals(Set.of(3), visitor.reachable());
        }
    }

    @Test
    @DisplayName("a custom visitor adds an operation without touching DirectedGraph")
    void customVisitorExtensibility() {
        // An ad-hoc operation: count the edges. No change to DirectedGraph needed.
        int[] edgeCount = {0};
        GraphVisitor edgeCounter = new GraphVisitor() {
            @Override
            public void visitEdge(int from, int to) {
                edgeCount[0]++;
            }
        };
        GraphTraversal.visitAll(sample(), edgeCounter);
        assertEquals(4, edgeCount[0]);
    }
}
