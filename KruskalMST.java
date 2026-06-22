import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class KruskalMST {
    private KruskalMST() {
        // Utility class.
    }

    public static int kruskalsMST(int vertexCount, int[][] rawEdges) {
        return minimumSpanningTree(Graph.fromEdgeMatrix(vertexCount, rawEdges)).totalWeight();
    }

    static MinimumSpanningTreeResult minimumSpanningTree(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        List<Edge> sortedEdges = new ArrayList<>(graph.edges());
        Collections.sort(sortedEdges);

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

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10},
            {1, 3, 15},
            {2, 3, 4},
            {2, 0, 6},
            {0, 3, 5}
        };

        MinimumSpanningTreeResult result = minimumSpanningTree(Graph.fromEdgeMatrix(4, edges));
        System.out.println(result.totalWeight());
        System.out.println(result.isConnected());
    }
}
