import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The outcome of running a {@link StronglyConnectedComponentsFinder}: the full
 * collection of strongly connected components discovered in a graph, plus a few
 * convenience queries over them.
 *
 * <p>A vertex-to-component index is built once at construction so that
 * {@link #componentContaining} answers in O(1) rather than scanning every
 * component.
 *
 * @param <V> the vertex label type
 */
public final class StronglyConnectedComponentsResult<V>
        implements Iterable<StronglyConnectedComponent<V>> {

    private final List<StronglyConnectedComponent<V>> components;
    private final Map<V, StronglyConnectedComponent<V>> componentByVertex;

    public StronglyConnectedComponentsResult(List<StronglyConnectedComponent<V>> components) {
        this.components = Collections.unmodifiableList(new ArrayList<>(components));
        Map<V, StronglyConnectedComponent<V>> index = new HashMap<>();
        for (StronglyConnectedComponent<V> component : this.components) {
            for (V vertex : component.vertices()) {
                index.put(vertex, component);
            }
        }
        this.componentByVertex = Collections.unmodifiableMap(index);
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
     * @return the component containing {@code vertex}, if any (O(1))
     */
    public Optional<StronglyConnectedComponent<V>> componentContaining(V vertex) {
        return Optional.ofNullable(componentByVertex.get(vertex));
    }

    @Override
    public Iterator<StronglyConnectedComponent<V>> iterator() {
        return components.iterator();
    }
}
