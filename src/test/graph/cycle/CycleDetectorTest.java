package graph.cycle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CycleDetectorTest {

    // ------------------------------------------------------------------ fixtures

    private static final DirectedGraph ACYCLIC =
            GraphBuilder.withVertices(4)
                    .edge(0, 1).edge(0, 2)
                    .edge(1, 3).edge(2, 3)
                    .build();

    private static final DirectedGraph CYCLIC =
            GraphBuilder.withVertices(4)
                    .edge(0, 1).edge(1, 2)
                    .edge(2, 0).edge(2, 3)
                    .build();

    private static final DirectedGraph DISCONNECTED_WITH_CYCLE =
            GraphBuilder.withVertices(4)
                    .edge(0, 1)           // acyclic component
                    .edge(2, 3).edge(3, 2) // cyclic component
                    .build();

    private static final DirectedGraph DISCONNECTED_WITHOUT_CYCLE =
            GraphBuilder.withVertices(4)
                    .edge(0, 1)
                    .edge(2, 3)
                    .build();

    private static final DirectedGraph SELF_LOOP =
            GraphBuilder.withVertices(2)
                    .edge(0, 1).edge(1, 1)
                    .build();

    private static final DirectedGraph SINGLE_VERTEX = GraphBuilder.withVertices(1).build();

    // ------------------------------------------------------------------ tests

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    void acyclicGraph(Algorithm alg) {
        assertFalse(detector(alg).hasCycle(ACYCLIC));
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    void cyclicGraph(Algorithm alg) {
        assertTrue(detector(alg).hasCycle(CYCLIC));
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    void disconnectedGraphWithCycle(Algorithm alg) {
        assertTrue(detector(alg).hasCycle(DISCONNECTED_WITH_CYCLE));
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    void disconnectedGraphWithoutCycle(Algorithm alg) {
        assertFalse(detector(alg).hasCycle(DISCONNECTED_WITHOUT_CYCLE));
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    void selfLoop(Algorithm alg) {
        assertTrue(detector(alg).hasCycle(SELF_LOOP));
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    void singleVertexNoEdges(Algorithm alg) {
        assertFalse(detector(alg).hasCycle(SINGLE_VERTEX));
    }

    // ------------------------------------------------------------------ helpers

    private static CycleDetector detector(Algorithm alg) {
        return CycleDetectorFactory.create(alg);
    }
}
