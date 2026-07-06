import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PathBuilder {

    private final int[] predecessors;

    PathBuilder(int[] predecessors) {
        this.predecessors = predecessors.clone();
    }

    Path build(int target, int totalWeight) {
        List<Integer> vertices = new ArrayList<>();
        for (int v = target; v != Distances.NO_PREDECESSOR; v = predecessors[v]) {
            vertices.add(v);
        }
        Collections.reverse(vertices);
        return Path.of(vertices, totalWeight);
    }
}
