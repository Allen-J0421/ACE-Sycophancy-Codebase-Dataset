package bipartite;

import java.util.List;

/**
 * The outcome of a shortest-path query, modelled as a sealed type — mirroring
 * {@link BipartiteResult} and {@link TopologicalSortResult} — so callers handle
 * exactly the two possible cases and illegal states cannot be represented.
 *
 * <p>Either the target is reachable, in which case the total
 * {@linkplain Path#distance() distance} and the {@linkplain Path#vertices()
 * vertex sequence} of a shortest path are available, or it is
 * {@linkplain Unreachable}, in which case no path exists. Callers can branch with
 * a switch:
 *
 * <pre>{@code
 * switch (new ShortestPath().compute(graph, source, target)) {
 *     case ShortestPathResult.Path p        -> use(p.distance(), p.vertices());
 *     case ShortestPathResult.Unreachable u -> report(u.source(), u.target());
 * }
 * }</pre>
 */
public sealed interface ShortestPathResult
        permits ShortestPathResult.Path, ShortestPathResult.Unreachable {

    /** @return whether a path from the source to the target exists */
    boolean isReachable();

    /**
     * Result for a reachable target.
     *
     * @param distance the total weight of the shortest path
     * @param vertices the vertices along the shortest path, from source to target
     *                 inclusive
     */
    record Path(double distance, List<Integer> vertices) implements ShortestPathResult {

        public Path {
            vertices = List.copyOf(vertices);
        }

        @Override
        public boolean isReachable() {
            return true;
        }
    }

    /**
     * Result for a target that cannot be reached from the source.
     *
     * @param source the source vertex of the query
     * @param target the unreachable target vertex
     */
    record Unreachable(int source, int target) implements ShortestPathResult {

        @Override
        public boolean isReachable() {
            return false;
        }
    }
}
