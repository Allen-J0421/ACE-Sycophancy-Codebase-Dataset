import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for {@link TopologicalSorter}.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("TopologicalSorter")
class TopologicalSorterTest {

    private final TopologicalSorter sorter = new TopologicalSorter();

    /** Asserts {@code order} is a permutation of all vertices respecting every edge. */
    private static void assertValidTopologicalOrder(List<Integer> order, DirectedGraph graph) {
        int n = graph.vertices();
        assertEquals(n, order.size(), "order must cover every vertex: " + order);

        int[] position = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);
            assertFalse(seen[vertex], "vertex " + vertex + " appears twice: " + order);
            seen[vertex] = true;
            position[vertex] = i;
        }
        for (int u = 0; u < n; u++) {
            for (int v : graph.neighbors(u)) {
                assertTrue(position[u] < position[v],
                        "edge " + u + " -> " + v + " violates the order " + order);
            }
        }
    }

    @Test
    @DisplayName("orders a diamond DAG so every edge points forward")
    void diamondDag() {
        DirectedGraph dag = DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(0, 2)
                .addEdge(1, 3)
                .addEdge(2, 3)
                .build();
        assertValidTopologicalOrder(sorter.sort(dag).orElseThrow(), dag);
    }

    @Test
    @DisplayName("orders a linear chain in sequence")
    void linearChain() {
        DirectedGraph chain = DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .build();
        assertEquals(List.of(0, 1, 2, 3), sorter.sort(chain).orElseThrow());
    }

    @Test
    @DisplayName("orders a disconnected DAG, covering isolated vertices")
    void disconnectedDag() {
        DirectedGraph graph = DirectedGraph.builder(5)
                .addEdge(0, 1)   // component {0,1}
                .addEdge(2, 3)   // component {2,3}
                .build();        // vertex 4 is isolated
        assertValidTopologicalOrder(sorter.sort(graph).orElseThrow(), graph);
    }

    @Test
    @DisplayName("treats the empty graph as a (trivially ordered) DAG")
    void emptyGraph() {
        Optional<List<Integer>> order = sorter.sort(DirectedGraph.builder(0).build());
        assertEquals(Optional.of(List.of()), order);
        assertTrue(sorter.isDag(DirectedGraph.builder(0).build()));
    }

    @Test
    @DisplayName("returns empty for a cyclic graph")
    void cyclicGraph() {
        DirectedGraph cyclic = DirectedGraph.builder(3)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .build();
        assertEquals(Optional.empty(), sorter.sort(cyclic));
        assertFalse(sorter.isDag(cyclic));
    }

    @Test
    @DisplayName("treats a self-loop as cyclic (not a DAG)")
    void selfLoop() {
        DirectedGraph graph = DirectedGraph.builder(2).addEdge(1, 1).build();
        assertFalse(sorter.isDag(graph));
    }

    @Test
    @DisplayName("orders only the acyclic part is impossible: a tail into a cycle is not a DAG")
    void tailIntoCycle() {
        DirectedGraph graph = DirectedGraph.builder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 1)   // cycle 1->2->3->1
                .build();
        assertEquals(Optional.empty(), sorter.sort(graph));
    }

    @Test
    @DisplayName("the returned ordering is unmodifiable")
    void orderingUnmodifiable() {
        DirectedGraph dag = DirectedGraph.builder(2).addEdge(0, 1).build();
        List<Integer> order = sorter.sort(dag).orElseThrow();
        assertThrows(UnsupportedOperationException.class, () -> order.add(99));
    }

    @Test
    @DisplayName("agrees with the cycle detector on DAG-ness")
    void agreesWithCycleDetector() {
        DirectedGraph[] graphs = {
            DirectedGraph.builder(3).addEdge(0, 1).addEdge(1, 2).build(),               // DAG
            DirectedGraph.builder(3).addEdge(0, 1).addEdge(1, 2).addEdge(2, 0).build(), // cyclic
            DirectedGraph.builder(1).addEdge(0, 0).build(),                             // self-loop
            DirectedGraph.builder(4).build(),                                           // edgeless
        };
        CycleDetector detector = CycleDetector.create(CycleDetectionAlgorithm.KAHN);
        for (DirectedGraph graph : graphs) {
            assertEquals(detector.hasCycle(graph), !sorter.isDag(graph),
                    "sorter and detector disagree on " + graph);
        }
    }
}
