import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public record StronglyConnectedComponentsResult(List<StronglyConnectedComponent> components)
        implements Iterable<StronglyConnectedComponent> {

    public StronglyConnectedComponentsResult {
        components = List.copyOf(components);
    }

    public int size() {
        return components.size();
    }

    public Stream<StronglyConnectedComponent> stream() {
        return components.stream();
    }

    @Override
    public Iterator<StronglyConnectedComponent> iterator() {
        return components.iterator();
    }
}
