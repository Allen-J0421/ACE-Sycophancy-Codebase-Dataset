import java.util.Arrays;

final class Distances implements ShortestPathResult {

    static final int UNREACHABLE = Integer.MAX_VALUE;
    static final int NO_PREDECESSOR = -1;

    private final int source;
    private final int[] distances;
    private final PathBuilder pathBuilder;

    Distances(int source, int[] distances, int[] predecessors) {
        this.source = source;
        this.distances = distances.clone();
        this.pathBuilder = new PathBuilder(predecessors);
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
        return pathBuilder.build(target, distances[target]);
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
