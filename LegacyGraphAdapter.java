import java.util.ArrayList;

final class LegacyGraphAdapter {
    private static final int DESTINATION_INDEX = 0;
    private static final int WEIGHT_INDEX = 1;
    private static final int EDGE_FIELD_COUNT = 2;

    private LegacyGraphAdapter() {
    }

    static void addUndirectedEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        adj.get(u).add(edge(v, w));
        adj.get(v).add(edge(u, w));
    }

    static Graph toGraph(ArrayList<ArrayList<int[]>> legacyAdjacency) {
        if (legacyAdjacency == null) {
            throw new IllegalArgumentException("legacyAdjacency must not be null");
        }

        Graph graph = Graph.withVertexCount(legacyAdjacency.size());
        for (int from = 0; from < legacyAdjacency.size(); from++) {
            for (int[] legacyEdge : legacyAdjacency.get(from)) {
                if (legacyEdge == null || legacyEdge.length < EDGE_FIELD_COUNT) {
                    throw new IllegalArgumentException("legacy edge must contain destination and weight");
                }
                graph.addDirectedEdge(from, legacyEdge[DESTINATION_INDEX], legacyEdge[WEIGHT_INDEX]);
            }
        }
        return graph;
    }

    private static int[] edge(int destination, int weight) {
        return new int[]{destination, weight};
    }
}
