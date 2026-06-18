import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TopologicalOrder {
    private final List<Integer> vertices;

    TopologicalOrder(List<Integer> vertices) {
        this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
    }

    public List<Integer> vertices() {
        return vertices;
    }
}
