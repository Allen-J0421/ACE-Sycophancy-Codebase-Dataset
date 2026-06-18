import java.util.ArrayList;
import java.util.List;

final class LegacyGraphAdapter {
    private static final int EDGE_DESTINATION_INDEX = 0;
    private static final int EDGE_WEIGHT_INDEX = 1;

    private LegacyGraphAdapter() {
    }

    static WeightedGraph toWeightedGraph(ArrayList<ArrayList<int[]>> adjacency) {
        validateAdjacencyList(adjacency);

        WeightedGraph.Builder builder = WeightedGraph.builder(adjacency.size());
        for (int from = 0; from < adjacency.size(); from++) {
            for (int[] edge : adjacency.get(from)) {
                validateEdge(adjacency.size(), edge);
                builder.addDirectedEdge(from, destination(edge), weight(edge));
            }
        }

        return builder.build();
    }

    static void addUndirectedEdge(ArrayList<ArrayList<int[]>> adjacency, int u, int v, int weight) {
        validateLegacyVertex(adjacency, u);
        validateLegacyVertex(adjacency, v);
        Edge.validateWeight(weight);

        adjacency.get(u).add(new int[]{v, weight});
        adjacency.get(v).add(new int[]{u, weight});
    }

    private static void validateAdjacencyList(ArrayList<ArrayList<int[]>> adjacency) {
        if (adjacency == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        for (List<int[]> edges : adjacency) {
            if (edges == null) {
                throw new IllegalArgumentException("Adjacency list cannot contain null vertex entries.");
            }
        }
    }

    private static void validateLegacyVertex(ArrayList<ArrayList<int[]>> adjacency, int vertex) {
        validateAdjacencyList(adjacency);
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IllegalArgumentException("Vertex is outside the graph.");
        }
    }

    private static void validateEdge(int vertexCount, int[] edge) {
        if (edge == null || edge.length < 2) {
            throw new IllegalArgumentException("Each edge must contain a destination and weight.");
        }
        if (destination(edge) < 0 || destination(edge) >= vertexCount) {
            throw new IllegalArgumentException("Edge destination is outside the graph.");
        }
        Edge.validateWeight(weight(edge));
    }

    private static int destination(int[] edge) {
        return edge[EDGE_DESTINATION_INDEX];
    }

    private static int weight(int[] edge) {
        return edge[EDGE_WEIGHT_INDEX];
    }
}
