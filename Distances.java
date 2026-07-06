import java.util.Arrays;

final class Distances implements ShortestPathResult {

    static final int UNREACHABLE = Integer.MAX_VALUE;
    static final int NO_PREDECESSOR = -1;

    private final int source;
    private final DistanceMap distanceMap;
    private final PathBuilder pathBuilder;

    Distances(int source, DistanceMap distanceMap, PredecessorMap predecessors) {
        this.source = source;
        this.distanceMap = distanceMap;
        this.pathBuilder = new PathBuilder(predecessors);
    }

    int source() { return source; }

    boolean isReachable(int vertex) {
        return distanceMap.get(vertex) != UNREACHABLE;
    }

    int distanceTo(int vertex) {
        return distanceMap.get(vertex);
    }

    Path pathTo(int target) {
        if (!isReachable(target)) return Path.none();
        return pathBuilder.build(target, distanceMap.get(target));
    }

    int[] all() {
        return distanceMap.snapshot();
    }

    int vertexCount() {
        return distanceMap.size();
    }

    @Override
    public <T> T accept(ResultVisitor<T> visitor) {
        return visitor.onDistances(this);
    }

    @Override
    public String toString() {
        return "Distances[source=" + source + ", distances=" + Arrays.toString(distanceMap.snapshot()) + "]";
    }
}
