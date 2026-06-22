import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class KruskalMinimumSpanningTreeCalculator implements MinimumSpanningTreeCalculator {
    @Override
    public MstResult compute(Graph graph) {
        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        int totalWeight = 0;
        int edgesUsed = 0;

        for (Edge edge : sortedEdgesByWeight(graph.edges())) {
            if (!disjointSet.union(edge.from(), edge.to())) {
                continue;
            }

            totalWeight += edge.weight();
            edgesUsed++;

            if (edgesUsed == graph.requiredEdgeCount()) {
                return new MstResult(totalWeight, edgesUsed);
            }
        }

        if (!graph.canBeSpannedWith(edgesUsed)) {
            throw new IllegalArgumentException("Input graph must be connected to form an MST.");
        }

        return new MstResult(totalWeight, edgesUsed);
    }

    private List<Edge> sortedEdgesByWeight(List<Edge> edges) {
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(Edge::weight));
        return sortedEdges;
    }
}
