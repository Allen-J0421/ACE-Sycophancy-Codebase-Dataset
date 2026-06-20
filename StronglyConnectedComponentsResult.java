import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StronglyConnectedComponentsResult {
    private final List<StronglyConnectedComponent> components;

    public StronglyConnectedComponentsResult(List<StronglyConnectedComponent> components) {
        this.components = freezeComponents(components);
    }

    public List<StronglyConnectedComponent> components() {
        return components;
    }

    public int componentCount() {
        return components.size();
    }

    public String toDisplayString() {
        StringBuilder output = new StringBuilder("Strongly Connected Components:\n");

        for (StronglyConnectedComponent component : components) {
            output.append(component.toDisplayString()).append('\n');
        }

        return output.toString();
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    private static List<StronglyConnectedComponent> freezeComponents(
        List<StronglyConnectedComponent> components
    ) {
        if (components == null) {
            throw new IllegalArgumentException("Components must not be null.");
        }

        List<StronglyConnectedComponent> immutableComponents = new ArrayList<>(components.size());
        for (StronglyConnectedComponent component : components) {
            if (component == null) {
                throw new IllegalArgumentException("Components must not contain null values.");
            }
            immutableComponents.add(component);
        }

        return Collections.unmodifiableList(immutableComponents);
    }
}
