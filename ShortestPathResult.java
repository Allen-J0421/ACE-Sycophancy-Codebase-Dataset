import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Encapsulates the complete result of a shortest path computation.
 *
 * Contains:
 * - Distance from source to each vertex
 * - Predecessor information for path reconstruction
 * - Cached paths for efficient repeated queries
 * - Reference to original graph
 *
 * Features:
 * - Lazy path computation via caching
 * - Unreachable node detection
 * - Path length and membership queries
 * - Bulk path retrieval
 *
 * Example:
 * {@code
 * ShortestPathResult result = solver.solve(graph, 0);
 * int distTo2 = result.getDistanceTo(2).orElse(-1);
 * Optional<Path> path = result.getPathTo(2);
 * if (path.isPresent() && path.get().contains(1)) {
 *     // Node 1 is on the shortest path to node 2
 * }
 * Map<Integer, Optional<Path>> allPaths = result.getAllPaths();
 * }
 *
 * @see Path
 * @see PathCache
 * @see DijkstraShortestPathSolver
 */
class ShortestPathResult {
    private final List<Integer> distances;
    private final int[] predecessors;
    private final int sourceNode;
    private final WeightedGraphView graph;
    private final PathCache pathCache;
    private static final int INFINITY = Integer.MAX_VALUE;

    private ShortestPathResult(List<Integer> distances, int[] predecessors,
                               int sourceNode, WeightedGraphView graph) {
        this.distances = Collections.unmodifiableList(new ArrayList<>(distances));
        this.predecessors = predecessors.clone();
        this.sourceNode = sourceNode;
        this.graph = graph;
        this.pathCache = new PathCache();
    }

    static ShortestPathResult of(List<Integer> distances, int[] predecessors,
                                 int sourceNode, WeightedGraphView graph) {
        return new ShortestPathResult(distances, predecessors, sourceNode, graph);
    }

    List<Integer> getDistances() {
        return distances;
    }

    Optional<Integer> getDistanceTo(int node) {
        if (node < 0 || node >= distances.size()) {
            return Optional.empty();
        }
        return Optional.of(distances.get(node));
    }

    Optional<Path> getPathTo(int destination) {
        if (pathCache.get(destination).isPresent()) {
            return pathCache.get(destination).flatMap(p -> p);
        }

        if (!isReachable(destination)) {
            pathCache.put(destination, Optional.empty());
            return Optional.empty();
        }

        var path = new LinkedList<Integer>();
        int current = destination;

        while (current != sourceNode) {
            path.addFirst(current);
            current = predecessors[current];
        }
        path.addFirst(sourceNode);

        Optional<Path> result = Optional.of(Path.of(path, getDistanceTo(destination).orElse(INFINITY)));
        pathCache.put(destination, result);
        return result;
    }

    int getSourceNode() {
        return sourceNode;
    }

    boolean isReachable(int node) {
        return getDistanceTo(node)
            .map(d -> !d.equals(INFINITY))
            .orElse(false);
    }

    Map<Integer, Optional<Path>> getAllPaths() {
        var allPaths = new java.util.LinkedHashMap<Integer, Optional<Path>>();
        IntStream.range(0, distances.size())
            .forEach(node -> allPaths.put(node, getPathTo(node)));
        return Collections.unmodifiableMap(allPaths);
    }

    @Override
    public String toString() {
        return String.format("ShortestPathResult(source=%d, distances=%s)", sourceNode, distances);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShortestPathResult)) return false;
        ShortestPathResult other = (ShortestPathResult) obj;
        return sourceNode == other.sourceNode && distances.equals(other.distances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceNode, distances);
    }
}
