import java.util.Arrays;

final class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int FROM = 0;
    private static final int TO = 1;
    private static final int WEIGHT = 2;
    private static final int EDGE_FIELD_COUNT = 3;

    private BellmanFord() {
    }

    static int[] bellmanFord(int vertexCount, int[][] edges, int src) {
        validateGraph(vertexCount, edges, src);

        int[] dist = new int[vertexCount];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int i = 1; i < vertexCount; i++) {
            if (!relaxEdges(edges, dist)) {
                break;
            }
        }

        if (hasReachableNegativeCycle(edges, dist)) {
            return new int[]{-1};
        }

        return dist;
    }

    private static boolean relaxEdges(int[][] edges, int[] dist) {
        boolean changed = false;

        for (int[] edge : edges) {
            int u = edge[FROM];
            int v = edge[TO];
            int wt = edge[WEIGHT];

            if (canRelax(dist, u, v, wt)) {
                dist[v] = dist[u] + wt;
                changed = true;
            }
        }

        return changed;
    }

    private static boolean hasReachableNegativeCycle(int[][] edges, int[] dist) {
        for (int[] edge : edges) {
            int u = edge[FROM];
            int v = edge[TO];
            int wt = edge[WEIGHT];

            if (canRelax(dist, u, v, wt)) {
                return true;
            }
        }

        return false;
    }

    private static boolean canRelax(int[] dist, int u, int v, int wt) {
        return dist[u] != INF && dist[u] + wt < dist[v];
    }

    private static void validateGraph(int vertexCount, int[][] edges, int src) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }

        if (src < 0 || src >= vertexCount) {
            throw new IllegalArgumentException("Source vertex is out of range");
        }

        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null");
        }

        for (int i = 0; i < edges.length; i++) {
            int[] edge = edges[i];
            if (edge == null || edge.length != EDGE_FIELD_COUNT) {
                throw new IllegalArgumentException("Edge " + i + " must contain from, to, and weight");
            }

            int u = edge[FROM];
            int v = edge[TO];
            if (u < 0 || u >= vertexCount || v < 0 || v >= vertexCount) {
                throw new IllegalArgumentException("Edge " + i + " references a vertex outside the graph");
            }
        }
    }

    public static void main(String[] args) {

        int vertexCount = 5;

        int[][] edges = new int[][] {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };

        int src = 0;

        int[] ans = bellmanFord(vertexCount, edges, src);

        for (int dist : ans) {
            System.out.print(dist + " ");
        }
    }
}
