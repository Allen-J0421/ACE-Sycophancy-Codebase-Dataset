import java.util.ArrayList;
import java.util.List;

public final class Dijkstra {
    private static final int SAMPLE_VERTEX_COUNT = 5;
    private static final int SAMPLE_SOURCE = 0;

    private Dijkstra() {
        // Utility class.
    }

    public static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return dijkstra(LegacyGraphAdapter.toGraph(adj), src);
    }

    public static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        LegacyGraphAdapter.addUndirectedEdge(adj, u, v, w);
    }

    private static ArrayList<Integer> dijkstra(Graph graph, int source) {
        return ShortestPathSolver.solve(graph, source).toList();
    }

    private static Graph createSampleGraph() {
        Graph graph = Graph.withVertexCount(SAMPLE_VERTEX_COUNT);

        graph.addUndirectedEdge(0, 1, 4);
        graph.addUndirectedEdge(0, 2, 8);
        graph.addUndirectedEdge(1, 4, 6);
        graph.addUndirectedEdge(1, 2, 3);
        graph.addUndirectedEdge(2, 3, 2);
        graph.addUndirectedEdge(3, 4, 10);

        return graph;
    }

    private static String formatDistances(List<Integer> distances) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < distances.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(distances.get(i));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        ArrayList<Integer> result = dijkstra(createSampleGraph(), SAMPLE_SOURCE);
        System.out.println(formatDistances(result));
    }
}
