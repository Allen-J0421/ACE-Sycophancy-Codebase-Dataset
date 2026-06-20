import java.util.Optional;

/**
 * Strategy interface for directed-graph cycle detection. Callers depend on this
 * abstraction and pick a concrete algorithm at runtime — either by instantiating
 * an implementation directly or via {@link #create(CycleDetectionAlgorithm)}.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link DfsCycleDetector} — depth-first search with back-edge detection.</li>
 *   <li>{@link KahnCycleDetector} — Kahn's topological elimination.</li>
 * </ul>
 *
 * <p>All implementations agree on results; they differ only in approach and in
 * which operation they compute most cheaply.
 */
interface CycleDetector {

    /**
     * Finds one cycle if the graph contains any.
     *
     * @return a {@link Cycle} (e.g. {@code 0 -> 1 -> 2 -> 0}), or
     *         {@link Optional#empty()} if the graph is acyclic
     */
    Optional<Cycle> findCycle(DirectedGraph graph);

    /**
     * Returns {@code true} if {@code graph} contains at least one directed cycle.
     *
     * <p>The default derives the answer from {@link #findCycle}; an implementation
     * that can decide existence more cheaply than it can reconstruct a path should
     * override this.
     */
    default boolean hasCycle(DirectedGraph graph) {
        return findCycle(graph).isPresent();
    }

    /** Creates the detector for the requested algorithm. */
    static CycleDetector create(CycleDetectionAlgorithm algorithm) {
        return algorithm.create();
    }
}
