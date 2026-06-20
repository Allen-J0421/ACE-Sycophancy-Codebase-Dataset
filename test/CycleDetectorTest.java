import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for the {@link CycleDetector} strategy and its
 * implementations. Behavioural tests run against every {@link
 * CycleDetectionAlgorithm} so all implementations must satisfy the same contract.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("CycleDetector")
class CycleDetectorTest {

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

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("detects a cycle and returns a valid path")
    void detectsCycle(CycleDetectionAlgorithm algorithm) {
        CycleDetector detector = CycleDetector.create(algorithm);
        DirectedGraph graph = DirectedGraph.from(4, new int[][] {
            {0, 1}, {1, 2}, {2, 0}, {2, 3}
        });
        assertTrue(detector.hasCycle(graph));
        assertIsCycleOf(detector.findCycle(graph).orElseThrow(), graph);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("reports a DAG as acyclic")
    void acyclicDag(CycleDetectionAlgorithm algorithm) {
        CycleDetector detector = CycleDetector.create(algorithm);
        DirectedGraph graph = DirectedGraph.from(4, new int[][] {
            {0, 1}, {0, 2}, {1, 3}, {2, 3}
        });
        assertFalse(detector.hasCycle(graph));
        assertEquals(Optional.empty(), detector.findCycle(graph));
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("represents a self-loop as [v, v]")
    void selfLoop(CycleDetectionAlgorithm algorithm) {
        CycleDetector detector = CycleDetector.create(algorithm);
        DirectedGraph graph = DirectedGraph.from(2, new int[][] {{1, 1}});
        assertTrue(detector.hasCycle(graph));
        assertEquals(List.of(1, 1), detector.findCycle(graph).orElseThrow().vertices());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("treats empty and edgeless graphs as acyclic")
    void trivialGraphs(CycleDetectionAlgorithm algorithm) {
        CycleDetector detector = CycleDetector.create(algorithm);
        assertFalse(detector.hasCycle(DirectedGraph.from(0, new int[][] {})));
        assertFalse(detector.hasCycle(DirectedGraph.from(3, new int[][] {})));
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("finds a cycle living in a disconnected component")
    void cycleInSeparateComponent(CycleDetectionAlgorithm algorithm) {
        CycleDetector detector = CycleDetector.create(algorithm);
        DirectedGraph graph = DirectedGraph.from(4, new int[][] {
            {0, 1},          // acyclic component
            {2, 3}, {3, 2}   // cyclic component
        });
        Cycle cycle = detector.findCycle(graph).orElseThrow();
        assertIsCycleOf(cycle, graph);
        assertFalse(cycle.vertices().contains(0), "component {0,1} is acyclic: " + cycle);
    }

    @Test
    @DisplayName("all algorithms agree on hasCycle across a battery of graphs")
    void algorithmsAgree() {
        DirectedGraph[] graphs = {
            DirectedGraph.from(3, new int[][] {{0, 1}, {1, 2}, {2, 0}}),       // cycle
            DirectedGraph.from(3, new int[][] {{0, 1}, {1, 2}}),               // chain
            DirectedGraph.from(1, new int[][] {{0, 0}}),                       // self-loop
            DirectedGraph.from(5, new int[][] {{0, 1}, {1, 2}, {3, 4}}),       // forest
            DirectedGraph.from(4, new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 1}}), // tail into cycle
            DirectedGraph.from(0, new int[][] {}),                            // empty
        };
        CycleDetector dfs = CycleDetector.create(CycleDetectionAlgorithm.DFS);
        CycleDetector kahn = CycleDetector.create(CycleDetectionAlgorithm.KAHN);
        for (DirectedGraph graph : graphs) {
            assertEquals(dfs.hasCycle(graph), kahn.hasCycle(graph),
                    "algorithms disagree on " + graph);
        }
    }

    @Test
    @DisplayName("a tail leading into a cycle excludes the tail from the reported cycle")
    void tailExcludedFromCycle() {
        DirectedGraph graph = DirectedGraph.from(4, new int[][] {
            {0, 1}, {1, 2}, {2, 3}, {3, 1}   // 0 is a tail into the cycle 1->2->3->1
        });
        for (CycleDetectionAlgorithm algorithm : CycleDetectionAlgorithm.values()) {
            Cycle cycle = CycleDetector.create(algorithm).findCycle(graph).orElseThrow();
            assertIsCycleOf(cycle, graph);
            assertFalse(cycle.vertices().contains(0), algorithm + " included the tail: " + cycle);
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("handles a deep graph without stack overflow")
    void deepGraphNoStackOverflow(CycleDetectionAlgorithm algorithm) {
        int n = 200_000;
        CycleDetector detector = CycleDetector.create(algorithm);

        // A long acyclic chain 0 -> 1 -> ... -> n-1 (depth that would blow a recursive stack).
        int[][] chain = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            chain[i] = new int[] {i, i + 1};
        }
        assertFalse(detector.hasCycle(DirectedGraph.from(n, chain)));

        // The same chain closed into one big cycle by adding (n-1) -> 0.
        int[][] loop = new int[n][];
        System.arraycopy(chain, 0, loop, 0, n - 1);
        loop[n - 1] = new int[] {n - 1, 0};
        DirectedGraph cyclic = DirectedGraph.from(n, loop);
        assertTrue(detector.hasCycle(cyclic));
        assertIsCycleOf(detector.findCycle(cyclic).orElseThrow(), cyclic);
    }

    @Test
    @DisplayName("factory returns the requested implementation")
    void factorySelectsImplementation() {
        assertInstanceOf(DfsCycleDetector.class, CycleDetector.create(CycleDetectionAlgorithm.DFS));
        assertInstanceOf(KahnCycleDetector.class, CycleDetector.create(CycleDetectionAlgorithm.KAHN));
        // selection from a runtime string, e.g. config or CLI argument
        assertInstanceOf(KahnCycleDetector.class,
                CycleDetector.create(CycleDetectionAlgorithm.valueOf("KAHN")));
    }
}
