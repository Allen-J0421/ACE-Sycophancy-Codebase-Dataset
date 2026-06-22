import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class KruskalMinimumSpanningTreeFinder {
    private KruskalMinimumSpanningTreeFinder() {
    }

    static MinimumSpanningTree find(Graph graph) {
        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        List<Edge> selectedEdges = new ArrayList<>();
        int totalWeight = 0;

        for (Edge edge : sortedEdgesByWeight(graph.edges())) {
            if (!disjointSet.union(edge.from(), edge.to())) {
                continue;
            }

            selectedEdges.add(edge);
            totalWeight += edge.weight();

            if (selectedEdges.size() == graph.requiredEdgeCount()) {
                return new MinimumSpanningTree(totalWeight, selectedEdges);
            }
        }

        MinimumSpanningTree spanningTree = new MinimumSpanningTree(totalWeight, selectedEdges);
        graph.validateSpanningTree(spanningTree);
        return spanningTree;
    }

    private static List<Edge> sortedEdgesByWeight(List<Edge> edges) {
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(Edge::weight));
        return sortedEdges;
    }
}
