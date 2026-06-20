import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The outcome of running a {@link StronglyConnectedComponentsFinder}: the full
 * collection of strongly connected components discovered in a graph, plus a few
 * convenience queries over them.
 *
 * @param <V> the vertex label type
 */
public final class StronglyConnectedComponentsResult<V> {

    private final List<StronglyConnectedComponent<V>> components;

    public StronglyConnectedComponentsResult(List<StronglyConnectedComponent<V>> components) {
        this.components = Collections.unmodifiableList(
                new ArrayList<>(components));
    }

    /** @return all components (unmodifiable). */
    public List<StronglyConnectedComponent<V>> components() {
        return components;
    }

    /** @return the number of strongly connected components. */
    public int count() {
        return components.size();
    }

    /**
     * @param vertex a vertex
     * @return the component containing {@code vertex}, if any
     */
    public Optional<StronglyConnectedComponent<V>> componentContaining(V vertex) {
        for (StronglyConnectedComponent<V> component : components) {
            if (component.contains(vertex)) {
                return Optional.of(component);
            }
        }
        return Optional.empty();
    }
}
