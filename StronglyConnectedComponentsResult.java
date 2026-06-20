import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class StronglyConnectedComponentsResult implements Iterable<StronglyConnectedComponent> {
    private final List<StronglyConnectedComponent> components;

    public StronglyConnectedComponentsResult(List<StronglyConnectedComponent> components) {
        this.components = List.copyOf(components);
    }

    public List<StronglyConnectedComponent> getComponents() {
        return components;
    }

    public int count() {
        return components.size();
    }

    public Stream<StronglyConnectedComponent> stream() {
        return components.stream();
    }

    @Override
    public Iterator<StronglyConnectedComponent> iterator() {
        return components.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StronglyConnectedComponentsResult)) return false;
        return components.equals(((StronglyConnectedComponentsResult) o).components);
    }

    @Override
    public int hashCode() {
        return components.hashCode();
    }
}
