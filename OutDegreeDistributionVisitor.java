import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link GraphVisitor} that computes the out-degree distribution of a graph:
 * how many vertices have each out-degree. Run it with
 * {@link GraphTraversal#visitAll}, then read {@link #distribution()}.
 *
 * <p>Vertices with out-degree zero are included, so the counts always sum to the
 * graph's vertex count.
 */
final class OutDegreeDistributionVisitor implements GraphVisitor {

    private final Map<Integer, Integer> outDegreeByVertex = new HashMap<>();

    @Override
    public void visitVertex(int vertex) {
        outDegreeByVertex.putIfAbsent(vertex, 0);
    }

    @Override
    public void visitEdge(int from, int to) {
        outDegreeByVertex.merge(from, 1, Integer::sum);
    }

    /**
     * Returns a map from out-degree to the number of vertices having that
     * out-degree, ordered by ascending degree.
     */
    Map<Integer, Integer> distribution() {
        Map<Integer, Integer> distribution = new TreeMap<>();
        for (int outDegree : outDegreeByVertex.values()) {
            distribution.merge(outDegree, 1, Integer::sum);
        }
        return distribution;
    }
}
