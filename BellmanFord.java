import java.util.Arrays;
import java.util.List;

public class BellmanFord {

    private static final int INF = Integer.MAX_VALUE / 2;

    public static class Edge {
        public final int from, to, weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Returns shortest distances from src to all vertices.
     *
     * @throws IllegalArgumentException if src is out of range or numVertices is non-positive
     * @throws IllegalStateException    if a negative-weight cycle is reachable from src
     */
    public static int[] shortestPaths(int numVertices, List<Edge> edges, int src) {
        if (numVertices <= 0) throw new IllegalArgumentException("numVertices must be positive");
        if (src < 0 || src >= numVertices) throw new IllegalArgumentException("src out of range: " + src);

        int[] dist = new int[numVertices];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // V-1 passes guarantee shortest paths when no negative cycle exists:
        // the longest simple path in a V-vertex graph has at most V-1 edges.
        for (int i = 0; i < numVertices - 1; i++) {
            for (Edge e : edges) {
                if (dist[e.from] < INF && dist[e.from] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[e.from] + e.weight;
                }
            }
        }

        // A Vth relaxation succeeding means distances can still decrease, indicating a cycle.
        for (Edge e : edges) {
            if (dist[e.from] < INF && dist[e.from] + e.weight < dist[e.to]) {
                throw new IllegalStateException("Graph contains a negative-weight cycle");
            }
        }

        return dist;
    }

    public static void main(String[] args) {
        int numVertices = 5;
        List<Edge> edges = List.of(
            new Edge(1, 3, 2),
            new Edge(4, 3, -1),
            new Edge(2, 4, 1),
            new Edge(1, 2, 1),
            new Edge(0, 1, 5)
        );

        int[] distances = shortestPaths(numVertices, edges, 0);
        for (int v = 0; v < distances.length; v++) {
            System.out.println("Vertex " + v + ": " + distances[v]);
        }
    }
}
