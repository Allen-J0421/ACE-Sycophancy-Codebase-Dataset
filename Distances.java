import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Distances implements ShortestPathResult {

    static final int UNREACHABLE = Integer.MAX_VALUE;
    static final int NO_PREDECESSOR = -1;

    private final int source;
    private final int[] distances;
    private final int[] predecessors;

    Distances(int source, int[] distances, int[] predecessors) {
        this.source = source;
        this.distances = distances.clone();
        this.predecessors = predecessors.clone();
    }

    int source() { return source; }

    boolean isReachable(int vertex) {
        return distances[vertex] != UNREACHABLE;
    }

    int distanceTo(int vertex) {
        return distances[vertex];
    }

    Path pathTo(int target) {
        if (!isReachable(target)) return Path.none();

        List<Integer> path = new ArrayList<>();
        for (int v = target; v != NO_PREDECESSOR; v = predecessors[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return Path.of(path, distances[target]);
    }

    int[] all() {
        return distances.clone();
    }

    int vertexCount() {
        return distances.length;
    }

    @Override
    public String toString() {
        return "Distances[source=" + source + ", distances=" + Arrays.toString(distances) + "]";
    }
}
