import java.util.Iterator;
import java.util.List;

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

    @Override
    public Iterator<StronglyConnectedComponent> iterator() {
        return components.iterator();
    }
}
