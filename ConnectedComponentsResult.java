import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ConnectedComponentsResult {

    private final List<ConnectedComponent> components;

    ConnectedComponentsResult(List<ConnectedComponent> components) {
        Objects.requireNonNull(components, "components");

        this.components = List.copyOf(new ArrayList<>(components));
    }

    int componentCount() {
        return components.size();
    }

    ConnectedComponent componentAt(int componentIndex) {
        if (componentIndex < 0 || componentIndex >= components.size()) {
            throw new IllegalArgumentException("Component index out of bounds: " + componentIndex);
        }

        return components.get(componentIndex);
    }

    List<ConnectedComponent> components() {
        return components;
    }

    String format() {
        return ComponentFormatter.format(this);
    }

    @Override
    public String toString() {
        return components.toString();
    }
}
