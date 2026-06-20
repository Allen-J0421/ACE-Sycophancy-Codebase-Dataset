import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for {@link CycleDetector}.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("CycleDetector")
class CycleDetectorTest {

    private final CycleDetector detector = new CycleDetector();

    /** Asserts that {@code cycle} is a valid closed walk along real edges of {@code graph}. */
    private static void assertIsCycleOf(Cycle cycle, DirectedGraph graph) {
        List<Integer> v = cycle.vertices();
        assertTrue(v.size() >= 2, "a cycle needs at least two entries: " + v);
        assertEquals(v.get(0), v.get(v.size() - 1), "first and last vertex must match: " + v);
        for (int i = 0; i < v.size() - 1; i++) {
            int from = v.get(i);
            int to = v.get(i + 1);
            assertTrue(graph.neighbors(from).contains(to),
                    "edge " + from + " -> " + to + " is not in the graph");
        }
    }

    @Nested
    @DisplayName("hasCycle")
    class HasCycle {

        @Test
        @DisplayName("reports a cycle in a graph that loops back")
        void detectsCycle() {
            DirectedGraph graph = DirectedGraph.from(4, new int[][] {
                {0, 1}, {1, 2}, {2, 0}, {2, 3}
            });
            assertTrue(detector.hasCycle(graph));
        }

        @Test
        @DisplayName("reports no cycle in a DAG")
        void acyclicDag() {
            DirectedGraph graph = DirectedGraph.from(4, new int[][] {
                {0, 1}, {0, 2}, {1, 3}, {2, 3}
            });
            assertFalse(detector.hasCycle(graph));
        }

        @Test
        @DisplayName("treats a self-loop as a cycle")
        void selfLoop() {
            assertTrue(detector.hasCycle(DirectedGraph.from(1, new int[][] {{0, 0}})));
        }

        @Test
        @DisplayName("an empty graph is acyclic")
        void emptyGraph() {
            assertFalse(detector.hasCycle(DirectedGraph.from(0, new int[][] {})));
        }

        @Test
        @DisplayName("a graph with vertices but no edges is acyclic")
        void noEdges() {
            assertFalse(detector.hasCycle(DirectedGraph.from(3, new int[][] {})));
        }

        @Test
        @DisplayName("finds a cycle living in a disconnected component")
        void cycleInSeparateComponent() {
            DirectedGraph graph = DirectedGraph.from(4, new int[][] {
                {0, 1},          // acyclic component
                {2, 3}, {3, 2}   // cyclic component
            });
            assertTrue(detector.hasCycle(graph));
        }
    }

    @Nested
    @DisplayName("findCycle")
    class FindCycle {

        @Test
        @DisplayName("returns empty for an acyclic graph")
        void emptyWhenAcyclic() {
            DirectedGraph graph = DirectedGraph.from(3, new int[][] {{0, 1}, {1, 2}});
            assertEquals(Optional.empty(), detector.findCycle(graph));
        }

        @Test
        @DisplayName("returns a valid closed walk along graph edges")
        void returnsValidCycle() {
            DirectedGraph graph = DirectedGraph.from(4, new int[][] {
                {0, 1}, {1, 2}, {2, 0}, {2, 3}
            });
            Optional<Cycle> cycle = detector.findCycle(graph);
            assertTrue(cycle.isPresent());
            assertIsCycleOf(cycle.get(), graph);
        }

        @Test
        @DisplayName("represents a self-loop as [v, v]")
        void selfLoopShape() {
            Cycle cycle = detector.findCycle(DirectedGraph.from(2, new int[][] {{1, 1}})).orElseThrow();
            assertEquals(List.of(1, 1), cycle.vertices());
        }

        @Test
        @DisplayName("locates the cycle in a disconnected component")
        void cycleInSeparateComponent() {
            DirectedGraph graph = DirectedGraph.from(4, new int[][] {
                {0, 1}, {2, 3}, {3, 2}
            });
            Cycle cycle = detector.findCycle(graph).orElseThrow();
            assertNotNull(cycle);
            assertIsCycleOf(cycle, graph);
            assertFalse(cycle.vertices().contains(0), "component {0,1} is acyclic: " + cycle);
        }
    }
}
