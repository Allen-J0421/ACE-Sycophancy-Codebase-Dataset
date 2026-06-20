import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ConnectedComponentsResult {

    private final List<List<Integer>> components;

    ConnectedComponentsResult(List<List<Integer>> components) {
        Objects.requireNonNull(components, "components");

        List<List<Integer>> snapshot = new ArrayList<>(components.size());
        for (List<Integer> component : components) {
            snapshot.add(List.copyOf(component));
        }

        this.components = List.copyOf(snapshot);
    }

    int componentCount() {
        return components.size();
    }

    List<List<Integer>> components() {
        return components;
    }

    String format() {
        return ComponentFormatter.format(components);
    }

    @Override
    public String toString() {
        return components.toString();
    }
}
