import java.util.ArrayList;
import java.util.List;

public final class Dijkstra {
    private static final int SAMPLE_SOURCE = 0;

    private Dijkstra() {
        // Utility class.
    }

    public static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return shortestPaths(LegacyGraphAdapter.toGraph(adj), src);
    }

    public static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        LegacyGraphAdapter.addUndirectedEdge(adj, u, v, w);
    }

    public static ArrayList<Integer> shortestPaths(Graph graph, int source) {
        return ShortestPathSolver.solve(graph, source).toList();
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
        ArrayList<Integer> result = shortestPaths(SampleGraphs.weightedUndirectedExample(), SAMPLE_SOURCE);
        System.out.println(formatDistances(result));
    }
}
