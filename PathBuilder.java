import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PathBuilder {

    private final PredecessorMap predecessors;

    PathBuilder(PredecessorMap predecessors) {
        this.predecessors = predecessors;
    }

    Path build(int target, int totalWeight) {
        List<Integer> vertices = new ArrayList<>();
        for (int v = target; v != Distances.NO_PREDECESSOR; v = predecessors.predecessorOf(v)) {
            vertices.add(v);
        }
        Collections.reverse(vertices);
        return Path.of(vertices, totalWeight);
    }
}
