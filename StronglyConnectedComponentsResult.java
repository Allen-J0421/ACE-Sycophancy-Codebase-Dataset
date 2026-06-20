import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StronglyConnectedComponentsResult {
    private final List<StronglyConnectedComponent> components;

    public StronglyConnectedComponentsResult(List<StronglyConnectedComponent> components) {
        this.components = Collections.unmodifiableList(new ArrayList<>(components));
    }

    public List<StronglyConnectedComponent> getComponents() {
        return components;
    }

    public int count() {
        return components.size();
    }
}
