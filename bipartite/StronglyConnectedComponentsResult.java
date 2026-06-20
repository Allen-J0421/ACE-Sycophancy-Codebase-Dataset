package bipartite;

import java.util.ArrayList;
import java.util.List;

/**
 * The outcome of a strongly-connected-components computation, modelled as a
 * sealed type — mirroring {@link BipartiteResult}, {@link TopologicalSortResult},
 * and {@link ShortestPathResult} — so callers handle exactly the two meaningful
 * cases. Every graph decomposes into strongly connected components; the
 * distinction surfaced here is whether that decomposition is a single component
 * spanning the whole graph or several.
 *
 * <p>Either the graph is {@linkplain StronglyConnected strongly connected} (one
 * component containing every vertex) or it is {@linkplain Fragmented} (zero or
 * more than one component). Both expose the full list of components via
 * {@link #components()}; callers needing the distinction can switch:
 *
 * <pre>{@code
 * switch (new StronglyConnectedComponents().compute(graph)) {
 *     case StronglyConnectedComponentsResult.StronglyConnected s -> use(s.component());
 *     case StronglyConnectedComponentsResult.Fragmented f        -> use(f.components());
 * }
 * }</pre>
 */
public sealed interface StronglyConnectedComponentsResult
        permits StronglyConnectedComponentsResult.StronglyConnected,
                StronglyConnectedComponentsResult.Fragmented {

    /** @return whether the whole graph forms a single strongly connected component */
    boolean isStronglyConnected();

    /**
     * @return all strongly connected components, each a list of vertex ids sorted
     *         ascending; the components are in reverse topological order of the
     *         condensation (a component appears before the components it can reach)
     */
    List<List<Integer>> components();

    /**
     * Result for a graph whose every vertex lies in one strongly connected
     * component.
     *
     * @param component the single component, containing all vertices
     */
    record StronglyConnected(List<Integer> component) implements StronglyConnectedComponentsResult {

        public StronglyConnected {
            component = List.copyOf(component);
        }

        @Override
        public boolean isStronglyConnected() {
            return true;
        }

        @Override
        public List<List<Integer>> components() {
            return List.of(component);
        }
    }

    /**
     * Result for a graph that splits into zero or several strongly connected
     * components (the empty graph yields an empty list).
     *
     * @param components the components, in reverse topological order
     */
    record Fragmented(List<List<Integer>> components) implements StronglyConnectedComponentsResult {

        public Fragmented {
            List<List<Integer>> defensive = new ArrayList<>(components.size());
            for (List<Integer> component : components) {
                defensive.add(List.copyOf(component));
            }
            components = List.copyOf(defensive);
        }

        @Override
        public boolean isStronglyConnected() {
            return false;
        }
    }
}
