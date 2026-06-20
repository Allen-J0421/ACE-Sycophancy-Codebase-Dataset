import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StronglyConnectedComponentsResult {
    private final List<List<Integer>> components;

    public StronglyConnectedComponentsResult(List<List<Integer>> components) {
        this.components = freezeComponents(components);
    }

    public List<List<Integer>> components() {
        return components;
    }

    public int componentCount() {
        return components.size();
    }

    public String toDisplayString() {
        StringBuilder output = new StringBuilder("Strongly Connected Components:\n");

        for (List<Integer> component : components) {
            for (int vertex : component) {
                output.append(vertex).append(' ');
            }
            output.append('\n');
        }

        return output.toString();
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    private static List<List<Integer>> freezeComponents(List<List<Integer>> components) {
        if (components == null) {
            throw new IllegalArgumentException("Components must not be null.");
        }

        List<List<Integer>> immutableComponents = new ArrayList<>(components.size());
        for (List<Integer> component : components) {
            if (component == null) {
                throw new IllegalArgumentException("Components must not contain null lists.");
            }
            immutableComponents.add(List.copyOf(component));
        }

        return Collections.unmodifiableList(immutableComponents);
    }
}
