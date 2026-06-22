import java.util.ArrayList;
import java.util.List;

final class KruskalMST {
    private KruskalMST() {
        // Utility class.
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        return minimumSpanningTreeCost(Graph.fromEdgeMatrix(vertexCount, rawEdges));
    }

    static int minimumSpanningTreeCost(Graph graph) {
        return findMinimumSpanningTree(graph).totalWeight();
    }

    static MinimumSpanningTreeResult findMinimumSpanningTree(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        List<Edge> sortedEdges = graph.edges().stream()
            .sorted()
            .toList();

        DisjointSet disjointSet = new DisjointSet(graph.vertexCount());
        List<Edge> selectedEdges = new ArrayList<>();
        int totalWeight = 0;

        for (Edge edge : sortedEdges) {
            if (disjointSet.union(edge.from(), edge.to())) {
                selectedEdges.add(edge);
                totalWeight += edge.weight();
                if (selectedEdges.size() == graph.vertexCount() - 1) {
                    break;
                }
            }
        }

        return new MinimumSpanningTreeResult(
            totalWeight,
            selectedEdges,
            graph.vertexCount() == 0 || selectedEdges.size() == graph.vertexCount() - 1
        );
    }
}
