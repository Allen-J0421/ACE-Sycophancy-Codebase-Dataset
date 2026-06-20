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

    private static List<StronglyConnectedComponent> freezeComponents(
        List<StronglyConnectedComponent> components
    ) {
        if (components == null) {
            throw new IllegalArgumentException("Components must not be null.");
        }

        for (StronglyConnectedComponent component : components) {
            if (component == null) {
                throw new IllegalArgumentException("Components must not contain null values.");
            }
        }

        return List.copyOf(components);
    }
}
