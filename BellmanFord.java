import java.util.Arrays;

public class BellmanFord {

    private static final int INF = Integer.MAX_VALUE / 2;

    static class Edge {
        final int from, to, weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Returns shortest distances from src to all vertices.
     * Throws IllegalStateException if a negative-weight cycle is reachable from src.
     */
    public static int[] shortestPaths(int numVertices, Edge[] edges, int src) {
        int[] dist = new int[numVertices];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int i = 0; i < numVertices; i++) {
            for (Edge e : edges) {
                if (dist[e.from] < INF && dist[e.from] + e.weight < dist[e.to]) {
                    if (i == numVertices - 1) {
                        throw new IllegalStateException("Graph contains a negative-weight cycle");
                    }
                    dist[e.to] = dist[e.from] + e.weight;
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        int numVertices = 5;
        Edge[] edges = {
            new Edge(1, 3, 2),
            new Edge(4, 3, -1),
            new Edge(2, 4, 1),
            new Edge(1, 2, 1),
            new Edge(0, 1, 5)
        };
        int src = 0;

        int[] distances = shortestPaths(numVertices, edges, src);
        for (int d : distances) {
            System.out.print(d + " ");
        }
        System.out.println();
    }
}
