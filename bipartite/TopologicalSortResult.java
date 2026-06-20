package bipartite;

import java.util.List;

/**
 * The outcome of a topological sort, modelled as a sealed type — mirroring
 * {@link BipartiteResult} — so callers handle exactly the two possible cases and
 * illegal states cannot be represented.
 *
 * <p>Either the graph is acyclic, in which case a {@linkplain Sorted valid
 * topological order} is available, or it contains a {@linkplain Cyclic cycle}
 * (which makes a topological order impossible), in which case a directed-cycle
 * witness is available. Callers can branch with a switch:
 *
 * <pre>{@code
 * switch (new TopologicalSort().sort(graph)) {
 *     case TopologicalSortResult.Sorted s -> use(s.order());
 *     case TopologicalSortResult.Cyclic c -> report(c.cycle());
 * }
 * }</pre>
 */
public sealed interface TopologicalSortResult
        permits TopologicalSortResult.Sorted, TopologicalSortResult.Cyclic {

    /** @return whether the graph is acyclic (and therefore sortable) */
    boolean isAcyclic();

    /**
     * Result for an acyclic graph.
     *
     * @param order every vertex in a dependency-respecting order: for each edge
     *              {@code u -> v}, {@code u} appears before {@code v}
     */
    record Sorted(List<Integer> order) implements TopologicalSortResult {

        public Sorted {
            order = List.copyOf(order);
        }

        @Override
        public boolean isAcyclic() {
            return true;
        }
    }

    /**
     * Result for a graph that contains a cycle, carrying a witness.
     *
     * @param cycle vertices forming a directed cycle (the closing edge runs from
     *              the last vertex back to the first); its presence proves no
     *              topological order exists
     */
    record Cyclic(List<Integer> cycle) implements TopologicalSortResult {

        public Cyclic {
            cycle = List.copyOf(cycle);
        }

        @Override
        public boolean isAcyclic() {
            return false;
        }
    }
}
